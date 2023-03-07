package com.example.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author sht
 * @since 2023-02-14
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
