package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口，站在“使用者”的角度来实现设计接口（这部分不用关注太多细节）
 * 三个方面：方法定义粒度，参数设置，以及返回类型（有可能是异常，如：不能重复秒杀，秒杀已经关闭）
 * @author 光玉
 * @create 2018-05-12 18:44
 **/
public interface SeckillService {
    /**
     * 查询单个秒杀商品信息
     * @param seckillId     通过id查询
     * @return              返回对应的商品信息
     */
    public Seckill getSeckillById(long seckillId) throws Exception;

    /**
     * 查询所有秒杀商品信息
     * @return              返回所有商品信息
     */
    public List<Seckill> getSeckillList() throws Exception;

    /**
     * 功能是：秒杀开始时，输出秒杀接口的地址；秒杀还没开始时，输出系统时间和秒杀开始时间
     * @param seckillId     通过id可以获取秒杀开始时间，与系统时间进行比较
     * @return              秒杀开始时，输出秒杀接口的地址；秒杀还没开始时，输出系统时间和秒杀开始时间
     */
    public Exposer exportSeckillUrl(long seckillId) throws Exception;

    /**
     * 执行秒杀操作
     * @param seckillId         商品id
     * @param userPhone         用户手机号
     * @param md5               用来比对的md5
     * @return                  返回秒杀成功或失败信息
     * throws Exception         需要根据对抛出的异常进行处理（如：用户不能重复秒杀的异常）
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws Exception,SeckillException,RepeatKillException,SeckillCloseException;

    /**
     * 通过sql包下的seckill.sql存储过程实现优化操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws Exception
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
