package com.spark.chiefwallet.app.me.finance.record.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.chiefwallet.R;
import com.spark.ucclient.pojo.RecordPageResult;

import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/1
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class InviteFriendAdapter extends BaseQuickAdapter<RecordPageResult.DataBean.RecordsBean, InviteFriendAdapter.InviteFriendViewHolder> {
    public InviteFriendAdapter(@Nullable List<RecordPageResult.DataBean.RecordsBean> data) {
        super(R.layout.item_invite_friend, data);
    }

    @Override
    protected void convert(InviteFriendViewHolder helper, RecordPageResult.DataBean.RecordsBean item) {
        ViewDataBinding binding = helper.getBinding();
        binding.setVariable(BR.inviteFriendVM, item);
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

    public static class InviteFriendViewHolder extends BaseViewHolder {

        public InviteFriendViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) itemView.getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }
}
