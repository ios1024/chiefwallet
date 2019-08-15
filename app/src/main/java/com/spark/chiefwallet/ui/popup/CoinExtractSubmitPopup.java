package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lxj.xpopup.core.BottomPopupView;
import com.spark.acclient.pojo.CoinExtractBean;
import com.spark.acclient.pojo.CoinExtractSubmitBean;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.popup.impl.OnCoinExtractSubmitListener;
import com.spark.chiefwallet.ui.toast.Toasty;

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
public class CoinExtractSubmitPopup extends BottomPopupView {
    @BindView(R.id.address)
    TextView mAddress;
    @BindView(R.id.tag)
    TextView mTag;
    @BindView(R.id.number)
    TextView mNumber;
    @BindView(R.id.service_charge)
    TextView mServiceCharge;
    @BindView(R.id.trade_pwd)
    EditText mTradePwd;
    @BindView(R.id.submit)
    TextView mSubmit;
    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.price)
    TextView mPrice;

    private CoinExtractSubmitBean mCoinExtractSubmitBean;
    private OnCoinExtractSubmitListener mOnCoinExtractSubmitListener;

    public CoinExtractSubmitPopup(@NonNull Context context, CoinExtractSubmitBean coinExtractSubmitBean, OnCoinExtractSubmitListener onCoinExtractSubmitListener) {
        super(context);
        this.mCoinExtractSubmitBean = coinExtractSubmitBean;
        this.mOnCoinExtractSubmitListener = onCoinExtractSubmitListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_extract_coin_submit;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mAddress.setText(mCoinExtractSubmitBean.getAddress());
        mTag.setText(mCoinExtractSubmitBean.getTag());
        mNumber.setText(mCoinExtractSubmitBean.getAmount());
        mServiceCharge.setText(mCoinExtractSubmitBean.getServiceCharge() + mCoinExtractSubmitBean.getCoinName());
        mPrice.setText(String.valueOf(Double.valueOf(mCoinExtractSubmitBean.getAmount()) - Double.valueOf(mCoinExtractSubmitBean.getServiceCharge())) + mCoinExtractSubmitBean.getCoinName());
    }

    @OnClick({R.id.submit,
            R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if (TextUtils.isEmpty(mTradePwd.getText().toString())) {
                    Toasty.showError(App.getInstance().getString(R.string.trade_pwd_hint));
                    return;
                }

                CoinExtractBean coinExtractBean = new CoinExtractBean();
                coinExtractBean.setAddress(mCoinExtractSubmitBean.getAddress());
                coinExtractBean.setAmount(Double.valueOf(mCoinExtractSubmitBean.getAmount()));
                coinExtractBean.setCoinName(mCoinExtractSubmitBean.getCoinName());
                coinExtractBean.setTradePassword(mTradePwd.getText().toString().trim());
                mOnCoinExtractSubmitListener.onReceiveCoinExtractSubmit(coinExtractBean);
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
