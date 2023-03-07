package com.example.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 秒杀商品表 Mapper 接口
 * </p>
 *
 * @author sht
 * @since 2023-02-14
 */
@Mapper
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

}
