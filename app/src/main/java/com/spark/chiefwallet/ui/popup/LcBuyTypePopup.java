package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.lxj.xpopup.core.AttachPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.popup.impl.OnTypeChooseListener;

import butterknife.BindView;
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
public class LcBuyTypePopup extends AttachPopupView {
    @BindView(R.id.by_price)
    TextView byPrice;
    @BindView(R.id.by_num)
    TextView byNum;

    private OnTypeChooseListener mOnTypeChooseListener;
    private int mTradeType;// 1 - 买  0 - 卖


    public LcBuyTypePopup(@NonNull Context context, OnTypeChooseListener onTypeChooseListener, int mTradeType) {
        super(context);
        this.mOnTypeChooseListener = onTypeChooseListener;
        this.mTradeType = mTradeType;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_lc_buy_type;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        if (mTradeType == 1) {
            byPrice.setText("按价格购买");
            byNum.setText("按数量购买");
        } else {
            byPrice.setText("按价格出售");
            byNum.setText("按数量出售");
        }
    }

    @OnClick({R.id.by_price,
            R.id.by_num})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.by_price:
                mOnTypeChooseListener.onChooseType(0, byPrice.getText().toString());
                dismiss();
                break;
            case R.id.by_num:
                mOnTypeChooseListener.onChooseType(1, byNum.getText().toString());
                dismiss();
                break;
        }
    }

}
