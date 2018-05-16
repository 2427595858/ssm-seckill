package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 编写处理器来调用service业务层以及和前端进行交互
 * 异常在controller中处理，更规范的做法是使用springMVC的全局异常处理，不需要在每个方法中都try-catch
 * @author 光玉
 * @create 2018-05-13 15:33
 **/
@Controller
@RequestMapping("/seckill")     // 相当于一个总的前缀，这个模块下的url格式为：/seckill/**
public class SeckillController {
    // 定义日志文件
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    // 秒杀商品列表页
    @RequestMapping(value = "/list", method = RequestMethod.GET)     // 只能是GET请求
    public String list(Model model) throws Exception {
        // 使用model来设置参数，返回视图
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("seckillList", seckillList);
        return "list";          // 前缀和后缀在spring-web.xml中已配好，实际路径是“/WEB-INF/jsp/list.jsp”
    }

    // 秒杀商品详情页
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) throws Exception {
        // 这里传递的seckillId使用封装类型Long，好处就是默认是null值
        if (seckillId == null) {
            return "redirect:/seckill/list";    // 重定向到商品列表页面（地址栏url改变）
        }
        Seckill seckill = seckillService.getSeckillById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";     // 转发到商品列表页面（地址栏url不变）
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // ajax，json暴露秒杀接口的方法
    @RequestMapping(value = "/{seckillId}/exposer",
                    method = RequestMethod.GET,
                    produces = {"application/json;charset=UTF-8"})
    @ResponseBody       // 返回json类型数据
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try{
            Exposer exposer=seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<>(true,exposer);             // 暴露接口成功
        }catch (Exception e){
            //logger.error(e.getMessage(),e);                                  // 打印日志
            result=new SeckillResult<>(false,e.getMessage());       // 暴露接口失败
        }
        return result;
    }

    // 执行秒杀操作
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
                        method = RequestMethod.POST,
                        produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "userPhone",required = false) Long userPhone){
        if(userPhone == null){  // 验证用户手机号是否为空
            return new SeckillResult<>(false,"未注册");
        }
        try{
            // SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
            // 通过存储过程调用
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, userPhone, md5);
            return new SeckillResult<>(true, execution);
        }catch (RepeatKillException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<>(true, execution);
        }catch (SeckillCloseException e){
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<>(true, execution);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<>(true, execution);
        }
    }

    // 获取当前系统时间
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<>(true, now.getTime());
    }
}
