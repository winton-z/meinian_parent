package com.atguigu.controller;

import com.atguigu.constant.MessageConstant;
import com.atguigu.constant.RedisMessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.util.SMSUtils;
import com.atguigu.util.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;


@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    JedisPool jedisPool;




    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        try {
            //1.生成4位数密码
            Integer code = ValidateCodeUtils.generateValidateCode(4);
            //发送短信
            SMSUtils.sendShortMessage(telephone,code.toString());
            //将生成的验证码缓存到redis
            jedisPool.getResource().setex(telephone + RedisMessageConstant.SENDTYPE_LOGIN,5*60,code.toString());
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }


    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){

        try {
            //1.生成4位验证码
            Integer code = ValidateCodeUtils.generateValidateCode(4);
            //2.发送验证码到手机号
            SMSUtils.sendShortMessage(telephone,code.toString());
            //3.将验证码存储到redis 中，进行后期验证
            //验证码5分钟有效
            jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,5*60,code.toString());
            System.out.println("tel: " + telephone + "code: " +code);
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }

    }
}
