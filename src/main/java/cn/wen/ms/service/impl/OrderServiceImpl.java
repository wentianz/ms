package cn.wen.ms.service.impl;

import cn.wen.ms.dao.OrderDOMapper;
import cn.wen.ms.dao.SequenceDOMapper;
import cn.wen.ms.dataobject.OrderDO;
import cn.wen.ms.dataobject.SequenceDO;
import cn.wen.ms.error.BusinessException;
import cn.wen.ms.error.EmBusinessError;
import cn.wen.ms.service.ItemService;
import cn.wen.ms.service.OrderService;
import cn.wen.ms.service.UserService;
import cn.wen.ms.service.model.ItemModel;
import cn.wen.ms.service.model.OrderModel;
import cn.wen.ms.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl  implements OrderService {

    @Autowired
    OrderDOMapper orderDOMapper;

    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;
    @Autowired
    SequenceDOMapper sequenceDOMapper;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId,Integer amount) throws BusinessException {
        //检验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemId==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
        }
        UserModel userModel = userService.getUserById(userId);
        if(userModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
        }
        if(amount<=0 || amount>=200){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不符");
        }
        //校验活动信息
        if(promoId!=null){
            //校驗对应活动是否存在
            if(promoId.intValue()!=itemModel.getPromoModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息不正确");
                //校验活动是否正在进行
            } else if(itemModel.getPromoModel().getStatus()!=2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动还未开始");
            }
        }
        //#下单后减去库存，支付减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if(result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //订单入库
        OrderModel orderModel = new OrderModel();
        //生成订单流水号
        orderModel.setOrderId(generateOrderNo());
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if(promoId!=null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        orderModel.setPromoId(promoId);
        OrderDO orderDO = this.convertFromOrderModel(orderModel);
        orderDOMapper.insert(orderDO);
        //加上商品的销量
        itemService.increaseSales(itemId,amount);
        //返回前端
        return orderModel;
    }


    private String generateOrderNo(){
        //订单号有16位  前8位是时间信息 中间6位为自增序列  最后两位为分库分表位
        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);
        //中间6位为自增序列
        int sequence=0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKey(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for (int i =0 ; i< 6- sequenceStr.length();i++){
                stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);
        //最后两位为分录分表暂时写死
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if(orderModel==null){
            return  null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setAmount(orderModel.getAmount());
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
