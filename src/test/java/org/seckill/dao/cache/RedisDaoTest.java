package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
    private long id = 1L;
    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void testSeckill() throws Exception{
        // 综合测试RedisDao中的两个方法
        Seckill seckill = redisDao.getSeckill(id);  // 从缓存中获取对象
        if(seckill == null){                        // 如果缓存中没有该对象
            seckill = seckillDao.queryById(id);     // 则从数据库中获取
            if(seckill != null){
                String result = redisDao.putSeckill(seckill);       // 将对象放到缓存中
                System.out.println("result = " + result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }
}