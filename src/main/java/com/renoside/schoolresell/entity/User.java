package com.renoside.schoolresell.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class User {

    @Id
    @Column(length = 50, nullable = false)
    private String userId;
    @Column(length = 50, nullable = false)
    private String token;
    private String userImg;
    @Column(length = 50, nullable = false)
    private String loginName;
    @Column(length = 50, nullable = false)
    private String loginPassword;
    @Column(length = 50)
    private String userName;
    private String userDescription;
    @Column(length = 11)
    private String userPhone;
    private String userAddress;
    private Date userCreateTime;
    private Date userLastTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Date getUserCreateTime() {
        return userCreateTime;
    }

    public void setUserCreateTime(Date userCreateTime) {
        this.userCreateTime = userCreateTime;
    }

    public Date getUserLastTime() {
        return userLastTime;
    }

    public void setUserLastTime(Date userLastTime) {
        this.userLastTime = userLastTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userImg='" + userImg + '\'' +
                ", loginName='" + loginName + '\'' +
                ", loginPassword='" + loginPassword + '\'' +
                ", userName='" + userName + '\'' +
                ", userDescription='" + userDescription + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userCreateTime=" + userCreateTime +
                ", userLastTime=" + userLastTime +
                '}';
    }
}
