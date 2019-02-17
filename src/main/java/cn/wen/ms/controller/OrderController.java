package cn.wen.ms.controller;


import cn.wen.ms.error.BusinessException;
import cn.wen.ms.error.EmBusinessError;
import cn.wen.ms.response.CommonReturnType;
import cn.wen.ms.service.OrderService;
import cn.wen.ms.service.model.OrderModel;
import cn.wen.ms.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = {"*"},allowCredentials = "true")
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @PostMapping("/createOrder")
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,@RequestParam(name = "promoId",required = false)Integer promoId)
                                         throws BusinessException {
        //获取用户登录信息
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("LOGIN");
        if(isLogin==null|| !isLogin){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户还未登录不能下单");
        }
        UserModel userModel= (UserModel) httpServletRequest.getSession().getAttribute("USER");
        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId,promoId, amount);
        return CommonReturnType.create(null);
    }

}
