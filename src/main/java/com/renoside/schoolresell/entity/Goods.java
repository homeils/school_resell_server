package com.renoside.schoolresell.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Goods {

    public static final int STATUS_SELLING = 1001;
    public static final int STATUS_PROCESSING = 1002;
    public static final int STATUS_COMPLETED = 1003;

    @Id
    @Column(length = 50, nullable = false)
    private String goodsId;
    @Column(length = 50, nullable = false)
    private String userId;
    @Column(length = 50, nullable = false)
    private String goodsName;
    private String goodsDescription;
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal goodsPrice;
    @Column(nullable = true)
    private int goodsLikes;
    @Column(nullable = false)
    private int goodsStatus;
    @Column(length = 11, nullable = false)
    private String goodsPhone;
    @Column(nullable = false)
    private String goodsAddress;

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public int getGoodsLikes() {
        return goodsLikes;
    }

    public void setGoodsLikes(int goodsLikes) {
        this.goodsLikes = goodsLikes;
    }

    public int getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(int goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public String getGoodsPhone() {
        return goodsPhone;
    }

    public void setGoodsPhone(String goodsPhone) {
        this.goodsPhone = goodsPhone;
    }

    public String getGoodsAddress() {
        return goodsAddress;
    }

    public void setGoodsAddress(String goodsAddress) {
        this.goodsAddress = goodsAddress;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goodsId='" + goodsId + '\'' +
                ", userId='" + userId + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsDescription='" + goodsDescription + '\'' +
                ", goodsPrice=" + goodsPrice +
                ", goodsLikes=" + goodsLikes +
                ", goodsStatus=" + goodsStatus +
                '}';
    }
}
