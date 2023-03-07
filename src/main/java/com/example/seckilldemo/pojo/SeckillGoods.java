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
 * 秒杀商品表
 * </p>
 *
 * @author sht
 * @since 2023-02-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_seckill_goods")
public class SeckillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 秒杀商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("goods_id")
    private Long goodsId;

    /**
     * 秒杀家
     */
    @TableField("seckill_price")
    private BigDecimal seckillPrice;

    /**
     * 库存数量
     */
    @TableField("stock_count")
    private Integer stockCount;

    /**
     * 秒杀开始时间
     */
    @TableField("start_date")
    private Date startDate;

    /**
     * 秒杀结束时间
     */
    @TableField("end_date")
    private Date endDate;


}
