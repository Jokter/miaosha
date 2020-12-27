package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.OrderModel;

public interface OrderService {
    //1、通过前端传秒杀id，然后下单接口内校验对应id是否属于对应商品且活动已开始
    //2、直接在下单接口判断商品是否存在秒杀活动，若存在进行中则已秒杀价格下单
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;
}