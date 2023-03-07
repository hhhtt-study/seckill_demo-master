package com.example.seckilldemo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author sht
 * @since 2023-02-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 商品ID
     */
    @TableField("goods_id")
    private Long goodsId;

    /**
     * 收获地址ID
     */
    @TableField("delivery_addr_id")
    private Long deliveryAddrId;

    /**
     * 商品名字
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 商品数量
     */
    @TableField("goods_count")
    private Integer goodsCount;

    /**
     * 商品价格
     */
    @TableField("goods_price")
    private BigDecimal goodsPrice;

    /**
     * 1 pc,2 android, 3 ios
     */
    @TableField("order_channel")
    private Integer orderChannel;

    /**
     * 订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退货，5已完成
     */
    @TableField("status")
    private Integer status;

    /**
     * 订单创建时间
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 支付时间
     */
    @TableField("pay_date")
    private Date payDate;


}
