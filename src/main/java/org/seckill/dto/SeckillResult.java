package org.seckill.dto;

/**
 * 将所有ajax返回类型，全部封装为json数据
 * @author 光玉
 * @create 2018-05-13 16:14
 **/
public class SeckillResult<T> {
    private boolean success;        // 成功标志
    private T data;                 // 成功时返回的数据
    private String error;           // 失败时返回的错误信息

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
