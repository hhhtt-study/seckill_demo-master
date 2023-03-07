package com.example.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.pojo.Order;
import com.example.seckilldemo.pojo.User;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sht
 * @since 2023-02-14
 */
public interface OrderService extends IService<Order> {

    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId,String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
