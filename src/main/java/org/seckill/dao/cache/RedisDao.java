package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 使用redis缓存优化
 * @author 光玉
 * @create 2018-05-15 9:22
 **/
public class RedisDao {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;    // 类似于数据库连接池

    public RedisDao(String ip, int port) {
        this.jedisPool = new JedisPool(ip, port);
    }

    // 使用protostuff动态生成对应类的schema
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(long seckillId) {
        // redis操作逻辑，从cache中获取seckill秒杀商品对象
        try {
            Jedis jedis = jedisPool.getResource();  // jedis相当于数据库的连接
            try {
                String key = "seckill:" + seckillId;
                // 使用protostuff将对象序列化，存储到缓存中
                // 取出对象时将对应的二进制数组反序列化，得到对象
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {  // 如果获取到的字节码不为空
                    Seckill seckill = schema.newMessage();              // 根据schema创建空对象
                    ProtobufIOUtil.mergeFrom(bytes, seckill, schema);     // 将信息赋给seckill对象，即反序列化过程
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill) {
        // 如果seckill对象不在缓存中，则将对象放到缓存中
        // 将对象序列化，对应的字节数组保存到redis中
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 超时缓存
                int timeout = 60 * 60;                                           // 缓存一小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
