package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
/**
 * 配置spring和junit整合，这样junit在启动时就会加载spring容器
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception{
        /**
         * 第一次执行时信息插入成功，返回“successCount=1”
         * 第二次执行时，由于设置了不允许重复秒杀，信息插入失败，返回“successCount=0”
         */
        long id=3L;
        long userPhone=13823232333L;
        int successCount = successKilledDao.insertSuccessKilled(id,userPhone);
        System.out.println("successCount="+successCount);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception{
        long id=3L;
        long userPhone=13823232333L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,userPhone);
        System.out.println(successKilled);                  // 输出successKilled信息
        System.out.println(successKilled.getSeckill());     // 输出对应的seckill信息（根据id串联起来）
    }
}