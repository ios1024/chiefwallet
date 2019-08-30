package com.spark.chiefwallet.ui.popup.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.ucclient.pojo.CountryEntity2;

import java.util.List;

import me.spark.mvvm.utils.LanguageSPUtil;

public class ChoiceOfNationalityAdapter extends BaseQuickAdapter<CountryEntity2, ChoiceOfNationalityAdapter.ViewHolder> {


    public ChoiceOfNationalityAdapter(@Nullable List<CountryEntity2> data) {
        super(R.layout.activity_choice_of_nationality_adapter, data);
    }

    @Override
    protected void convert(ViewHolder helper, CountryEntity2 item) {
        ViewDataBinding binding = helper.getBinding();
        binding.setVariable(BR.countryEntity, item);
        binding.executePendingBindings();


        switch (LanguageSPUtil.getInstance(App.getInstance()).getSelectLanguage()) {
            case 1://中文
                helper.setText(R.id.zhName, item.getZhName());
                break;
            case 0://英文
                helper.setText(R.id.zhName, item.getEnName());
                break;
            default:
                helper.setText(R.id.zhName, item.getZhName());
                break;
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

    public static class ViewHolder extends BaseViewHolder {

        public ViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }

}
