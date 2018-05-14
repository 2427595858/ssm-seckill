package org.seckill.exception;

/**
 * 处理重复秒杀异常（运行期异常）
 * @author 光玉
 * @create 2018-05-12 19:41
 **/
public class RepeatKillException extends SeckillException{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
