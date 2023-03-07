package com.example.seckilldemo.controller;


import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.rabbitmq.MQSender;
import com.example.seckilldemo.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sht
 * @since 2023-02-12
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;


    /**
     * 用户信息(测试)
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

    /**
     * 测试发送RabbitMQ消息
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }
}

