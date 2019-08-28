package com.spark.chiefwallet.app.home.otc_quotes.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.chiefwallet.R;
import com.spark.wsclient.pojo.B2BThumbBean;

import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/7
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class OtcquotesAdapter extends BaseQuickAdapter<B2BThumbBean.DateBean, OtcquotesAdapter.CoinPairPushBeanViewHolder> {
    public OtcquotesAdapter(@Nullable List<B2BThumbBean.DateBean> data) {
        super(R.layout.item_otc_quotes, data);
    }

    @Override
    protected void convert(CoinPairPushBeanViewHolder helper, B2BThumbBean.DateBean item) {
        ViewDataBinding binding = helper.getBinding();
        binding.setVariable(BR.itemOtcQuotes, item);
        binding.executePendingBindings();
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

    public static class CoinPairPushBeanViewHolder extends BaseViewHolder {

        public CoinPairPushBeanViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }
}
