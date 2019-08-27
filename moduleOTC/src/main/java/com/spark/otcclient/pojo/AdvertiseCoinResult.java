package com.spark.otcclient.pojo;

public class AdvertiseCoinResult {

    /**
     * code : 200
     * message : SUCCESS
     * data : {"id":2,"merchantType":"2","coinName":"BTC","amount":2,"buyFeeRate":0.007,"sellFeeRate":0.007,"advMinLimit":1,"advMaxLimit":20,"createTime":1546840537000,"updateTime":null,"status":1}
     * count : null
     * responseString : 200~SUCCESS
     * url : null
     * cid : null
     */

    private int code;
    private String message;
    private DataBean data;
    private Object count;
    private String responseString;
    private Object url;
    private Object cid;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
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

    public static class DataBean {
        /**
         * id : 2
         * merchantType : 2
         * coinName : BTC
         * amount : 2.0
         * buyFeeRate : 0.007
         * sellFeeRate : 0.007
         * advMinLimit : 1.0
         * advMaxLimit : 20.0
         * createTime : 1546840537000
         * updateTime : null
         * status : 1
         */

        private int id;
        private String merchantType;
        private String coinName;
        private double amount;
        private double buyFeeRate;
        private double sellFeeRate;
        private double advMinLimit;
        private double advMaxLimit;
        private long createTime;
        private Object updateTime;
        private int status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMerchantType() {
            return merchantType;
        }

        public void setMerchantType(String merchantType) {
            this.merchantType = merchantType;
        }

        public String getCoinName() {
            return coinName;
        }

        public void setCoinName(String coinName) {
            this.coinName = coinName;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getBuyFeeRate() {
            return buyFeeRate;
        }

        public void setBuyFeeRate(double buyFeeRate) {
            this.buyFeeRate = buyFeeRate;
        }

        public double getSellFeeRate() {
            return sellFeeRate;
        }

        public void setSellFeeRate(double sellFeeRate) {
            this.sellFeeRate = sellFeeRate;
        }

        public double getAdvMinLimit() {
            return advMinLimit;
        }

        public void setAdvMinLimit(double advMinLimit) {
            this.advMinLimit = advMinLimit;
        }

        public double getAdvMaxLimit() {
            return advMaxLimit;
        }

        public void setAdvMaxLimit(double advMaxLimit) {
            this.advMaxLimit = advMaxLimit;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public Object getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Object updateTime) {
            this.updateTime = updateTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
