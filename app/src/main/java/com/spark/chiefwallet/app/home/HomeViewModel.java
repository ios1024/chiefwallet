package com.spark.chiefwallet.app.home;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.api.AppClient;
import com.spark.wsclient.base.WsCMD;
import com.spark.wsclient.pojo.B2BThumbBean;
import com.spark.wsclient.pojo.RecommendCoinBean;
import com.spark.wsclient.utils.WsConstant;
import com.spark.wsclient.utils.WsUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.SPUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.WebSocketRequest;
import me.spark.mvvm.utils.WebSocketResponse;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019-08-16
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class HomeViewModel extends BaseViewModel {
    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    private OnRequestListener onRequestListener, onRequestListenerAnnounce, onRequestListenerRecommendCoin;
    private Context mContext;
    public boolean isGetRecommendCoin = false;
    public ObservableField<CharSequence> chartSmybol = new ObservableField<>();
    public ObservableField<Boolean> riseOrFall = new ObservableField<>(false);
    public ObservableField<String> chartChg = new ObservableField<>();
    public ObservableField<String> chartClose = new ObservableField<>();

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<String> closeValue = new SingleLiveEvent<>();
    }

    /**
     * banner
     *
     * @param context
     * @param onRequestListener
     */
    public void loadBanner(Context context, OnRequestListener onRequestListener) {
        this.mContext = context;
        this.onRequestListener = onRequestListener;
        AppClient.getInstance().getBanner();
    }

    /**
     * 公告
     *
     * @param context
     * @param onRequestListener
     */
    public void loadAnnounce(Context context, OnRequestListener onRequestListener) {
        this.mContext = context;
        this.onRequestListenerAnnounce = onRequestListener;
        AppClient.getInstance().getAnnounce();
    }

    /**
     * 推荐币种
     *
     * @param onRequestListener
     */
    public void loadRecommend(OnRequestListener onRequestListener) {
        this.onRequestListenerRecommendCoin = onRequestListener;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            case EvKey.banner:
                if (eventBean.isStatue()) {
                    onRequestListener.onSuccess(eventBean.getObject());
                } else {
                    onRequestListener.onFail(eventBean.getMessage());
                }
                break;
            case EvKey.announce:
                if (eventBean.isStatue()) {
                    onRequestListenerAnnounce.onSuccess(eventBean.getObject());
                } else {
                    onRequestListenerAnnounce.onFail(eventBean.getMessage());
                }
                break;
            case EvKey.klineConnectSuccess:
                getOtcThumb();
                if (isGetRecommendCoin) return;
                getRecommendCoinWS();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebSocketResponse webSocketResponse) {
        switch (webSocketResponse.getCmd()) {
            //推荐币种
            case WsCMD.GET_RECOMMEND_THUMB:
                try {
                    RecommendCoinBean recommendCoinBean = App.gson.fromJson(webSocketResponse.getResponse(), RecommendCoinBean.class);
                    onRequestListenerRecommendCoin.onSuccess(recommendCoinBean);
                    LogUtils.e("recommendCoinBean", webSocketResponse.getResponse());
                } catch (Exception e) {
                    LogUtils.e("PUSH_THUMB", e.toString());
                }
                break;
            case WsCMD.OTC_PUSH_THUMB:
                try {
                    String json = "{\"date\":" + webSocketResponse.getResponse() + "}";
                    B2BThumbBean b2BThumbBean = App.gson.fromJson(json, B2BThumbBean.class);
                    String currencytype;
                    //1.人民币 CNY 2.美元 USDT 3.欧元 EUR 4.赛地 GHS 5.尼日利亚 NGN
                    switch (SPUtils.getInstance().getPricingCurrency()) {
                        case "1":
                            currencytype = Constant.CNY;
                            break;
                        case "2":
                            currencytype = Constant.USD;
                            break;
                        case "3":
                            currencytype = Constant.EUR;
                            break;
                        case "4":
                            currencytype = Constant.GHS;
                            break;
                        case "5":
                            currencytype = Constant.NGN;
                            break;
                        default:
                            currencytype = Constant.CNY;
                            break;
                    }
                    for (B2BThumbBean.DateBean dateBean : b2BThumbBean.getDate()) {


                        if (dateBean.getSymbol().equals("BTC/" + currencytype)) {
                            updateChart(dateBean);
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e("PUSH_THUMB", e.toString());
                }
                break;
            default:
                break;
        }
    }

    private void updateChart(B2BThumbBean.DateBean dateBean) {
        CharSequence text = new SpanUtils()
                .append(dateBean.getSymbol().split("/")[0])
                .append("/" + dateBean.getSymbol().split("/")[1])
                .setForegroundColor(ContextCompat.getColor(mContext, R.color.gray_b2))
                .setFontSize(14, true)
                .create();
        chartSmybol.set(text);
        riseOrFall.set(dateBean.getChg() >= 0);
        chartChg.set((riseOrFall.get() ? "+" : "") +
                MathUtils.getRundNumber(dateBean.getChg() * 100, 2, "########0.")
                + "%");
        chartClose.set(DfUtils.formatNum(MathUtils.getRundNumber(dateBean.getClose(), 2, null)));
        uc.closeValue.setValue(DfUtils.numberFormatDown(dateBean.getClose(), 2));
    }


    private void getRecommendCoinWS() {
        WebSocketRequest mWebSocketRequest = new WebSocketRequest();
        mWebSocketRequest.setCode(WsConstant.CODE_KLINE);
        mWebSocketRequest.setCmd(WsCMD.GET_RECOMMEND_THUMB);
        mWebSocketRequest.setBody(App.gson.toJson(WsUtils.setSubscribeThumbSPOTJsonMap()).getBytes());
        EventBusUtils.postEvent(mWebSocketRequest);
    }

    private void getOtcThumb() {
        WebSocketRequest mWebSocketRequest = new WebSocketRequest();
        mWebSocketRequest.setCode(WsConstant.CODE_KLINE);
        mWebSocketRequest.setCmd(WsCMD.OTC_SUBSCRIBE_THUMB);
        mWebSocketRequest.setBody(App.gson.toJson(WsUtils.setSubscribeThumbOTCJsonMap()).getBytes());
        EventBusUtils.postEvent(mWebSocketRequest);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegister(this);
    }
}
