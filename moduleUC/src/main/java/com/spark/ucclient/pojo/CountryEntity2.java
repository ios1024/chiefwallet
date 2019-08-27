package com.spark.ucclient.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/8
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class CountryEntity2 implements Parcelable {

    /**
     * zhName : 中国
     * areaCode : 86
     * enName : China
     * language : 中文
     * localCurrency : CNY
     * sort : 0
     * sysLanguage : null
     */

    private String zhName;
    private String areaCode;
    private String enName;


    public CountryEntity2() {
    }

    protected CountryEntity2(Parcel in) {
        zhName = in.readString();
        areaCode = in.readString();
        enName = in.readString();
    }

    public static final Creator<CountryEntity2> CREATOR = new Creator<CountryEntity2>() {
        @Override
        public CountryEntity2 createFromParcel(Parcel in) {
            return new CountryEntity2(in);
        }

        @Override
        public CountryEntity2[] newArray(int size) {
            return new CountryEntity2[size];
        }
    };

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(zhName);
        dest.writeString(areaCode);
        dest.writeString(enName);
    }

    @Override
    public String toString() {
        return "CountryEntity2{" +
                "zhName='" + zhName + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", enName='" + enName + '\'' +
                '}';
    }
}
