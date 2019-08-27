package com.spark.chiefwallet.app.me.safe.business.surrender;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.LcTradeClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

public class SurrenderViewModel extends BaseViewModel {
    public SurrenderViewModel(@NonNull Application application) {
        super(application);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            case EvKey.application_for_reinsurance_refund:
                dismissDialog();
                if (eventBean.isStatue()) {
//                    ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_CERTIFICATIONDETAILS)
//                            .navigation();

                    Toasty.showSuccess(eventBean.getMessage());
                    showDialog("请求中");
                    LcTradeClient.getInstance().getBusinessFind();
                    finish();
                } else
                    Toasty.showError(eventBean.getMessage());
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
