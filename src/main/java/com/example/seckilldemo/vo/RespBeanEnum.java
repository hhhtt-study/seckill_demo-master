package com.example.seckilldemo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务器异常"),
    //登录模块
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    MOBILE_NOT_EXIST(500213,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214,"密码更新失败"),
    SESSION_ERROR(500215,"用户不存在"),
    //秒杀模块
    EMPTY_ERROR(500500,"库存不足"),
    REPEATE_ERROR(500501,"该商品每人限购一件"),
    REQUEST_ILLEGAL(500502,"请求非法,请重新尝试"),
    ERROR_CAPTCHA(500503,"验证码错误请重新输入"),
    ACCESS_LIMIT_REACHED(500504,"访问过于频繁，请稍候再试"),
    //订单模块5005XX
    ORDER_NOT_EXIST(500300,"订单信息不存在"),
    ;

    private final Integer code;
    private final String message;

}
