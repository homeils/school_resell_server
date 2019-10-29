package com.renoside.schoolresell.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class GoodsLikes {

    @Id
    @Column(length = 50, nullable = false)
    private String likesId;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String goodsId;

    public String getLikesId() {
        return likesId;
    }

    public void setLikesId(String likesId) {
        this.likesId = likesId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "GoodsLikes{" +
                "likesId='" + likesId + '\'' +
                ", userId='" + userId + '\'' +
                ", goodsId='" + goodsId + '\'' +
                '}';
    }
}
