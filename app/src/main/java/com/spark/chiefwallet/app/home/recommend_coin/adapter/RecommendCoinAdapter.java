package com.spark.chiefwallet.app.home.recommend_coin.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.chiefwallet.R;
import com.spark.wsclient.pojo.RecommendCoinBean;

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
public class RecommendCoinAdapter extends BaseQuickAdapter<RecommendCoinBean.DataBean, RecommendCoinAdapter.RecommendCoinViewHolder> {
    private List<RecommendCoinBean.DataBean> mDataBeans;

    public RecommendCoinAdapter(@Nullable List<RecommendCoinBean.DataBean> data) {
        super(R.layout.item_recommend_coin, data);
        this.mDataBeans = data;
    }

    @Override
    protected void convert(RecommendCoinViewHolder helper, RecommendCoinBean.DataBean item) {
        ViewDataBinding binding = helper.getBinding();
        binding.setVariable(BR.itemRecommendCoin, item);
        binding.executePendingBindings();
        if (helper.getAdapterPosition() == mDataBeans.size() - 1) {
            helper.getView(R.id.line).setVisibility(View.GONE);
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

    public static class RecommendCoinViewHolder extends BaseViewHolder {

        public RecommendCoinViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }
}
