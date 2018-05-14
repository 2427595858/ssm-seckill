package org.seckill.dao;

import org.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * 秒杀成功信息表DAO层编写
 * - 插入秒杀成功信息
 * - 根据id和phone查找相关信息
 * @author 光玉
 * @create 2018-05-10 19:14
 **/
public interface SuccessKilledDao {

    /**
     * 插入购买明细,可过滤重复
     * @param seckillId
     * @param userPhone
     * @return插入的行数
     */
    public int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone)throws Exception;


    /**
     * 根据秒杀商品的id查询明细SuccessKilled对象(该对象携带了Seckill秒杀产品对象)
     * @param seckillId
     * @return
     */
    public SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone)throws Exception;

}
