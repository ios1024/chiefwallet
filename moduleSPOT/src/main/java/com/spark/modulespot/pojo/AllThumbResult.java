package com.spark.modulespot.pojo;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.spark.modulespot.R;

import java.util.List;

import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.SPUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AllThumbResult implements Parcelable {
    /**
     * code : 200
     * message : 操作成功!
     * data : [{"symbol":"BTC/USDT","open":9311.9657,"high":9314.6985,"low":9314.6725,"close":9314.6725,"chg":2.0E-4,"change":2.7068,"volume":0.004,"turnover":37.25850516,"lastDayClose":0.1,"startTime":1561046400000,"baseCoinScale":8,"baseCoinScreenScale":4,"coinScale":8,"coinScreenScale":4,"indexPrice":0.1,"tagPrice":0.1,"fundRates":1,"legalAsset":9702.36,"baseCoin":"USDT","tradeCoin":"BTC"}]
     * count : null
     * responseString : 200~操作成功!
     */

    private int code;
    private String message;
    private Object count;
    private String responseString;
    private List<DataBean> data;

    protected AllThumbResult(Parcel in) {
        code = in.readInt();
        message = in.readString();
        responseString = in.readString();
    }

    public static final Creator<AllThumbResult> CREATOR = new Creator<AllThumbResult>() {
        @Override
        public AllThumbResult createFromParcel(Parcel in) {
            return new AllThumbResult(in);
        }

        @Override
        public AllThumbResult[] newArray(int size) {
            return new AllThumbResult[size];
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(message);
        dest.writeString(responseString);
    }

    public static class DataBean implements Parcelable {
        /**
         * symbol : BTC/USDT
         * open : 9311.9657
         * high : 9314.6985
         * low : 9314.6725
         * close : 9314.6725
         * chg : 2.0E-4
         * change : 2.7068
         * volume : 0.004
         * turnover : 37.25850516
         * lastDayClose : 0.1
         * startTime : 1561046400000
         * baseCoinScale : 8
         * baseCoinScreenScale : 4
         * coinScale : 8
         * coinScreenScale : 4
         * indexPrice : 0.1
         * tagPrice : 0.1
         * fundRates : 1
         * legalAsset : 9702.36
         * baseCoin : USDT
         * tradeCoin : BTC
         */

        private String symbol;
        private double open;
        private double high;
        private double low;
        private double close;
        private double chg;
        private double change;
        private double volume;
        private double turnover;
        private double lastDayClose;
        private long startTime;
        private int baseCoinScale;
        private int baseCoinScreenScale;
        private int coinScale;
        private int coinScreenScale;
        private double indexPrice;
        private double tagPrice;
        private int fundRates;
        private double usdLegalAsset;
        private double cnyLegalAsset;
        private double eurLegalAsset;
        private double ghsLegalAsset;
        private double ngnLegalAsset;
        private String baseCoin;
        private String tradeCoin;

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            symbol = in.readString();
            open = in.readDouble();
            high = in.readDouble();
            low = in.readDouble();
            close = in.readDouble();
            chg = in.readDouble();
            change = in.readDouble();
            volume = in.readDouble();
            turnover = in.readDouble();
            lastDayClose = in.readDouble();
            startTime = in.readLong();
            baseCoinScale = in.readInt();
            baseCoinScreenScale = in.readInt();
            coinScale = in.readInt();
            coinScreenScale = in.readInt();
            indexPrice = in.readDouble();
            tagPrice = in.readDouble();
            fundRates = in.readInt();
            usdLegalAsset = in.readDouble();
            cnyLegalAsset = in.readDouble();
            eurLegalAsset = in.readDouble();
            ghsLegalAsset = in.readDouble();
            ngnLegalAsset = in.readDouble();
            baseCoin = in.readString();
            tradeCoin = in.readString();
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

        public double getEurLegalAsset() {
            return eurLegalAsset;
        }

        public void setEurLegalAsset(double eurLegalAsset) {
            this.eurLegalAsset = eurLegalAsset;
        }

        public double getGhsLegalAsset() {
            return ghsLegalAsset;
        }

        public void setGhsLegalAsset(double ghsLegalAsset) {
            this.ghsLegalAsset = ghsLegalAsset;
        }

        public double getNgnLegalAsset() {
            return ngnLegalAsset;
        }

        public void setNgnLegalAsset(double ngnLegalAsset) {
            this.ngnLegalAsset = ngnLegalAsset;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public double getOpen() {
            return open;
        }

        public void setOpen(double open) {
            this.open = open;
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public double getLow() {
            return low;
        }

        public void setLow(double low) {
            this.low = low;
        }

        public double getClose() {
            return close;
        }

        public void setClose(double close) {
            this.close = close;
        }

        public double getChg() {
            return chg;
        }

        public void setChg(double chg) {
            this.chg = chg;
        }

        public double getChange() {
            return change;
        }

        public void setChange(double change) {
            this.change = change;
        }

        public double getVolume() {
            return volume;
        }

        public void setVolume(double volume) {
            this.volume = volume;
        }

        public double getTurnover() {
            return turnover;
        }

        public void setTurnover(double turnover) {
            this.turnover = turnover;
        }

        public double getLastDayClose() {
            return lastDayClose;
        }

        public void setLastDayClose(double lastDayClose) {
            this.lastDayClose = lastDayClose;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getBaseCoinScale() {
            return baseCoinScale;
        }

        public void setBaseCoinScale(int baseCoinScale) {
            this.baseCoinScale = baseCoinScale;
        }

        public int getBaseCoinScreenScale() {
            return baseCoinScreenScale;
        }

        public void setBaseCoinScreenScale(int baseCoinScreenScale) {
            this.baseCoinScreenScale = baseCoinScreenScale;
        }

        public int getCoinScale() {
            return coinScale;
        }

        public void setCoinScale(int coinScale) {
            this.coinScale = coinScale;
        }

        public int getCoinScreenScale() {
            return coinScreenScale;
        }

        public void setCoinScreenScale(int coinScreenScale) {
            this.coinScreenScale = coinScreenScale;
        }

        public double getIndexPrice() {
            return indexPrice;
        }

        public void setIndexPrice(double indexPrice) {
            this.indexPrice = indexPrice;
        }

        public double getTagPrice() {
            return tagPrice;
        }

        public void setTagPrice(double tagPrice) {
            this.tagPrice = tagPrice;
        }

        public int getFundRates() {
            return fundRates;
        }

        public void setFundRates(int fundRates) {
            this.fundRates = fundRates;
        }

        public double getUsdLegalAsset() {
            return usdLegalAsset;
        }

        public void setUsdLegalAsset(double usdLegalAsset) {
            this.usdLegalAsset = usdLegalAsset;
        }

        public double getCnyLegalAsset() {
            return cnyLegalAsset;
        }

        public void setCnyLegalAsset(double cnyLegalAsset) {
            this.cnyLegalAsset = cnyLegalAsset;
        }

        public String getBaseCoin() {
            return baseCoin;
        }

        public void setBaseCoin(String baseCoin) {
            this.baseCoin = baseCoin;
        }

        public String getTradeCoin() {
            return tradeCoin;
        }

        public void setTradeCoin(String tradeCoin) {
            this.tradeCoin = tradeCoin;
        }
        //--databinding

        //--QuotesVPViewModel
        // 涨幅是否为 +/-
        public boolean isCoinPairPushChgUp() {
            return SPUtils.getInstance().getGainMode() ? chg >= 0 : chg < 0;
        }

        //
        public String initConvert() {
            //1.人民币 CNY 2.美元 USDT 3.欧元 EUR 4.赛地 GHS 5.尼日利亚 NGN
            if (SPUtils.getInstance().getPricingCurrency().equals("1")) {
                return "≈ " + Constant.CNY_symbol + MathUtils.getRundNumber(cnyLegalAsset, 2, null);
            } else if (SPUtils.getInstance().getPricingCurrency().equals("2")) {
                return "≈ " + Constant.USD_symbol + MathUtils.getRundNumber(usdLegalAsset, 2, null);
            } else if (SPUtils.getInstance().getPricingCurrency().equals("3")) {
                return "≈ " + Constant.EUR_symbol + MathUtils.getRundNumber(eurLegalAsset, 2, null);
            } else if (SPUtils.getInstance().getPricingCurrency().equals("4")) {
                return "≈ " + Constant.GHS_symbol + MathUtils.getRundNumber(ghsLegalAsset, 2, null);
            } else if (SPUtils.getInstance().getPricingCurrency().equals("5")) {
                return "≈ " + Constant.NGN_symbol + MathUtils.getRundNumber(ngnLegalAsset, 2, null);
            } else
                return "≈ " + Constant.CNY_symbol + MathUtils.getRundNumber(cnyLegalAsset, 2, null);

        }

        //现价
        public String initClose() {
            return MathUtils.getRundNumber(close, baseCoinScreenScale == 0 ? 2 : baseCoinScreenScale, null);
        }

        public String init24HourCount() {
            return BaseApplication.getInstance().getString(R.string._24h_size) + (int) volume;
        }

        //币种
        public CharSequence initSymbol() {
            CharSequence text = new SpanUtils()
                    .append(symbol.split("/")[0]).setBold()
                    .append(" / " + symbol.split("/")[1])
                    .setFontSize(12, true)
                    .setForegroundColor(Color.parseColor("#718495"))
                    .create();
            return text;
        }

        public CharSequence initSymbol2() {
            CharSequence text = new SpanUtils()
                    .append(symbol.split("/")[0]).setBold()
                    .append("/" + symbol.split("/")[1])
                    .setFontSize(12, true)
                    .setForegroundColor(Color.parseColor("#b2bad1"))
                    .create();
            return text;
        }

        public CharSequence initSymbol3() {
            CharSequence text = new SpanUtils()
                    .append(symbol.split("/")[0]).setBold()
                    .append("/" + symbol.split("/")[1])
                    .setFontSize(10, true)
                    .setForegroundColor(Color.parseColor("#A4ABC0"))
                    .create();
            return text;
        }

        public String initChg() {
            return (chg >= 0 ? "+" : "") + MathUtils.getRundNumber(chg * 100, 2, "########0.") + "%";
        }

        public String initFavor() {
            return MathUtils.getRundNumber(close, baseCoinScreenScale == 0 ? 2 : baseCoinScreenScale, null) + " / " +
                    MathUtils.getRundNumber(cnyLegalAsset, 2, null)
                    + "CNY";
        }

        public String initImgUrl() {
            String url = "";
            if (!StringUtils.isEmpty(Constant.spotJson)) {
                SpotCoinResult spotCoinResult = BaseApplication.gson.fromJson(Constant.spotJson, SpotCoinResult.class);
                for (SpotCoinResult.DataBean dataBean : spotCoinResult.getData()) {
                    if (symbol.equals("USDT")) {
                        url = dataBean.getBaseIconUrl();
                        break;
                    } else if (dataBean.getSymbol().equals(symbol)) {
                        url = dataBean.getCoinIconUrl();
                        break;
                    }
                }
            }
            return url;
        }

        public boolean isFavor() {
            boolean isFavor = false;
            if (!BaseApplication.getInstance().isAppLogin()) {
                isFavor = false;
            } else if (SPUtils.getInstance().getFavorFindList().contains(symbol)) {
                isFavor = true;
            }
            return isFavor;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(symbol);
            dest.writeDouble(open);
            dest.writeDouble(high);
            dest.writeDouble(low);
            dest.writeDouble(close);
            dest.writeDouble(chg);
            dest.writeDouble(change);
            dest.writeDouble(volume);
            dest.writeDouble(turnover);
            dest.writeDouble(lastDayClose);
            dest.writeLong(startTime);
            dest.writeInt(baseCoinScale);
            dest.writeInt(baseCoinScreenScale);
            dest.writeInt(coinScale);
            dest.writeInt(coinScreenScale);
            dest.writeDouble(indexPrice);
            dest.writeDouble(tagPrice);
            dest.writeInt(fundRates);
            dest.writeDouble(usdLegalAsset);
            dest.writeDouble(cnyLegalAsset);
            dest.writeDouble(eurLegalAsset);
            dest.writeDouble(ghsLegalAsset);
            dest.writeDouble(ngnLegalAsset);
            dest.writeString(baseCoin);
            dest.writeString(tradeCoin);
        }
    }
}
