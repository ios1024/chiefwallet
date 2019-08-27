package com.spark.chiefwallet.app.trade.legal_currency.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.SmoothCheckBox;
import com.spark.otcclient.pojo.PayListBean;

import java.util.List;

import me.spark.mvvm.base.Constant;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/22
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SelectPayWayAdapter extends BaseQuickAdapter<PayListBean.DataBean, SelectPayWayAdapter.PayWayViewHolder> {
    private OnSelectListener mOnSelectListener;

    private int card = 0;
    private int alipay = 0;
    private int wechat = 0;
    private int PAYPAL = 0;
    private int MTN = 0;
    private int other = 0;

    public interface OnSelectListener {
        void onSelect(PayListBean.DataBean dataBean);
    }

    //设置监听
    public void setOnSelectListener(OnSelectListener listener) {
        this.mOnSelectListener = listener;
    }

    public SelectPayWayAdapter(@Nullable List<PayListBean.DataBean> data) {
        super(R.layout.item_select_pay_way, data);
    }

    @Override
    protected void convert(final PayWayViewHolder helper, final PayListBean.DataBean item) {
        ViewDataBinding binding = helper.getBinding();
        binding.setVariable(BR.paywayBean, item);
        binding.executePendingBindings();

        ((SmoothCheckBox) helper.getView(R.id.pay_way_select)).setChecked(true);
        helper.getView(R.id.pay_way_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //观察选中项
                mOnSelectListener.onSelect(item);
            }
        });

        ((SmoothCheckBox) helper.getView(R.id.pay_way_select)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //观察选中项
                mOnSelectListener.onSelect(item);
            }
        });

        if (item.getPayType().equals(Constant.card) && card == 0) {
            helper.setVisible(R.id.ll_ivt, true);
            helper.setImageResource(R.id.ivType2, R.drawable.svg_bankpay);
            helper.setText(R.id.tvBankName2, App.getInstance().getString(R.string.str_bank));
            card = 1;
        }

        if (item.getPayType().equals(Constant.alipay) && alipay == 0) {
            helper.setVisible(R.id.ll_ivt, true);
            helper.setImageResource(R.id.ivType2, R.drawable.svg_alipay);
            helper.setText(R.id.tvBankName2, App.getInstance().getString(R.string.str_alipay));
            alipay = 1;
        }
        if (item.getPayType().equals(Constant.wechat) && wechat == 0) {
            helper.setVisible(R.id.ll_ivt, true);
            helper.setImageResource(R.id.ivType2, R.drawable.svg_wechatpay);
            helper.setText(R.id.tvBankName2, App.getInstance().getString(R.string.str_wechat));
            wechat = 1;
        }
        if (item.getPayType().equals(Constant.PAYPAL) && PAYPAL == 0) {
            helper.setVisible(R.id.ll_ivt, true);
            helper.setImageResource(R.id.ivType2, R.drawable.svg_pay_paypal);
            helper.setText(R.id.tvBankName2, App.getInstance().getString(R.string.str_paypal));
            PAYPAL = 1;
        }
        if (item.getPayType().equals(Constant.MTN) && MTN == 0) {
            helper.setVisible(R.id.ll_ivt, true);
            helper.setImageResource(R.id.ivType2, R.drawable.svg_pay_mtn);
            helper.setText(R.id.tvBankName2, App.getInstance().getString(R.string.str_mtn));
            MTN = 1;
        }
        if (item.getPayType().equals(Constant.other) && other == 0) {
            helper.setVisible(R.id.ll_ivt, true);
            helper.setImageResource(R.id.ivType2, R.drawable.svg_pay_other);
            helper.setText(R.id.tvBankName2, App.getInstance().getString(R.string.str_other));
            other = 1;
        }
        switch (item.getPayType()) {
            case Constant.alipay:
                if (item.getIsSelected() == 1) {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.base));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.base));
                } else {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.black));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.black));
                }
                helper.setImageResource(R.id.ivType, R.drawable.svg_alipay);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_alipay));
                break;
            case Constant.wechat:
                if (item.getIsSelected() == 1) {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.base));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.base));
                } else {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.black));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.black));
                }
                helper.setImageResource(R.id.ivType, R.drawable.svg_wechatpay);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_wechat));
                break;
            case Constant.card:
                if (item.getIsSelected() == 1) {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.base));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.base));
                } else {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.black));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.black));
                }
                helper.setImageResource(R.id.ivType, R.drawable.svg_bankpay);
                helper.setText(R.id.tvBankName, item.getBank());
                break;
            case Constant.PAYPAL:
                if (item.getIsSelected() == 1) {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.base));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.base));
                } else {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.black));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.black));
                }
                helper.setImageResource(R.id.ivType, R.drawable.svg_pay_paypal);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_paypal));
                break;
            case Constant.MTN:
                if (item.getIsSelected() == 1) {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.base));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.base));
                } else {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.black));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.black));
                }
                helper.setImageResource(R.id.ivType, R.drawable.svg_pay_mtn);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_mtn));
                break;
            case Constant.other:
                if (item.getIsSelected() == 1) {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.base));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.base));
                } else {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.black));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.black));
                }
                helper.setImageResource(R.id.ivType, R.drawable.svg_pay_other);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_other));
                break;
            default:
                if (item.getIsSelected() == 1) {
                    helper.setTextColor(R.id.tvName, App.getInstance().getResources().getColor(R.color.base));
                    helper.setTextColor(R.id.tvBankNum, App.getInstance().getResources().getColor(R.color.base));
                }
                helper.setImageResource(R.id.ivType, R.drawable.svg_pay_other);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_other));
                break;
        }
        if (item.getIsSelected() == 1) {
            helper.setVisible(R.id.pay_way_select, true);
        } else {
            helper.setVisible(R.id.pay_way_select, false);
        }

    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        if (binding == null) {
            return super.getItemView(layoutResId, parent);
        }
        View view = binding.getRoot();
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding);
        return view;
    }

    public static class PayWayViewHolder extends BaseViewHolder {

        public PayWayViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }

    public void update() {
        notifyDataSetChanged();
    }
}
