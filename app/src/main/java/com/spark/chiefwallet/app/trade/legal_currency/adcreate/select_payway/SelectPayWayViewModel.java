package com.spark.chiefwallet.app.trade.legal_currency.adcreate.select_payway;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.spark.otcclient.PayControlClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/29
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SelectPayWayViewModel extends BaseViewModel {
    public SelectPayWayViewModel(@NonNull Application application) {
        super(application);
    }

    private OnRequestListener mOnRequestListener;

    public ObservableField<String> titleText = new ObservableField<>("请选择收款方式");
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> isRefresh = new SingleLiveEvent<>();
    }

    /**
     * 获取收款方式
     */
    public void getCoinAddressList(OnRequestListener onRequestListener) {
        this.mOnRequestListener = onRequestListener;
        PayControlClient.getInstance().queryList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //查询用户的某个币种的提币地址信息
            case EvKey.payList:
                if (mOnRequestListener == null) return;
                if (eventBean.isStatue()) {
                    mOnRequestListener.onSuccess(eventBean.getObject());
                } else {
                    mOnRequestListener.onFail(eventBean.getMessage());
                }
                break;
            case EvKey.logout_success_401:
                if (eventBean.isStatue()) {
                    uc.isRefresh.setValue(true);
                }
                break;
            default:
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
