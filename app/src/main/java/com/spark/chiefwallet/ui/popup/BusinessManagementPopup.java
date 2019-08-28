package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.core.AttachPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BusinessManagementPopup extends AttachPopupView {


    public BusinessManagementPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {

        return R.layout.activity_business_management_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);

    }

    @OnClick({R.id.lc_buy,
            R.id.lc_sell})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lc_buy:
                ARouter.getInstance().build(ARouterPath.ACTIVITY_TRADE_AD_NEWCREATE)
                        .navigation();
                break;
            case R.id.lc_sell:
                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_CERTIFICATIONDETAILS)
                        .navigation();
                break;
        }
        dismiss();
    }


}
