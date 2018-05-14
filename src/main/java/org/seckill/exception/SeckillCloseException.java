package org.seckill.exception;

/**
 * 秒杀关闭异常（当库存为0，或者秒杀时间已过，秒杀关闭，用户无法再进行秒杀操作）
 * @author 光玉
 * @create 2018-05-12 19:47
 **/
public class SeckillCloseException extends SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
