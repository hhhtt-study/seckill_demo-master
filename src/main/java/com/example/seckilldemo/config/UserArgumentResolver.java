package com.example.seckilldemo.config;


import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.service.UserService;
import com.example.seckilldemo.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 自定义用户参数
 *
 * @author: LC
 * @date 2022/3/3 4:46 下午
 * @ClassName: UserArgumentResolver
 */

//自定义参数解析器，解析User参数
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //当参数上有 @User 时才使用该拦截器
        Class<?> parameterType = parameter.getParameterType();
        return parameterType == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        return UserContext.getUser();
    }

}