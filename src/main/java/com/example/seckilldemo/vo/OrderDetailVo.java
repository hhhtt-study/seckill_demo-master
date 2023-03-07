package com.example.seckilldemo.vo;

import com.example.seckilldemo.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private Order order;

    private GoodsVo goodsVo;
}
