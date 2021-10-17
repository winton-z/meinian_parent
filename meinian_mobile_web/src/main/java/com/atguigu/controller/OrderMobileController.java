package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.constant.RedisMessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.service.OrderService;
import com.atguigu.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderMobileController {

    @Reference
    OrderService orderService;

    @Autowired
    JedisPool jedisPool;

    @RequestMapping("/findById")
    public Result findById(Integer orderId){
        try {
            Map<String,Object> map = orderService.findById(orderId);
            Date date = (Date) map.get("orderDate");
            map.put("orderDate", DateUtils.parseDate2String(date));
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }


    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){
        try {
            System.out.println(" map= " + map);
            //获取手机号
            String telephone = (String) (map.get("telephone"));
            //获取验证码
            String validateCode =(String)(map.get("validateCode"));
            //从Redis中获取缓存的验证码，key为手机号+RedisConstant.SENDTYPE_ORDER
            String redisCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);

            if (redisCode == null || !redisCode.equals(validateCode)){
                return  new Result(false,MessageConstant.VALIDATECODE_ERROR);
            }

            Result result = orderService.saveOrder(map);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDER_SUCCESS);
        }
    }
}
