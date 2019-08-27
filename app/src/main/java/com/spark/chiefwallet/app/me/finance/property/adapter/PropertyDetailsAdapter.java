package com.spark.chiefwallet.app.me.finance.property.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.acclient.pojo.CoinTransDetailsResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;

import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/31
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PropertyDetailsAdapter extends BaseQuickAdapter<CoinTransDetailsResult.DataBean.RecordsBean, PropertyDetailsAdapter.PropertyDetailsViewHolder> {
    public PropertyDetailsAdapter(@Nullable List<CoinTransDetailsResult.DataBean.RecordsBean> data) {
        super(R.layout.item_property_details, data);
    }

    @Override
    protected void convert(PropertyDetailsViewHolder helper, CoinTransDetailsResult.DataBean.RecordsBean item) {
        ViewDataBinding binding = helper.getBinding();
        binding.setVariable(BR.propertyDetails, item);
        binding.executePendingBindings();

        if (item.getSubType() == 5 || item.getSubType() == 3) {
            helper.setVisible(R.id.tv_typeall, false);
            helper.setVisible(R.id.ll_type, true);
            helper.setVisible(R.id.tv_formatDate, false);
            helper.setText(R.id.tv_state, "时间");
            helper.setText(R.id.tv_coinName, "币种");
            helper.setText(R.id.tv_formatStatue, item.formatDate());
            helper.setText(R.id.tv_formatFee, item.getCoinName());
        }
        if (item.getSubType() == 3) {//币币转法币
            helper.setText(R.id.tv_type1, "币币");
            helper.setTextColor(R.id.tv_type1, App.getInstance().getResources().getColor(R.color.card_bg));
            helper.setText(R.id.tv_type2, "法币");
            helper.setTextColor(R.id.tv_type2, App.getInstance().getResources().getColor(R.color.zfb_bg));

        } else if (item.getSubType() == 5) {
            helper.setText(R.id.tv_type1, "法币");
            helper.setTextColor(R.id.tv_type1, App.getInstance().getResources().getColor(R.color.zfb_bg));
            helper.setText(R.id.tv_type2, "币币");
            helper.setTextColor(R.id.tv_type2, App.getInstance().getResources().getColor(R.color.card_bg));

        } else
            helper.setVisible(R.id.ll_type, false);


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

    public static class PropertyDetailsViewHolder extends BaseViewHolder {

        public PropertyDetailsViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }
}
