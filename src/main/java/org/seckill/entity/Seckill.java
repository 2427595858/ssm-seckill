package org.seckill.entity;

import java.util.Date;

/**
 * 编写数据库seckill表对应pojo类seckill
 * @author 光玉
 * @create 2018-05-10 17:19
 **/
public class Seckill {
    private long seckillId;      // 秒杀商品id
    private String name;        // 秒杀商品名称
    private int number;         // 商品数量
    private Date startTime;     // 秒杀开始时间
    private Date endTime;       // 秒杀结束时间
    private Date createTime;    // 创建时间

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Seckill{" +
                "seckillId=" + seckillId +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                '}';
    }
}
