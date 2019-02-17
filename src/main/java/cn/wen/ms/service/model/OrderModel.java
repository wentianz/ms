package cn.wen.ms.service.model;


import javax.naming.ldap.PagedResultsControl;
import java.math.BigDecimal;

//用户下单交易模型
public class OrderModel {


    private String orderId;

    private Integer userId;

    private Integer itemId;
    //购买数量
    private Integer amount;

    //购买金额
    private BigDecimal orderPrice;

    //购买商品时的单价 ，若promoId非空，则表示秒杀的商品价格
    private BigDecimal itemPrice;
    //若是非空 则是秒杀的方式下单
    private Integer promoId;

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }



    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
}
