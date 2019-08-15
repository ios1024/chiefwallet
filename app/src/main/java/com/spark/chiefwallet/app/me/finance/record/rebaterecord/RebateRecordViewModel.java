package com.spark.chiefwallet.app.me.finance.record.rebaterecord;

import android.app.Application;
import android.support.annotation.NonNull;

import com.spark.ucclient.MemberClient;

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
 * 创建日期：2019-07-24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RebateRecordViewModel extends BaseViewModel {
    public RebateRecordViewModel(@NonNull Application application) {
        super(application);
    }

    private OnRequestListener mOnRequestListener;

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> isRefresh = new SingleLiveEvent<>();
    }

    public void getDate(int page, OnRequestListener onRequestListener) {
        this.mOnRequestListener = onRequestListener;
        MemberClient.getInstance().getCommissionRecord(page);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //查询佣金记录
            case EvKey.commissionRecord:
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
