package com.example.seckilldemo.rabbitmq;

import com.example.seckilldemo.pojo.SeckillMessage;
import com.example.seckilldemo.pojo.SeckillOrder;
import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.service.GoodsService;
import com.example.seckilldemo.service.OrderService;
import com.example.seckilldemo.utils.JsonUtil;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg){
        log.info("接收消息: "+msg);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodsId =seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount()<1){
            return;
        }
        //默认redis缓存失效时间大于秒杀时间，这样才能在redis中判断是否多买
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null){
            return;
        }
        orderService.seckill(user,goodsVo);

    }

}
