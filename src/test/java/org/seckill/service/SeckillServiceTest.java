package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    // 定义日志文件
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillById() throws Exception {
        long id = 1L;
        Seckill seckill = seckillService.getSeckillById(id);
        System.out.println(seckill);
//        // 将对应信息通过日志文件输出，这里设置的是输出到控制台中
//        logger.info("seckill={}",seckill);
    }

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        for (Seckill s : seckillList) {
            System.out.println(s);
        }
//        logger.info("seckillList={}",seckillList);
    }

    @Test       // 完整的测试代码，即将exportSeckillUrl()方法与executeSeckill()方法结合起来测试
    public void SeckillLogic() throws Exception {
        long id = 1L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){    // 如果现在正是秒杀时间段
            System.out.println(exposer);
//        logger.info("exposer={}",exposer);

            long userPhone = 15830232333L;
            String md5 = exposer.getMd5();
            try{
                SeckillExecution seckillExecution = seckillService.executeSeckill(id,userPhone,md5);
                System.out.println(seckillExecution);
                logger.info("seckillExecution={}",seckillExecution);
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());   // 表示不向上抛出个junit，自行输出错误信息
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            }
        } else {
            // 秒杀未开启
            logger.warn("exposer={}",exposer);
        }

    }

//    @Test     // 将两个测试方法结合，不需要这个测试方法
//    public void executeSeckill() throws Exception{
//        long id = 1L;
//        long userPhone = 13623232333L;
//        String md5 = "225b3fa515b9574b55bb5bb14f1fc0c7";
//        try{
//            SeckillExecution seckillExecution = seckillService.executeSeckill(id,userPhone,md5);
//            System.out.println(seckillExecution);
//            logger.info("seckillExecution={}",seckillExecution);
//        } catch (SeckillCloseException e) {
//            logger.error(e.getMessage());
//        } catch (RepeatKillException e) {
//            logger.error(e.getMessage());
//        }
//    }
}