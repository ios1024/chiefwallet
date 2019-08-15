package com.spark.chiefwallet.app.quotes;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.modulespot.SpotCoinClient;
import com.spark.modulespot.pojo.SpotCoinResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/18
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class QuotesViewModel extends BaseViewModel {
    public QuotesViewModel(@NonNull Application application) {
        super(application);
    }

    private OnRequestListener onRequestListenerCoinAll;
    private Context context;
    private SpotCoinResult mSpotCoinResult;
    public boolean isVisible2User = false;
    public boolean isOnPause = true;

    public void getSpotCoinAll(Context context, OnRequestListener onRequestListener) {
        this.onRequestListenerCoinAll = onRequestListener;
        this.context = context;
        SpotCoinClient.getInstance().getSpotCoinAll();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        if ((!isVisible2User || isOnPause) && (!eventBean.getOrigin().equals(EvKey.loginStatue)))
            return;
        switch (eventBean.getOrigin()) {
            //查询所有的币种
            case EvKey.spotCoinAll:
                if (eventBean.isStatue()) {
                    Constant.coinPairThumbBeanList.clear();
                    Constant.coinPairThumbBeanList.add(App.getInstance().getString(R.string.favorites));
                    Constant.spotJson = App.gson.toJson(eventBean.getObject());
                    for (SpotCoinResult.DataBean dataBean : ((SpotCoinResult) eventBean.getObject()).getData()) {
                        if (!Constant.coinPairThumbBeanList.contains(dataBean.getBaseSymbol()))
                            Constant.coinPairThumbBeanList.add(dataBean.getBaseSymbol());
                    }
                    onRequestListenerCoinAll.onSuccess(eventBean.getObject());

                } else {
                    onRequestListenerCoinAll.onFail(eventBean.getMessage());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isVisible2User = true;
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }

}
