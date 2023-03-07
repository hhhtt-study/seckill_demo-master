package com.example.seckilldemo.config;


import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.service.UserService;
import com.example.seckilldemo.utils.CookieUtil;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            User user = getUser(request,response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null)
                return true;
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if(user==null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                key+=":"+user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            //限制访问次数，5秒内访问5次
            String uri = request.getRequestURI();
            Integer count = (Integer) valueOperations.get(key);
            if (count==null){
                valueOperations.set(key,1,second, TimeUnit.SECONDS);
            }else if(count<maxCount){
                valueOperations.increment(key);
            }else {
                render(response,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    /**
     * 构建返回对象
     * @param response
     * @param sessionError
     */
    private void render(HttpServletResponse response, RespBeanEnum sessionError) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(sessionError);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }

    /**
     * 获取当前登录用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "token");
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        return userService.getUserByCookie(userTicket, request, response);
    }
}
