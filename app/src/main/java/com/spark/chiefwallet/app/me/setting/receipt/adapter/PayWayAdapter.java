package com.spark.chiefwallet.app.me.setting.receipt.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.otcclient.pojo.PayListBean;

import java.util.List;

import me.spark.mvvm.base.Constant;

/**
 * ================================================
 * 作    者：ccs
 * 版    本：1.0.0
 * 创建日期：2019/7/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PayWayAdapter extends BaseQuickAdapter<PayListBean.DataBean, PayWayAdapter.PayWayViewHolder> {
    private OnSelectListener mOnSelectListener;

    public interface OnSelectListener {
        void onSelect(PayListBean.DataBean dataBean);
    }

    //设置监听
    /*public void setOnSelectListener(OnSelectListener listener) {
        this.mOnSelectListener = listener;
    }*/

    public PayWayAdapter(@Nullable List<PayListBean.DataBean> data) {
        super(R.layout.item_pay_way, data);
    }

    @Override
    protected void convert(final PayWayViewHolder helper, final PayListBean.DataBean item) {
        ViewDataBinding binding = helper.getBinding();
        binding.setVariable(BR.paywayBean, item);
        binding.executePendingBindings();

       /* helper.getView(R.id.pay_way_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //观察选中项
                mOnSelectListener.onSelect(item);
            }
        });*/

        switch (item.getPayType()) {
            case Constant.alipay:
                helper.setBackgroundColor(R.id.pay_way_root, App.getInstance().getResources().getColor(R.color.zfb_bg));
                helper.setImageResource(R.id.ivType, R.drawable.svg_alipay_white);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_alipay));
                helper.setVisible(R.id.ivQrCode, true);
                break;
            case Constant.wechat:
                helper.setBackgroundColor(R.id.pay_way_root, App.getInstance().getResources().getColor(R.color.wexin_bg));
                helper.setImageResource(R.id.ivType, R.drawable.svg_wechatpay_white);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_wechat));
                helper.setVisible(R.id.ivQrCode, true);

                break;
            case Constant.card:
                helper.setBackgroundColor(R.id.pay_way_root, App.getInstance().getResources().getColor(R.color.card_bg));
                helper.setImageResource(R.id.ivType, R.drawable.svg_bankpay_white);
                helper.setText(R.id.tvBankName, item.Bank());
                helper.setVisible(R.id.ivQrCode, false);

                break;
            case Constant.AfricaCard:
                helper.setBackgroundColor(R.id.pay_way_root, App.getInstance().getResources().getColor(R.color.card_bg));
                helper.setImageResource(R.id.ivType, R.mipmap.african_bank);
                helper.setText(R.id.tvBankName, item.Bank());
                helper.setVisible(R.id.ivQrCode, false);
                break;
            case Constant.PAYPAL:
                helper.setBackgroundColor(R.id.pay_way_root, App.getInstance().getResources().getColor(R.color.paypal_bg));
                helper.setImageResource(R.id.ivType, R.drawable.svg_pay_paypal_white);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_paypal));
                helper.setVisible(R.id.ivQrCode, false);

                break;
            case Constant.MTN:
                helper.setBackgroundColor(R.id.pay_way_root, App.getInstance().getResources().getColor(R.color.mtn_bg));
                helper.setImageResource(R.id.ivType, R.drawable.svg_pay_mtn);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_mtn));
                helper.setVisible(R.id.ivQrCode, false);

                break;
            case Constant.other:
                helper.setBackgroundColor(R.id.pay_way_root, App.getInstance().getResources().getColor(R.color.fall_dark));
                helper.setImageResource(R.id.ivType, R.drawable.svg_pay_other);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_other));
                helper.setVisible(R.id.ivQrCode, false);

                break;
            default:
                helper.setBackgroundColor(R.id.pay_way_root, App.getInstance().getResources().getColor(R.color.mtn_bg));
                helper.setImageResource(R.id.ivType, R.drawable.svg_pay_other);
                helper.setText(R.id.tvBankName, App.getInstance().getString(R.string.str_other));
                helper.setVisible(R.id.ivQrCode, false);

                break;
        }

        if (item.getStatus() == 1) {
            helper.getView(R.id.ivStatus).setSelected(true);
//            helper.setText(R.id.tvSwitch, R.string.open);
        } else {
            helper.getView(R.id.ivStatus).setSelected(false);
//            helper.setText(R.id.tvSwitch, R.string.shut);
        }
        helper.addOnClickListener(R.id.ivStatus);
        helper.addOnClickListener(R.id.tvupdate);
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
