package com.spark.chiefwallet.app.home;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.spark.chiefwallet.App;
import com.spark.chiefwallet.api.AppClient;
import com.spark.wsclient.base.WsCMD;
import com.spark.wsclient.pojo.RecommendCoinBean;
import com.spark.wsclient.utils.WsConstant;
import com.spark.wsclient.utils.WsUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LogUtils;
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
                } catch (Exception e) {
                    LogUtils.e("PUSH_THUMB", e.toString());
                }
                break;
            default:
                break;
        }
    }


    private void getRecommendCoinWS() {
        WebSocketRequest mWebSocketRequest = new WebSocketRequest();
        mWebSocketRequest.setCode(WsConstant.CODE_KLINE);
        mWebSocketRequest.setCmd(WsCMD.GET_RECOMMEND_THUMB);
        mWebSocketRequest.setBody(App.gson.toJson(WsUtils.setSubscribeThumbSPOTJsonMap()).getBytes());
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
