package com.spark.otcclient.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.spark.otcclient.R;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class FindAdvertiseResult implements Parcelable {

    /**
     * code : 200
     * message : SUCCESS
     * data : [{"id":438,"memberId":199,"advertiseType":0,"tradeType":0,"country":"China","localCurrency":"CNY","coinName":"USDT","payMode":"alipay,wechat,card","payData":"[{\"createTime\":1563184641000,\"id\":111,\"memberId\":199,\"payAddress\":\"alipay12345\",\"payType\":\"alipay\",\"qrCodeUrl\":\"http://oss.bitrade.top/oss/2019-07-15/08142ca4-bbb0-4e4b-930c-f6103b2700c9.jpg\",\"realName\":\"朱守明\",\"status\":1,\"updateTime\":1563184641000},{\"createTime\":1565319970000,\"id\":147,\"memberId\":199,\"payAddress\":\"yuuihgg\",\"payType\":\"wechat\",\"qrCodeUrl\":\"http://xinhuo-xindai.oss-cn-hangzhou.aliyuncs.com/2019/08/09/041a4e1d-1aab-4769-afe5-e5be3d7800cf.jpg\",\"realName\":\"虚度\",\"status\":1,\"updateTime\":1565319970000},{\"bank\":\"中国建设银行\",\"branch\":\"是的\",\"createTime\":1565320002000,\"id\":148,\"memberId\":199,\"payAddress\":\"65852336655255555\",\"payType\":\"card\",\"realName\":\"朱亚文\",\"status\":1,\"updateTime\":1565320002000}]","number":12,"remainAmount":12,"planFrozenFee":0.24,"remainFrozenFee":0.24,"maxLimit":26,"minLimit":26,"premiseRate":0.25,"price":25,"priceType":0,"timeLimit":25,"remark":"","createTime":1565333395000,"username":"小香","status":0},{"id":439,"memberId":199,"advertiseType":1,"tradeType":0,"country":"China","localCurrency":"CNY","coinName":"USDT","payMode":"wechat,alipay,card","payData":"[{\"createTime\":1563184641000,\"id\":111,\"memberId\":199,\"payAddress\":\"alipay12345\",\"payType\":\"alipay\",\"qrCodeUrl\":\"http://oss.bitrade.top/oss/2019-07-15/08142ca4-bbb0-4e4b-930c-f6103b2700c9.jpg\",\"realName\":\"朱守明\",\"status\":1,\"updateTime\":1563184641000},{\"createTime\":1565319970000,\"id\":147,\"memberId\":199,\"payAddress\":\"yuuihgg\",\"payType\":\"wechat\",\"qrCodeUrl\":\"http://xinhuo-xindai.oss-cn-hangzhou.aliyuncs.com/2019/08/09/041a4e1d-1aab-4769-afe5-e5be3d7800cf.jpg\",\"realName\":\"虚度\",\"status\":1,\"updateTime\":1565319970000},{\"bank\":\"中国建设银行\",\"branch\":\"是的\",\"createTime\":1565320002000,\"id\":148,\"memberId\":199,\"payAddress\":\"65852336655255555\",\"payType\":\"card\",\"realName\":\"朱亚文\",\"status\":1,\"updateTime\":1565320002000}]","number":23,"remainAmount":21.92,"planFrozenFee":0.23,"remainFrozenFee":0.2192,"maxLimit":36,"minLimit":36,"premiseRate":0,"price":100,"priceType":0,"timeLimit":26,"remark":"","createTime":1565333470000,"username":"小香","status":0},{"id":447,"memberId":199,"advertiseType":1,"tradeType":0,"country":"China","localCurrency":"CNY","coinName":"USDT","payMode":"alipay","payData":"[{\"createTime\":1563184641000,\"id\":111,\"memberId\":199,\"payAddress\":\"alipay12345\",\"payType\":\"alipay\",\"qrCodeUrl\":\"http://oss.bitrade.top/oss/2019-07-15/08142ca4-bbb0-4e4b-930c-f6103b2700c9.jpg\",\"realName\":\"朱守明\",\"status\":1,\"updateTime\":1563184641000}]","number":50,"remainAmount":49.30555556,"planFrozenFee":0.5,"remainFrozenFee":0.49305556,"maxLimit":20,"minLimit":1,"premiseRate":0,"price":7.2,"priceType":0,"timeLimit":25,"remark":"测试","createTime":1565339995000,"username":"小香","status":0}]
     * count : null
     * responseString : 200~SUCCESS
     * url : null
     * cid : null
     */

    private int code;
    private String message;
    private Object count;
    private String responseString;
    private Object url;
    private Object cid;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getCount() {
        return count;
    }

    public void setCount(Object count) {
        this.count = count;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public Object getCid() {
        return cid;
    }

    public void setCid(Object cid) {
        this.cid = cid;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 438
         * memberId : 199
         * advertiseType : 0
         * tradeType : 0
         * country : China
         * localCurrency : CNY
         * coinName : USDT
         * payMode : alipay,wechat,card
         * payData : [{"createTime":1563184641000,"id":111,"memberId":199,"payAddress":"alipay12345","payType":"alipay","qrCodeUrl":"http://oss.bitrade.top/oss/2019-07-15/08142ca4-bbb0-4e4b-930c-f6103b2700c9.jpg","realName":"朱守明","status":1,"updateTime":1563184641000},{"createTime":1565319970000,"id":147,"memberId":199,"payAddress":"yuuihgg","payType":"wechat","qrCodeUrl":"http://xinhuo-xindai.oss-cn-hangzhou.aliyuncs.com/2019/08/09/041a4e1d-1aab-4769-afe5-e5be3d7800cf.jpg","realName":"虚度","status":1,"updateTime":1565319970000},{"bank":"中国建设银行","branch":"是的","createTime":1565320002000,"id":148,"memberId":199,"payAddress":"65852336655255555","payType":"card","realName":"朱亚文","status":1,"updateTime":1565320002000}]
         * number : 12.0
         * remainAmount : 12.0
         * planFrozenFee : 0.24
         * remainFrozenFee : 0.24
         * maxLimit : 26.0
         * minLimit : 26.0
         * premiseRate : 0.25
         * price : 25.0
         * priceType : 0
         * timeLimit : 25
         * remark :
         * createTime : 1565333395000
         * username : 小香
         * status : 0
         */

        private int id;
        private int memberId;
        private int advertiseType;
        private int tradeType;
        private String country;
        private String localCurrency;
        private String coinName;
        private String payMode;
        private String payData;
        private double number;
        private double remainAmount;
        private double planFrozenFee;
        private double remainFrozenFee;
        private double maxLimit;
        private double minLimit;
        private double premiseRate;
        private double price;
        private int priceType;
        private int timeLimit;
        private String remark;
        private long createTime;
        private String username;
        private int status;

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

        public int getAdvertiseType() {
            return advertiseType;
        }

        public void setAdvertiseType(int advertiseType) {
            this.advertiseType = advertiseType;
        }

        public int getTradeType() {
            return tradeType;
        }

        public void setTradeType(int tradeType) {
            this.tradeType = tradeType;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getLocalCurrency() {
            return localCurrency;
        }

        public void setLocalCurrency(String localCurrency) {
            this.localCurrency = localCurrency;
        }

        public String getCoinName() {
            return coinName;
        }

        public void setCoinName(String coinName) {
            this.coinName = coinName;
        }

        public String getPayMode() {
            return payMode;
        }

        public void setPayMode(String payMode) {
            this.payMode = payMode;
        }

        public String getPayData() {
            return payData;
        }

        public void setPayData(String payData) {
            this.payData = payData;
        }

        public double getNumber() {
            return number;
        }

        public void setNumber(double number) {
            this.number = number;
        }

        public double getRemainAmount() {
            return remainAmount;
        }

        public void setRemainAmount(double remainAmount) {
            this.remainAmount = remainAmount;
        }

        public double getPlanFrozenFee() {
            return planFrozenFee;
        }

        public void setPlanFrozenFee(double planFrozenFee) {
            this.planFrozenFee = planFrozenFee;
        }

        public double getRemainFrozenFee() {
            return remainFrozenFee;
        }

        public void setRemainFrozenFee(double remainFrozenFee) {
            this.remainFrozenFee = remainFrozenFee;
        }

        public double getMaxLimit() {
            return maxLimit;
        }

        public void setMaxLimit(double maxLimit) {
            this.maxLimit = maxLimit;
        }

        public double getMinLimit() {
            return minLimit;
        }

        public void setMinLimit(double minLimit) {
            this.minLimit = minLimit;
        }

        public double getPremiseRate() {
            return premiseRate;
        }

        public void setPremiseRate(double premiseRate) {
            this.premiseRate = premiseRate;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getPriceType() {
            return priceType;
        }

        public void setPriceType(int priceType) {
            this.priceType = priceType;
        }

        public int getTimeLimit() {
            return timeLimit;
        }

        public void setTimeLimit(int timeLimit) {
            this.timeLimit = timeLimit;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        /**
         * 姓名缩写
         *
         * @return
         */
        public String getNameFirstChar() {
            return StringUtils.isEmpty(username) ? "" : username.substring(0, 1);
        }

        /**
         * 数量
         *
         * @return
         */
        public String formatNum() {
            return MathUtils.getRundNumber(remainAmount, 8, null) + " " + coinName;
        }

        /**
         * 限额
         *
         * @return
         */
        public String formatLimit() {
            return MathUtils.getRundNumber(minLimit, 2, null)
                    + " - " +
                    MathUtils.getRundNumber(maxLimit, 2, null)
                    + " " + localCurrency;
        }

        /**
         * 是否支持支付宝支付
         *
         * @return
         */
        public boolean isSupportAliPay() {
            return payMode.contains(Constant.alipay);
        }

        /**
         * 是否支持微信支付
         *
         * @return
         */
        public boolean isSupportWeChatPay() {
            return payMode.contains(Constant.wechat);
        }

        /**
         * 是否支持银行卡支付
         *
         * @return
         */
        public boolean isSupportBankPay() {
            return payMode.contains(Constant.card);
        }

        /**
         * 是否支持Paypal支付
         *
         * @return
         */
        public boolean isSupportPaypalPay() {
            return payMode.contains(Constant.PAYPAL);
        }

        /**
         * 是否支持其他支付
         *
         * @return
         */
        public boolean isSupportOtherPay() {
            return payMode.contains(Constant.other);
        }

        /**
         * 价格
         *
         * @return
         */
        public String formatPrice() {
            return price + " " + localCurrency;
        }

        /**
         * 购买or出售
         *
         * @return
         */
        public String buyOrSell() {
            return advertiseType == 0 ? BaseApplication.getInstance().getString(R.string.sell2) : BaseApplication.getInstance().getString(R.string.buy2);
        }


//        public String formatRangeTimeOrder() {
//            if (StringUtils.isEmpty(String.valueOf(rangeTimeOrder))
//                    || StringUtils.isEmpty(String.valueOf(rangeTimeSuccessOrder))
//                    || rangeTimeSuccessOrder == 0) {
//                rangeTimeOrder = 1;
//                rangeTimeSuccessOrder = 0;
//            }
//            return rangeTimeSuccessOrder + "    " + MathUtils.getRundNumber((Double.parseDouble(rangeTimeSuccessOrder + "") / rangeTimeOrder) * 100, 2, null) + "%";
//        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeString(this.responseString);
        dest.writeList(this.data);
    }

    public FindAdvertiseResult() {
    }

    protected FindAdvertiseResult(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.count = in.readParcelable(Object.class.getClassLoader());
        this.responseString = in.readString();
        this.url = in.readParcelable(Object.class.getClassLoader());
        this.cid = in.readParcelable(Object.class.getClassLoader());
        this.data = new ArrayList<DataBean>();
        in.readList(this.data, DataBean.class.getClassLoader());
    }

    public static final Creator<FindAdvertiseResult> CREATOR = new Creator<FindAdvertiseResult>() {
        @Override
        public FindAdvertiseResult createFromParcel(Parcel source) {
            return new FindAdvertiseResult(source);
        }

        @Override
        public FindAdvertiseResult[] newArray(int size) {
            return new FindAdvertiseResult[size];
        }
    };
}
