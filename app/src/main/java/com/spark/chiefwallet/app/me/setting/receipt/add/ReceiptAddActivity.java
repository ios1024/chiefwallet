package com.spark.chiefwallet.app.me.setting.receipt.add;

import android.graphics.Color;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityReceiptAddBinding;
import com.spark.chiefwallet.databinding.ActivityReceiptBinding;
import com.spark.chiefwallet.util.StatueBarUtils;

import me.spark.mvvm.base.BaseActivity;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_ME_RECEIPT_ADD)
public class ReceiptAddActivity extends BaseActivity<ActivityReceiptAddBinding, ReceiptAddViewModel> {
    private TitleBean mTitleModel;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_receipt_add;
    }

    @Override
    public int initVariableId() {
        return BR.receiptAddViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.receiptTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleName("添加");
        binding.receiptTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.receiptTitle.titleRootLeft);
    }
}
