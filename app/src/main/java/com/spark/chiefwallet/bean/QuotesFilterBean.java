package com.spark.chiefwallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019-08-19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class QuotesFilterBean implements Parcelable {
    private int type;
    private int filterType;

    public QuotesFilterBean() {
    }

    protected QuotesFilterBean(Parcel in) {
        type = in.readInt();
        filterType = in.readInt();
    }

    public static final Creator<QuotesFilterBean> CREATOR = new Creator<QuotesFilterBean>() {
        @Override
        public QuotesFilterBean createFromParcel(Parcel in) {
            return new QuotesFilterBean(in);
        }

        @Override
        public QuotesFilterBean[] newArray(int size) {
            return new QuotesFilterBean[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeInt(filterType);
    }
}
