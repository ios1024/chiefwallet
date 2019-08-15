package com.spark.chiefwallet.app.trade.contract.kline;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.example.modulecfd.pojo.CfdAllThumbResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.bean.KlineSubscribeBean;
import com.spark.chiefwallet.util.DateUtils;
import com.spark.klinelib.KLineEntity;
import com.spark.modulespot.KlineClient;
import com.spark.wsclient.base.WsCMD;
import com.spark.wsclient.pojo.CfdThumbBean;
import com.spark.wsclient.utils.WsConstant;
import com.spark.wsclient.utils.WsUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.StringUtils;
import me.spark.mvvm.utils.WebSocketRequest;
import me.spark.mvvm.utils.WebSocketResponse;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/30
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CfdKlineHorizontalViewModel extends BaseViewModel {
    private CfdAllThumbResult.DataBean allThumbResult;
    private String resolution, period;
    private List<KLineEntity> mKLineEntities = new ArrayList<>();

    private String lastResolution;//上次传入时间段
    private String mResolutionVertical;
    private int type;
    //现价
    public ObservableField<CharSequence> close = new ObservableField<>();
    //换算
    public ObservableField<CharSequence> convert = new ObservableField<>();
    public ObservableField<Boolean> convertColor = new ObservableField<>();
    //24H量
    public ObservableField<String> _24hourcount = new ObservableField<>();
    //24H最高
    public ObservableField<String> _24hourhigh = new ObservableField<>();
    //24H最低
    public ObservableField<String> _24hourlow = new ObservableField<>();

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<List<KLineEntity>> klineHistoryList = new SingleLiveEvent<>();
        public SingleLiveEvent<KLineEntity> klineSubscribe = new SingleLiveEvent<>();
    }

    public CfdKlineHorizontalViewModel(@NonNull Application application) {
        super(application);
    }

    public void initDate(CfdAllThumbResult.DataBean allThumbResult, String lastResolution) {
        this.allThumbResult = allThumbResult;
        this.mResolutionVertical = lastResolution;
        updateCoinPairPushBean();
    }

    //获取K线历史数据
    public void loadKlineHistory(int type) {
        this.type = type;
        Long to = System.currentTimeMillis(), from;
        switch (type) {
            //分时
            case 0:
                from = to - 12L * 60 * 60 * 1000;       // 前半天的数据
                resolution = "m1";
                period = "m1";
                break;
            //1分
            case 1:
                from = to - 12L * 60 * 60 * 1000;       // 前一天的数据
                resolution = "m1";
                period = "m1";
                break;
            //5分
            case 2:
                from = to - 2 * 24L * 60 * 60 * 1000;   // 前2天的数据
                resolution = "m5";
                period = "m5";
                break;
            //1小时
            case 3:
                from = to - 24 * 24L * 60 * 60 * 1000;  // 前24天数据
                resolution = "h1";
                period = "h1";
                break;
            //1天
            case 4:
                from = to - 60 * 24L * 60 * 60 * 1000;  // 前60天数据
                resolution = "d1";
                period = "d1";
                break;
            case 5: // 15分钟
                from = to - 6 * 24L * 60 * 60 * 1000;   // 前6天的数据
                resolution = "m15";
                period = "m15";
                break;
            case 6: // 30分钟
                from = to - 10 * 24L * 60 * 60 * 1000;  // 前12 天数据
                resolution = "m30";
                period = "m30";
                break;
            case 7: // 1周
                from = to - 730 * 24L * 60 * 60 * 1000;  // 前2年数据
                resolution = "w1";
                period = "w1";
                break;
            case 8: // 1月
                from = to - 1095 * 24L * 60 * 60 * 1000; // 前3年数据
                resolution = "M1";
                period = "M1";
                break;
            default: // 默认1分钟
                from = to - 24L * 60 * 60 * 1000;        // 前一天的数据
                resolution = "m1";
                period = "m1";
                break;
        }
        //获取历史数据
        KlineClient.getInstance().getKlineHistory(allThumbResult.getSymbol(), WsConstant.CFD, from, to, resolution);
    }

    //title
    public CharSequence initTitle() {
        CharSequence text = new SpanUtils()
                .append(allThumbResult.getSymbol().split("/")[0])
                .append(" / " + allThumbResult.getSymbol().split("/")[1])
                .setFontSize(13, true)
                .create();
        return text;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebSocketResponse webSocketResponse) {
        switch (webSocketResponse.getCmd()) {
            //缩略图推送
            case WsCMD.PUSH_CFD_THUMB:
                Constant.lastCfdKlinePushTime = System.currentTimeMillis();
                try {
                    String json = "{\"date\":" + webSocketResponse.getResponse() + "}";
                    CfdThumbBean cfdThumbBean = App.gson.fromJson(json, CfdThumbBean.class);
                    for (CfdThumbBean.DateBean dateBean : cfdThumbBean.getDate()) {
                        if (allThumbResult.getSymbol().equals(dateBean.getSymbol())) {
                            allThumbResult.setVolume(dateBean.getVolume());
                            allThumbResult.setHigh(dateBean.getHigh());
                            allThumbResult.setChg(dateBean.getChg());
                            allThumbResult.setCnyLegalAsset(dateBean.getCnyLegalAsset());
                            allThumbResult.setLow(dateBean.getLow());
                            allThumbResult.setClose(dateBean.getClose());
                            allThumbResult.setTurnover(dateBean.getTurnover());
                            updateCoinPairPushBean();
                            break;
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e("Exception", e.toString());
                }
                break;
            //K线订阅
            case WsCMD.PUSH_KLINE:
                KlineSubscribeBean klineSubscribeBean = App.gson.fromJson(webSocketResponse.getResponse(), KlineSubscribeBean.class);
                if (!klineSubscribeBean.getPeriod().equals(period)) return;
                KLineEntity mKLineEntity = new KLineEntity();
                mKLineEntity.Date = DateUtils.formatKlineTime(type, klineSubscribeBean.getTime());
                mKLineEntity.Open = klineSubscribeBean.getOpenPrice();
                mKLineEntity.Close = klineSubscribeBean.getClosePrice();
                mKLineEntity.High = klineSubscribeBean.getHighestPrice();
                mKLineEntity.Low = klineSubscribeBean.getLowestPrice();
                mKLineEntity.Volume = klineSubscribeBean.getVolume();
                uc.klineSubscribe.setValue(mKLineEntity);
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //K线历史数据
            case EvKey.klineHistory:
                if (eventBean.isStatue()) {
                    senKlineHistoryDate((String) eventBean.getObject());
                }
                break;
            //币种缩略图推送
            case EvKey.cfdAllSymbolPush:
                synchronized (this) {
                    if (eventBean.isStatue()) {
                        for (CfdAllThumbResult.DataBean dataBean : ((CfdAllThumbResult) eventBean.getObject()).getData()) {
                            if (allThumbResult.getSymbol().equals(dataBean.getSymbol())) {
                                this.allThumbResult = dataBean;
                                updateCoinPairPushBean();
                                break;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void senKlineHistoryDate(String response) {
        try {
            mKLineEntities.clear();
            JSONObject object = new JSONObject(response);
            JSONArray jsonElements = object.getJSONArray("data");
            for (int i = 0, len = jsonElements.length(); i < len; i++) {
                JSONArray data = jsonElements.optJSONArray(i);
                KLineEntity mKLineEntity = new KLineEntity();
                mKLineEntity.Date = DateUtils.formatKlineTime(type, data.optLong(0));
                mKLineEntity.Open = (float) data.optDouble(1);
                mKLineEntity.Close = (float) data.optDouble(4);
                mKLineEntity.High = (float) data.optDouble(2);
                mKLineEntity.Low = (float) data.optDouble(3);
                mKLineEntity.Volume = (float) data.optDouble(5);
                mKLineEntities.add(mKLineEntity);
            }
            uc.klineHistoryList.setValue(mKLineEntities);

            //取消上一次Tab的订阅，订阅当前选中Tab
            if (!TextUtils.isEmpty(lastResolution)) {
                unSubscribeKline();
            }
            lastResolution = resolution;
            subscribeKline();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //实时更新
    private void updateCoinPairPushBean() {
        close.set(initClose());
        convert.set(initConvert());
        convertColor.set(isCoinPairPushChgUp());
        _24hourcount.set(init24HourCount());
        _24hourhigh.set(init24HourHigh());
        _24hourlow.set(init24HourLow());
    }

    // 涨幅是否为 +/-
    private boolean isCoinPairPushChgUp() {
        return allThumbResult.getChg() >= 0 ? true : false;
    }

    //现价
    private CharSequence initClose() {
        String close = DfUtils.formatNum(MathUtils.getRundNumber(allThumbResult.getClose(), allThumbResult.getBaseCoinScreenScale() == 0 ?
                2 : allThumbResult.getBaseCoinScreenScale(), null));
        if (!close.contains(".")) return close;
        CharSequence text = new SpanUtils()
                .append(close.split("\\.")[0])
                .setFontSize(16, true)
                .append("." + close.split("\\.")[1])
                .setFontSize(13, true)
                .create();
        return text;
    }

    //换算
    private CharSequence initConvert() {
        CharSequence text = new SpanUtils()
                .append("≈" + MathUtils.getRundNumber(allThumbResult.getCnyLegalAsset(), 2, null) + Constant.CNY)
                .setForegroundColor(ContextCompat.getColor(App.getInstance(), R.color.white))
                .append(" 涨幅" + (isCoinPairPushChgUp() ? "+" : "") + MathUtils.getRundNumber(allThumbResult.getChg() * 100, 2, "########0.") + "%")
                .setForegroundColor(isCoinPairPushChgUp() ? ContextCompat.getColor(App.getInstance(), R.color.green) : ContextCompat.getColor(App.getInstance(), R.color.red))
                .create();
        return text;
    }

    //24H量
    private String init24HourCount() {
        return String.valueOf((int) allThumbResult.getVolume());
    }

    //24H最高
    private String init24HourHigh() {
        return DfUtils.numberFormatWithZero(allThumbResult.getHigh(), allThumbResult.getBaseCoinScreenScale());
    }

    //24H最低
    private String init24HourLow() {
        return DfUtils.numberFormatWithZero(allThumbResult.getLow(), allThumbResult.getBaseCoinScreenScale());
    }

    //订阅本次实时k线
    public void subscribeKline() {
        WebSocketRequest mWebSocketRequest = new WebSocketRequest();
        mWebSocketRequest.setCode(WsConstant.CODE_KLINE);
        mWebSocketRequest.setCmd(WsCMD.SUBSCRIBE_KLINE);
        mWebSocketRequest.setBody(App.gson.toJson(WsUtils.setKlineSubscribeJsonMap(allThumbResult.getSymbol(), WsConstant.CFD, resolution)).getBytes());
        EventBusUtils.postEvent(mWebSocketRequest);
    }

    //取消订阅上次实时k线
    public void unSubscribeKline() {
        WebSocketRequest mWebSocketRequest = new WebSocketRequest();
        mWebSocketRequest.setCode(WsConstant.CODE_KLINE);
        mWebSocketRequest.setCmd(WsCMD.UN_SUBSCRIBE_KLINE);
        mWebSocketRequest.setBody(App.gson.toJson(WsUtils.setKlineSubscribeJsonMap(allThumbResult.getSymbol(), WsConstant.CFD, lastResolution)).getBytes());
        EventBusUtils.postEvent(mWebSocketRequest);
    }

    public void subscribeKlineVertical() {
        if (StringUtils.isEmpty(mResolutionVertical)) return;
        WebSocketRequest mWebSocketRequest = new WebSocketRequest();
        mWebSocketRequest.setCode(WsConstant.CODE_KLINE);
        mWebSocketRequest.setCmd(WsCMD.SUBSCRIBE_KLINE);
        mWebSocketRequest.setBody(App.gson.toJson(WsUtils.setKlineSubscribeJsonMap(allThumbResult.getSymbol(), WsConstant.SPOT, mResolutionVertical)).getBytes());
        EventBusUtils.postEvent(mWebSocketRequest);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Constant.isCfdKineHorizontalVisiable = true;
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constant.isCfdKineHorizontalVisiable = false;
        unSubscribeKline();
        subscribeKlineVertical();
        EventBusUtils.unRegister(this);
    }
}
