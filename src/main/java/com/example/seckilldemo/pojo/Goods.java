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
@TableName("t_goods")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 商品标题
     */
    @TableField("goods_title")
    private String goodsTitle;

    /**
     * 商品图片
     */
    @TableField("goods_img")
    private String goodsImg;

    /**
     * 商品详情
     */
    @TableField("goods_detail")
    private String goodsDetail;

    /**
     * 商品价格
     */
    @TableField("goods_price")
    private BigDecimal goodsPrice;

    /**
     * 商品库存，-1表示没有限制
     */
    @TableField("goods_stock")
    private Integer goodsStock;


}
