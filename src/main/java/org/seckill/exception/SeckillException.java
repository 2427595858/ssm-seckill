package org.seckill.exception;

/**
 * 秒杀相关业务异常
 * @author 光玉
 * @create 2018-05-12 19:51
 **/
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
