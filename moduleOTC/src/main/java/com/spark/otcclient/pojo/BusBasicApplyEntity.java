package com.spark.otcclient.pojo;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;


@SuppressLint("ParcelCreator")
public class BusBasicApplyEntity implements Parcelable {
    /**
     * 商家认证认证上传
     *
     * @param applyMarginId 1 - 个人商家   2 - 企业商家
     * @param username      姓名
     * @param telno         电话
     * @param email         邮箱
     * @param contact       紧急联系人
     * @param contactTel    紧急联系人电话
     * @param relationship  与本人关系
     * @param address       长住地址
     */



    private String username;
    private String telno;
    private String email;
    private String contact;
    private String contactTel;
    private String relationship;
    private String address;
    private String coinSymbol;
    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
