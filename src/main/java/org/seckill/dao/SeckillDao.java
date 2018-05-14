package org.seckill.dao;

import org.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 秒杀商品表DAO层编写
 * - 根据id减少商品数量
 * - 根据id查找相应商品
 * - 根据偏移量查询商品列表（相当于分页显示，使用mysql的limit语句）
 * @author 光玉
 * @create 2018-05-10 18:56
 **/
public interface SeckillDao
{

    /**
     * 减库存
     * @param seckillId
     * @param killTime          表示秒杀时间，需要和开始时间startTime比较，当startTime<=killTime时，才可以秒杀
     * @return 如果影响行数>1，表示更新库存的记录行数
     */
    public int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime)throws Exception;

    /**
     * 根据id查询秒杀的商品信息
     * @param seckillId
     * @return
     */
    public Seckill queryById(long seckillId) throws Exception;

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    public List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit)throws Exception;



}
