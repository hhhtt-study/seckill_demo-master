package com.example.seckilldemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.seckilldemo.config.AccessLimit;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.pojo.Order;
import com.example.seckilldemo.pojo.SeckillMessage;
import com.example.seckilldemo.pojo.SeckillOrder;
import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.rabbitmq.MQSender;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.OrderService;
import com.example.seckilldemo.service.SeckillOrderService;
import com.example.seckilldemo.utils.JsonUtil;
import com.example.seckilldemo.utils.UUIDUtil;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /**
     * 秒杀
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_ERROR.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        SeckillOrder secKillOner = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (secKillOner != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }

    /**
     * 秒杀
     *
     * @param
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable("path")String path, User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check =orderService.checkPath(user,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //默认redis缓存失效时间大于秒杀时间，这样才能在redis中判断是否多买
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记，减少Redis的访问
        if(EmptyStockMap.get(goodsId))
            return RespBean.error(RespBeanEnum.EMPTY_ERROR);

        //加入分布式锁，防止中间发送错误导致无法删除锁，设置自动删除时间，并用Lua脚本检测是否为本线程设置的锁(未实现lua
        String lockPass = UUID.randomUUID().toString();
        Boolean isLock= valueOperations.setIfAbsent("user:"+user.getId()+":"+goodsId,lockPass,60, TimeUnit.SECONDS);
        if(!isLock)
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);

        //如果value是数值类型，原子性调用减一
        //stock预减库存
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        Long stock= (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:"+goodsId),
                Collections.EMPTY_LIST);
        if (stock < 0) {
            EmptyStockMap.put(goodsId,true);
//            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_ERROR);
        }
        SeckillMessage message = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(message));
        return RespBean.success(0);


        /*GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_ERROR);
        }
        //判断是否重复抢购
        *//*SeckillOrder secKillOner = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(secKillOner!=null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }*//*
        //默认redis缓存失效时间大于秒杀时间，这样才能在redis中判断是否多买
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order =orderService.seckill(user,goods);
        return RespBean.success(order);*/
    }


    /**
     *
     * @param user
     * @param goodsId
     * @return orderId:成功，-1：秒杀失败，0：秒杀中
     */
    @RequestMapping(value = "/getResult", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if(user==null)
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user,Long goodsId,String captcha,HttpServletRequest request){
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        boolean check =orderService.checkCaptcha(user,goodsId,captcha);
        if(!check)
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    @ResponseBody
    public void vertifyCode(User user, Long goodsId, HttpServletResponse response){
        if(user==null||goodsId<0)
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");
        response.setDateHeader("Expires",0);
        //生成验证码，讲结果存入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300,TimeUnit.SECONDS);
        try{
            captcha.out(response.getOutputStream());
        }catch (IOException e){
            log.error("验证码生成失败",e.getMessage());
        }

    }


    /**
     * 系统初始化，把商品库存数量加载到Redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo ->
        {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
   