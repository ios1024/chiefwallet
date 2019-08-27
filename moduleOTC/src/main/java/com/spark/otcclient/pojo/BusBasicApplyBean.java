package com.spark.otcclient.pojo;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;


@SuppressLint("ParcelCreator")
public class BusBasicApplyBean implements Parcelable {


  private long applyMarginId;
  private String assetImg;
  private String detail;
  private String tradeDataImg;

    public long getApplyMarginId() {
        return applyMarginId;
    }

    public void setApplyMarginId(long applyMarginId) {
        this.applyMarginId = applyMarginId;
    }

    public String getAssetImg() {
        return assetImg;
    }

    public void setAssetImg(String assetImg) {
        this.assetImg = assetImg;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTradeDataImg() {
        return tradeDataImg;
    }

    public void setTradeDataImg(String tradeDataImg) {
        this.tradeDataImg = tradeDataImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
