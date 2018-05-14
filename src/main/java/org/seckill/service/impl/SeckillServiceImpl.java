package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * 实现SeckillService的类
 * @author 光玉
 * @create 2018-05-12 20:00
 **/
@Service
public class SeckillServiceImpl implements SeckillService {
    // 设置日志文件（slf4j日志）
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    // 注入service依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    // md5的盐值，用于md5加密（越复杂越好）
    private final String salt = "jsisufdl8&&%%@22";

    /**
     *
     * @param seckillId     通过id查询
     * @return
     * @throws Exception
     */
    @Override
    public Seckill getSeckillById(long seckillId) throws Exception {
        return seckillDao.queryById(seckillId);
    }

    /**
     * 返回全部秒杀商品信息
     * @return
     * @throws Exception
     */
    @Override
    public List<Seckill> getSeckillList() throws Exception{
        return seckillDao.queryAll(0,4);
    }

    /**
     * 需要进行判断
     * @param seckillId     通过id可以获取秒杀开始时间，与系统时间进行比较
     * @return
     * @throws Exception
     */
    @Override
    public Exposer exportSeckillUrl(long seckillId) throws Exception {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {     // 获取的秒杀商品对象为空时
            return new Exposer(false, seckillId);
        }

        Date startTime = seckill.getStartTime();      // 获取秒杀开始时间
        Date endTime = seckill.getEndTime();          // 获取秒杀结束时间
        Date now = new Date();                        // 获取当前系统时间
        if (now.getTime() < startTime.getTime() || now.getTime() > endTime.getTime()) {
            // 当前系统时间没在秒杀时间范围内时
            return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
        }

        String md5 = getMd5(seckillId);               // 通过getMd5方法获取md5的值
        return new Exposer(true, md5, seckillId);
    }

    // 进行md5加密的方法。exportSeckillUrl方法和executeSeckill方法都需要用到
    private String getMd5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());     // 通过org.springframework.util的一个工具类得到md5
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务的优点
     * - 开发团队达成一致约定，明确表明事务方法的编程风格
     * - 保证事务方法的时间尽可能短，不要穿插其他网络操作（如RPC/HTTP请求），要将这些操作剥离到事务方法外部
     * - 不是所有方法都需要事务，如：只有一条添加/修改/删除操作，只读操作不要事务控制
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws Exception, SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {          // 如果md5的值为空，或者md5的值不匹配
            throw new SeckillException("seckill data rewrite");     // 抛出相应异常
        }
        try {
            // 执行秒杀逻辑，也就是之前所说的减库存，记录购买行为
            int updateCount = seckillDao.reduceNumber(seckillId, new Date());

            // 减库存
            if (updateCount <= 0) {
                // 没有更新到记录中，秒杀已经结束
                throw new SeckillCloseException("seckill is closed");
            } else {
                // 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    // 表示该用户已秒杀过，不能再进行秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    // 秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
                throw e1;
        } catch (RepeatKillException e2) {
                throw e2;
        } catch (Exception e){
            logger.error(e.getMessage(),e);
            // 将编译器异常转换为运行期异常
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }

    }
}
