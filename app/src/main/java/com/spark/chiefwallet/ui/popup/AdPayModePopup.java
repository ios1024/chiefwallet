package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.lxj.xpopup.core.AttachPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.popup.impl.OnTypeChooseListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AdPayModePopup extends AttachPopupView {
    private OnTypeChooseListener mOnTypeChooseListener;

    public AdPayModePopup(@NonNull Context context, OnTypeChooseListener mOnTypeChooseListener) {
        super(context);
        this.mOnTypeChooseListener = mOnTypeChooseListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_ad_pay_mode;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
    }

    @OnClick({R.id.mode_alipay,
            R.id.mode_wechatpay,
            R.id.mode_bankpay})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.mode_alipay:
                mOnTypeChooseListener.onChooseType(0, "支付宝");
                break;
            case R.id.mode_wechatpay:
                mOnTypeChooseListener.onChooseType(1, "微信");
                break;
            case R.id.mode_bankpay:
                mOnTypeChooseListener.onChooseType(2, "银行卡");
                break;
        }
        dismiss();
    }
}
