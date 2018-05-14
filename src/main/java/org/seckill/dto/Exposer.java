package org.seckill.dto;


/**
 * 用来暴露秒杀url的DTO（数据传输层）
 * Exposer中的数据与业务没有多大关系，因此在这里编写，提供给SeckillService业务层的exportSeckillUrl方法调用
 * @author 光玉
 * @create 2018-05-12 18:59
 **/
public class Exposer {
    private boolean exposed;        // 表示开启秒杀的标志位
    private String md5;             // 使用md5算法加密
    private long seckillId;         // 秒杀商品id
    private long now;               // 当前系统时间，用毫秒表示
    private long start;             // 秒杀开始时间
    private long end;               // 秒杀结束时间

    public Exposer(){}

    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed,long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
