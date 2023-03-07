package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.mapper.OrderMapper;
import com.example.seckilldemo.pojo.Order;
import com.example.seckilldemo.pojo.SeckillGoods;
import com.example.seckilldemo.pojo.SeckillOrder;
import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.OrderService;
import com.example.seckilldemo.service.SeckillGoodsService;
import com.example.seckilldemo.service.SeckillOrderService;
import com.example.seckilldemo.utils.MD5Util;
import com.example.seckilldemo.utils.UUIDUtil;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.OrderDetailVo;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sht
 * @since 2023-02-14
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //秒杀商品减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
//        seckillGoodsService.updateById(seckillGoods);*/
        //这里是将修改库存和判断库存是否大于0一起进行，所以该操作有原子性 ，gt:greather than
        boolean result  = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count= "+
                "stock_count-1").eq("goods_id",goods.getId()).gt("stock_count",0));
        if(!result) {
            valueOperations.set("isStockEmpty:"+goods.getId(),"0");
            return null;
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(),seckillOrder);

        //删除锁
        redisTemplate.delete("user:"+user.getId()+":"+goods.getId());
        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId ==null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean checkPath(User user, Long goodsId , String path) {
        if(user==null || goodsId<0|| StringUtils.isEmpty(path))
            return false;
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(StringUtils.isEmpty(captcha)||user==null||goodsId<0)
            return false;
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
