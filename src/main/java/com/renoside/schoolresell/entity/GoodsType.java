package com.renoside.schoolresell.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class GoodsType {

    public static final int GOODSTYPE_ELECTRONIC = 2001;
    public static final int GOODSTYPE_BOOK = 2002;
    public static final int GOODSTYPE_LABOR = 2003;
    public static final int GOODSTYPE_HUMAN = 2004;
    public static final int GOODSTYPE_HURRY = 2005;

    @Id
    @Column(length = 50, nullable = false)
    private String typeId;
    @Column(nullable = false)
    private String goodsId;
    @Column(nullable = false)
    private int goodsType;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public int getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(int goodsType) {
        this.goodsType = goodsType;
    }

    @Override
    public String toString() {
        return "GoodsType{" +
                "typeId='" + typeId + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", goodsType='" + goodsType + '\'' +
                '}';
    }
}
