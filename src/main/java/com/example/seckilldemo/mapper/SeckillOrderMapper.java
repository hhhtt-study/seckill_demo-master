package com.example.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 秒杀订单表 Mapper 接口
 * </p>
 *
 * @author sht
 * @since 2023-02-14
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

}
