package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.core.BottomPopupView;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class TradePwdPopup extends BottomPopupView {
    @BindView(R.id.trade_pwd)
    EditText mTradePwd;
    @BindView(R.id.trade_title)
    TextView mTradeTitle;
    private String mTitle;

    private OnEtContentListener mOnEtContentListener;

    public TradePwdPopup(@NonNull Context context, OnEtContentListener mOnEtContentListener) {
        super(context);
        this.mOnEtContentListener = mOnEtContentListener;
    }

    public TradePwdPopup(@NonNull Context context, String mTitle, OnEtContentListener mOnEtContentListener) {
        super(context);
        this.mTitle = mTitle;
        this.mOnEtContentListener = mOnEtContentListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_trade_pwd;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
    }

    @Override
    public void init() {
        super.init();
        if (mTradeTitle != null && StringUtils.isNotEmpty(mTitle)) {
            mTradeTitle.setText(mTitle);
        }
    }

    @OnClick({R.id.btn_ensure, R.id.btn_cancel, R.id.btn_forget})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure:
                if (StringUtils.isEmpty(mTradePwd.getText().toString().trim())) {
                    Toasty.showError(App.getInstance().getString(R.string.trade_pwd_hint));
                } else {
                    dismiss();
                    mOnEtContentListener.onCEtContentInput(mTradePwd.getText().toString().trim());
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_forget:
                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_TRADE_PWD_FORGET)
                        .navigation();
                break;
        }
    }

}
