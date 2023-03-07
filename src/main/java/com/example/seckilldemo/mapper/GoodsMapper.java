package com.example.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.pojo.Goods;
import com.example.seckilldemo.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author sht
 * @since 2023-02-14
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);

}
