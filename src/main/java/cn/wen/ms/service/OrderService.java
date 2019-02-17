package cn.wen.ms.service;

import cn.wen.ms.error.BusinessException;
import cn.wen.ms.service.model.OrderModel;

public interface OrderService {



    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount) throws BusinessException;


}
