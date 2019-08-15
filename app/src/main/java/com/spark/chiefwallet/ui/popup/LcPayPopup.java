package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxj.xpopup.core.BottomPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.api.pojo.PayTypeBean;
import com.spark.chiefwallet.ui.SmoothCheckBox;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.LcTradeClient;
import com.spark.otcclient.pojo.LcOrderResult;
import com.spark.otcclient.pojo.OrderPayBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.DfUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LcPayPopup extends BottomPopupView {
    @BindView(R.id.title_sub)
    TextView mTitleSub;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.num)
    TextView mNum;
    @BindView(R.id.account)
    TextView mAccount;
    @BindView(R.id.wechat_cb)
    SmoothCheckBox mWechatCb;
    @BindView(R.id.alipay_cb)
    SmoothCheckBox mAlipayCb;
    @BindView(R.id.bank_cb)
    SmoothCheckBox mBankCb;
    @BindView(R.id.ensure)
    TextView mEnsure;
    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.ll_wechat)
    LinearLayout mLlWechat;
    @BindView(R.id.ll_alipay)
    LinearLayout mLlAlipay;
    @BindView(R.id.ll_bank)
    LinearLayout mLlBank;

    private LcOrderResult.DataBean.RecordsBean mRecordsBean;
    private PayTypeBean mPayTypeBean;
    private String actualPayment;

    public LcPayPopup(@NonNull Context context, LcOrderResult.DataBean.RecordsBean mRecordsBean, PayTypeBean mPayTypeBean) {
        super(context);
        this.mRecordsBean = mRecordsBean;
        this.mPayTypeBean = mPayTypeBean;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_lc_pay;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleSub.setText("下单后此交易的" + mRecordsBean.getCoinName() + "将托管锁定请放心购买");
        mPrice.setText(DfUtils.numberFormat(mRecordsBean.getPrice(), mRecordsBean.getPrice() == 0 ? 0 : 8) + " CNY");
        mNum.setText(DfUtils.numberFormat(mRecordsBean.getNumber(), mRecordsBean.getNumber() == 0 ? 0 : 8) + " " + mRecordsBean.getCoinName());
        mAccount.setText(DfUtils.numberFormat(mRecordsBean.getMoney(), mRecordsBean.getMoney() == 0 ? 0 : 8) + " CNY");
        for (PayTypeBean.PayTypeBeanBean payTypeBeanBean : mPayTypeBean.getPayTypeBean()) {
            if (payTypeBeanBean.getPayType().contains(Constant.wechat)) {
                mLlWechat.setVisibility(VISIBLE);
            } else if (payTypeBeanBean.getPayType().contains(Constant.alipay)) {
                mLlAlipay.setVisibility(VISIBLE);
            } else if (payTypeBeanBean.getPayType().contains(Constant.card)) {
                mLlBank.setVisibility(VISIBLE);
            }
        }

        initCb();
    }

    private void initCb() {
        mWechatCb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    mAlipayCb.setChecked(false);
                    mBankCb.setChecked(false);
                    actualPayment = Constant.wechat;
                }
            }
        });

        mAlipayCb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    mWechatCb.setChecked(false);
                    mBankCb.setChecked(false);
                    actualPayment = Constant.alipay;
                }
            }
        });

        mBankCb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    mWechatCb.setChecked(false);
                    mAlipayCb.setChecked(false);
                    actualPayment = Constant.card;
                }
            }
        });
    }

    @OnClick({R.id.ensure,
            R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ensure:
                if (mWechatCb.isChecked() || mAlipayCb.isChecked() || mBankCb.isChecked()) {
                    OrderPayBean orderPayBean = new OrderPayBean();
                    orderPayBean.setActualPayment(actualPayment);
                    orderPayBean.setOrderSn(mRecordsBean.getOrderSn());
                    LcTradeClient.getInstance().orderPayMent(orderPayBean);
                    dismiss();
                } else {
                    Toasty.showError("请先选择支付方式！");
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
