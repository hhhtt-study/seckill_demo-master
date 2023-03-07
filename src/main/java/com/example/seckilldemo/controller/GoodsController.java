package com.example.seckilldemo.controller;

import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.UserService;
import com.example.seckilldemo.vo.DetailVo;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8", method = RequestMethod.GET)
    @ResponseBody
    public String toList(Model model,User user, HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
//        if(StringUtils.isEmpty(token)){
//            return "login";
//        }
////        User user = (User) session.getAttribute(token);
//        User user = userService.getUserByCookie(token,request,response);
//        if(user == null){
//            return "login";
//        }
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
//        return "goodsList";
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html=thymeleafViewResolver.getTemplateEngine().process("goodsList",context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }
        return  html;
    }

    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8", method = RequestMethod.GET)
    @ResponseBody
    public String toDetail2(Model model,User user,@PathVariable("goodsId") Long goodsId, HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:"+goodsId);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus =0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);
        }else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            secKillStatus = 1;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods",goodsVo);
        model.addAttribute("user",user);
//        return "goodsDetail";
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html=thymeleafViewResolver.getTemplateEngine().process("goodsDetail",context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:"+goodsId,html,60, TimeUnit.SECONDS);
        }
        return html;
    }


    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(Model model,User user,@PathVariable("goodsId") Long goodsId, HttpServletRequest request, HttpServletResponse response){

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus =0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);
        }else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            secKillStatus = 1;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSeckillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return RespBean.success(detailVo);
    }

}
