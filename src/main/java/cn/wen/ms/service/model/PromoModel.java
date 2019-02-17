package cn.wen.ms.service.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;

public class PromoModel {
    private  Integer id;

    //秒杀活动状态 1 未开始 2 进行中 3 已结束
    private Integer status;
    //秒杀活动名称
    private  String PromoName;

    //秒杀活动
    private DateTime startDate;
    //秒杀活动结束时间
    private  DateTime endDate;
    //秒杀活动适用商品

    private  Integer itemId;

    //秒杀活动的商品价格
    private BigDecimal promoItemPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPromoName() {
        return PromoName;
    }

    public void setPromoName(String promoName) {
        PromoName = promoName;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getPromoItemPrice() {
        return promoItemPrice;
    }

    public void setPromoItemPrice(BigDecimal promoItemPrice) {
        this.promoItemPrice = promoItemPrice;
    }
}
