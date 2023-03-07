package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.mapper.UserMapper;
import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.service.UserService;
import com.example.seckilldemo.utils.CookieUtil;
import com.example.seckilldemo.utils.MD5Util;
import com.example.seckilldemo.utils.UUIDUtil;
import com.example.seckilldemo.vo.LoginVo;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sht
 * @since 2023-02-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        /*if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/
        User user = userMapper.selectById(mobile);
        if(user==null){
            throw  new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
            throw  new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        System.out.println("?????");
        //redis专门处理String类型
        redisTemplate.opsForValue().set("token:"+token,user);
        System.out.println("********");
//        request.getSession().setAttribute(token,user);
        CookieUtil.setCookie(request,response,"token",token);
        return RespBean.success(token);
    }

    @Override
    public User getUserByCookie(String userToken, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(userToken)){
            return  null;
        }
        User user= (User) redisTemplate.opsForValue().get("token:"+userToken);
        if(user!=null){
            CookieUtil.setCookie(request,response,"token",userToken);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userToken, String password,HttpServletRequest request , HttpServletResponse response) {
        User user = getUserByCookie(userToken, request, response);
        if (user==null){
            throw new GlobalException(RespBeanEnum.MOBILE_ERROR);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password,user.getSalt()));
        int result = userMapper.updateById(user);
        if(result==1){
            //删除redis
            redisTemplate.delete("token:"+userToken);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }


}
