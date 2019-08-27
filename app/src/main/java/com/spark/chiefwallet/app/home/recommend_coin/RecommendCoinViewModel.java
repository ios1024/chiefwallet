package com.spark.chiefwallet.app.home.recommend_coin;

import android.app.Application;
import android.support.annotation.NonNull;

import com.spark.modulespot.pojo.AllThumbResult;
import com.spark.wsclient.pojo.RecommendCoinBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019-08-16
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RecommendCoinViewModel extends BaseViewModel {
    private List<RecommendCoinBean.DataBean> mDataBeans = new ArrayList<>();

    public RecommendCoinViewModel(@NonNull Application application) {
        super(application);
    }

    public void initRecommendCoinData(List<RecommendCoinBean.DataBean> dataBeans) {
        this.mDataBeans = dataBeans;
    }


    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<AllThumbResult> mAllThumbResultSingleLiveEvent = new SingleLiveEvent<>();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //成交全币种缩略图
            case EvKey.klineThumbAll:
                synchronized (this) {
                    if (eventBean.isStatue()) {
                        uc.mAllThumbResultSingleLiveEvent.setValue((AllThumbResult) eventBean.getObject());
                    }
                }
                break;
        }
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
