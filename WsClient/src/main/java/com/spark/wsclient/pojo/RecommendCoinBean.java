package com.spark.wsclient.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.SpanUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019-08-16
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RecommendCoinBean implements Parcelable {
    /**
     * code : 200
     * data : [{"usdLegalAsset":69767.78229,"symbol":"BTC/USDT","chg":-0.0199,"coinScale":4,"trend":[9847.18,9969.16,10060.64,10154.73,9996.25,10014.16,10010,10093.44,10052.52,10026.71,10037.69,10107.75,10363.7,10338.33,10387.75,10294.52,10302.77,10355.09,10353.89,10323.62,10097.69,9906.42,9975.53,9939.02],"change":-200.96,"tradeCoin":"BTC","baseCoinScreenScale":2,"volume":5102.034602183436,"lastDayClose":0.21,"high":10449.21,"coinScreenScale":4,"cnyLegalAsset":69767.78229,"baseCoinScale":4,"low":9852.96,"zone":"USDT","startTime":1565884800000,"close":9891.93,"turnover":5.192667142,"baseCoin":"USDT","open":10092.89}]
     * message : SUCCESS
     * responseString : 200~SUCCESS
     */

    private int code;
    private String message;
    private String responseString;
    private List<DataBean> data;

    protected RecommendCoinBean(Parcel in) {
        code = in.readInt();
        message = in.readString();
        responseString = in.readString();
    }

    public static final Creator<RecommendCoinBean> CREATOR = new Creator<RecommendCoinBean>() {
        @Override
        public RecommendCoinBean createFromParcel(Parcel in) {
            return new RecommendCoinBean(in);
        }

        @Override
        public RecommendCoinBean[] newArray(int size) {
            return new RecommendCoinBean[size];
        }
    };

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

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(code);
        parcel.writeString(message);
        parcel.writeString(responseString);
    }

    public static class DataBean implements Parcelable {
        /**
         * usdLegalAsset : 69767.78229
         * symbol : BTC/USDT
         * chg : -0.0199
         * coinScale : 4
         * trend : [9847.18,9969.16,10060.64,10154.73,9996.25,10014.16,10010,10093.44,10052.52,10026.71,10037.69,10107.75,10363.7,10338.33,10387.75,10294.52,10302.77,10355.09,10353.89,10323.62,10097.69,9906.42,9975.53,9939.02]
         * change : -200.96
         * tradeCoin : BTC
         * baseCoinScreenScale : 2
         * volume : 5102.034602183436
         * lastDayClose : 0.21
         * high : 10449.21
         * coinScreenScale : 4
         * cnyLegalAsset : 69767.78229
         * baseCoinScale : 4
         * low : 9852.96
         * zone : USDT
         * startTime : 1565884800000
         * close : 9891.93
         * turnover : 5.192667142
         * baseCoin : USDT
         * open : 10092.89
         */

        private double usdLegalAsset;
        private String symbol;
        private double chg;
        private int coinScale;
        private double change;
        private String tradeCoin;
        private int baseCoinScreenScale;
        private double volume;
        private double lastDayClose;
        private double high;
        private int coinScreenScale;
        private double cnyLegalAsset;
        private int baseCoinScale;
        private double low;
        private String zone;
        private long startTime;
        private double close;
        private double turnover;
        private String baseCoin;
        private double open;
        private List<Double> trend;

        protected DataBean(Parcel in) {
            usdLegalAsset = in.readDouble();
            symbol = in.readString();
            chg = in.readDouble();
            coinScale = in.readInt();
            change = in.readDouble();
            tradeCoin = in.readString();
            baseCoinScreenScale = in.readInt();
            volume = in.readDouble();
            lastDayClose = in.readDouble();
            high = in.readDouble();
            coinScreenScale = in.readInt();
            cnyLegalAsset = in.readDouble();
            baseCoinScale = in.readInt();
            low = in.readDouble();
            zone = in.readString();
            startTime = in.readLong();
            close = in.readDouble();
            turnover = in.readDouble();
            baseCoin = in.readString();
            open = in.readDouble();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel in) {
                return new DataBean(in);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };

        public double getUsdLegalAsset() {
            return usdLegalAsset;
        }

        public void setUsdLegalAsset(double usdLegalAsset) {
            this.usdLegalAsset = usdLegalAsset;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public double getChg() {
            return chg;
        }

        public void setChg(double chg) {
            this.chg = chg;
        }

        public int getCoinScale() {
            return coinScale;
        }

        public void setCoinScale(int coinScale) {
            this.coinScale = coinScale;
        }

        public double getChange() {
            return change;
        }

        public void setChange(double change) {
            this.change = change;
        }

        public String getTradeCoin() {
            return tradeCoin;
        }

        public void setTradeCoin(String tradeCoin) {
            this.tradeCoin = tradeCoin;
        }

        public int getBaseCoinScreenScale() {
            return baseCoinScreenScale;
        }

        public void setBaseCoinScreenScale(int baseCoinScreenScale) {
            this.baseCoinScreenScale = baseCoinScreenScale;
        }

        public double getVolume() {
            return volume;
        }

        public void setVolume(double volume) {
            this.volume = volume;
        }

        public double getLastDayClose() {
            return lastDayClose;
        }

        public void setLastDayClose(double lastDayClose) {
            this.lastDayClose = lastDayClose;
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public int getCoinScreenScale() {
            return coinScreenScale;
        }

        public void setCoinScreenScale(int coinScreenScale) {
            this.coinScreenScale = coinScreenScale;
        }

        public double getCnyLegalAsset() {
            return cnyLegalAsset;
        }

        public void setCnyLegalAsset(double cnyLegalAsset) {
            this.cnyLegalAsset = cnyLegalAsset;
        }

        public int getBaseCoinScale() {
            return baseCoinScale;
        }

        public void setBaseCoinScale(int baseCoinScale) {
            this.baseCoinScale = baseCoinScale;
        }

        public double getLow() {
            return low;
        }

        public void setLow(double low) {
            this.low = low;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public double getClose() {
            return close;
        }

        public void setClose(double close) {
            this.close = close;
        }

        public double getTurnover() {
            return turnover;
        }

        public void setTurnover(double turnover) {
            this.turnover = turnover;
        }

        public String getBaseCoin() {
            return baseCoin;
        }

        public void setBaseCoin(String baseCoin) {
            this.baseCoin = baseCoin;
        }

        public double getOpen() {
            return open;
        }

        public void setOpen(double open) {
            this.open = open;
        }

        public List<Double> getTrend() {
            return trend;
        }

        public void setTrend(List<Double> trend) {
            this.trend = trend;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeDouble(usdLegalAsset);
            parcel.writeString(symbol);
            parcel.writeDouble(chg);
            parcel.writeInt(coinScale);
            parcel.writeDouble(change);
            parcel.writeString(tradeCoin);
            parcel.writeInt(baseCoinScreenScale);
            parcel.writeDouble(volume);
            parcel.writeDouble(lastDayClose);
            parcel.writeDouble(high);
            parcel.writeInt(coinScreenScale);
            parcel.writeDouble(cnyLegalAsset);
            parcel.writeInt(baseCoinScale);
            parcel.writeDouble(low);
            parcel.writeString(zone);
            parcel.writeLong(startTime);
            parcel.writeDouble(close);
            parcel.writeDouble(turnover);
            parcel.writeString(baseCoin);
            parcel.writeDouble(open);
        }


        //币种
        public CharSequence initSymbol() {
            CharSequence text = new SpanUtils()
                    .append(symbol.split("/")[0]).setBold()
                    .append("/" + symbol.split("/")[1])
                    .create();
            return text;
        }

        //现价
        public CharSequence initClose() {
            return DfUtils.formatNum(MathUtils.getRundNumber(close, baseCoinScreenScale == 0 ?
                    2 : baseCoinScreenScale, null));
        }

        // 涨幅是否为 +/-
        public boolean isCoinPairPushChgUp() {
            return chg >= 0 ? true : false;
        }

        public String initChg() {
            return (isCoinPairPushChgUp() ? "+" : "") + MathUtils.getRundNumber(chg * 100, 2, "########0.") + "%";
        }

        public String initConvert() {
            return "≈" + MathUtils.getRundNumber(cnyLegalAsset, 2, null)
                    + " CNY";
        }
    }
}
