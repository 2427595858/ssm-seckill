package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private RedisDao redisDao;

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
        // 优化点，缓存优化，超时基础上维护一致性
        Seckill seckill = redisDao.getSeckill(seckillId);       // 从缓存中获取对象
        if (seckill == null) {                                  // 如果缓存中没有该对象
            seckill = seckillDao.queryById(seckillId);          // 则从数据库中获取
            if (seckill == null) {                              // 获取的秒杀商品对象为空时
                return new Exposer(false, seckillId);
            } else {                                             // 对象不为空时
                redisDao.putSeckill(seckill);                   // 将对象放到redis中
            }
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
            // 这里可以进行优化，即先insert，再update，这样就减少了行级锁的执行次数（重复秒杀的不会执行update，行级锁也就不会发生）
            // 也就缩短了执行时间

            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                // 表示该用户已秒杀过，不能再进行秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                int updateCount = seckillDao.reduceNumber(seckillId, new Date());
                // 减库存
                if (updateCount <= 0) {
                    // 没有更新到记录中，秒杀已经结束
                    throw new SeckillCloseException("seckill is closed");
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

    /**
     * 调用存储过程来实现秒杀业务逻辑
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();             // 秒杀时间为当前系统时间
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("userPhone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);

        try {
            // 执行存储过程，使用map可以比较方便的保存和获取result的值
            seckillDao.killByProcedure(map);
            // 使用MapUtils（需要引入common-collections包）获取result的值
            // 当result为null时，可以赋默认值为-2，表示系统错误
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {   // 秒杀成功
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
            } else {    // 秒杀失败，根据result值返回对应错误状态
                System.out.println("result = "+ result);
                System.out.println(SeckillStateEnum.stateOf(result));
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);    // 系统内部错误
        }
    }
}
