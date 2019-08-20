package com.spark.acclient.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.spark.acclient.R;

import java.util.List;

import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.DateUtils;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CoinTransDetailsResult implements Parcelable {

    /**
     * cid : string
     * code : 0
     * count : {}
     * data : {"current":0,"pages":0,"records":[{"address":"string","amount":0.21,"businessId":"string","coinName":"string","createTime":"2019-06-11T02:41:06.799Z","fee":0.21,"id":0,"memberId":0,"subType":0,"type":0}],"size":0,"total":0}
     * message : string
     * responseString : string
     * url : string
     */

    private String cid;
    private int code;
    private CountBean count;
    private DataBean data;
    private String message;
    private String responseString;
    private String url;

    protected CoinTransDetailsResult(Parcel in) {
        cid = in.readString();
        code = in.readInt();
        message = in.readString();
        responseString = in.readString();
        url = in.readString();
    }

    public static final Creator<CoinTransDetailsResult> CREATOR = new Creator<CoinTransDetailsResult>() {
        @Override
        public CoinTransDetailsResult createFromParcel(Parcel in) {
            return new CoinTransDetailsResult(in);
        }

        @Override
        public CoinTransDetailsResult[] newArray(int size) {
            return new CoinTransDetailsResult[size];
        }
    };

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CountBean getCount() {
        return count;
    }

    public void setCount(CountBean count) {
        this.count = count;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cid);
        dest.writeInt(code);
        dest.writeString(message);
        dest.writeString(responseString);
        dest.writeString(url);
    }

    public static class CountBean {
    }

    public static class DataBean {
        /**
         * current : 0
         * pages : 0
         * records : [{"address":"string","amount":0.21,"businessId":"string","coinName":"string","createTime":"2019-06-11T02:41:06.799Z","fee":0.21,"id":0,"memberId":0,"subType":0,"type":0}]
         * size : 0
         * total : 0
         */

        private int current;
        private int pages;
        private int size;
        private int total;
        private List<RecordsBean> records;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class RecordsBean {
            /**
             * address : string
             * amount : 0.21
             * businessId : string
             * coinName : string
             * createTime : 2019-06-11T02:41:06.799Z
             * fee : 0.21
             * id : 0
             * memberId : 0
             * subType : 0
             * type : 0
             */

            private String address;
            private double amount;
            private String businessId;
            private String coinName;
            private String createTime;
            private double fee;
            private int id;
            private int memberId;
            private int subType;
            private int type;
            private int status;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public String getBusinessId() {
                return businessId;
            }

            public void setBusinessId(String businessId) {
                this.businessId = businessId;
            }

            public String getCoinName() {
                return coinName;
            }

            public void setCoinName(String coinName) {
                this.coinName = coinName;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public double getFee() {
                return fee;
            }

            public void setFee(double fee) {
                this.fee = fee;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getMemberId() {
                return memberId;
            }

            public void setMemberId(int memberId) {
                this.memberId = memberId;
            }

            public int getSubType() {
                return subType;
            }

            public void setSubType(int subType) {
                this.subType = subType;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String initType() {
                String typeText = "";
                if (Constant.isContract) {
                    switch (type) {
                        case 3:
                            typeText = BaseApplication.getInstance().getString(R.string.trans_in);
                            break;
                        case 4:
                            typeText = BaseApplication.getInstance().getString(R.string.trans_out);
                            break;
                    }
                } else {
                    if (!StringUtils.isEmpty(Constant.propertyDetailsTypeJson)) {
                        PropertyDetailsTypeResult propertyDetailsTypeResult = BaseApplication.gson.fromJson(Constant.propertyDetailsTypeJson, PropertyDetailsTypeResult.class);
                        for (PropertyDetailsTypeResult.DataBean dataBean : propertyDetailsTypeResult.getData()) {
                            if (subType == Integer.valueOf(dataBean.getValue())) {
                                typeText = dataBean.getDescript();
                                break;
                            }
                        }
                    }
                }

                return typeText;
//                String typeText = "";
//                switch (type) {
//                    case 1:
//                        typeText = BaseApplication.getInstance().getString(R.string.coin_in);
//                        break;
//                    case 2:
//                        typeText = BaseApplication.getInstance().getString(R.string.coin_out);
//                        break;
//                    case 3:
//                        typeText = BaseApplication.getInstance().getString(R.string.trans_in);
//                        break;
//                    case 4:
//                        typeText = BaseApplication.getInstance().getString(R.string.trans_out);
//                        break;
//                    case 5:
//                        typeText = BaseApplication.getInstance().getString(R.string.sell);
//                        break;
//                    case 6:
//                        typeText = BaseApplication.getInstance().getString(R.string.buy);
//                        break;
//                    case 7:
//                        typeText = BaseApplication.getInstance().getString(R.string.sell);
//                        break;
//                    case 8:
//                        typeText = BaseApplication.getInstance().getString(R.string.buy);
//                        break;
//                    case 9:
//                        typeText = BaseApplication.getInstance().getString(R.string.unknown);
//                        break;
//                }

//                return typeText;
            }

            public String formatStatue() {
                String typeText = BaseApplication.getInstance().getString(R.string.completed);
                if (type == 2) {
                    switch (status) {
                        case 0:
                            typeText = BaseApplication.getInstance().getString(R.string.pending_review);
                            break;
                        case 1:
                            typeText = BaseApplication.getInstance().getString(R.string.pending_payment);
                            break;
                        case 2:
                            typeText = BaseApplication.getInstance().getString(R.string.in_payment);
                            break;
                        case 3:
                            typeText = BaseApplication.getInstance().getString(R.string.completed);
                            break;
                        case 6:
                            typeText = BaseApplication.getInstance().getString(R.string.payment_rejection);
                            break;
                        case 7:
                            typeText = BaseApplication.getInstance().getString(R.string.audit_rejection);
                            break;
                        case 8:
                            typeText = BaseApplication.getInstance().getString(R.string.payment_Fail);
                            break;
                        case 9:
                            typeText = BaseApplication.getInstance().getString(R.string.cancelled);
                            break;
                    }
                }
                return typeText;
            }

            public boolean initTypeColor() {
//                boolean typeColor = false;
//                switch (type) {
//                    case 1:
//                        typeColor = true;
//                        break;
//                    case 2:
//                        typeColor = false;
//                        break;
//                    case 3:
//                        typeColor = true;
//                        break;
//                    case 4:
//                        typeColor = false;
//                        break;
//                    case 5:
//                        typeColor = false;
//                        break;
//                    case 6:
//                        typeColor = true;
//                        break;
//                    case 7:
//                        typeColor = false;
//                        break;
//                    case 8:
//                        typeColor = true;
//                        break;
//                    case 9:
//                        typeColor = true;
//                        break;
//                }

                return true;
            }

            public String formatDate() {
                return DateUtils.formatDate("MM/dd HH:mm", createTime);
            }

            public String formatAmount() {
                return DfUtils.numberFormat(amount, amount == 0 ? 0 : 8);
            }

            public String formatFee() {
                return DfUtils.numberFormat(fee, fee == 0 ? 0 : 8);
            }

            public String formatTypeName() {
                return BaseApplication.getInstance().getString(R.string.number) + "(" + coinName + ")";
            }
        }
    }
}
