package com.spark.chiefwallet.app.me.finance.order;

import android.graphics.Color;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityOrderBinding;
import com.spark.chiefwallet.util.StatueBarUtils;

import me.spark.mvvm.base.BaseActivity;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/29
 * 描    述：
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_ME_ORDER)
public class OrderActivity extends BaseActivity<ActivityOrderBinding, OrderViewModel> {
    private TitleBean mTitleModel;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_order;
    }

    @Override
    public int initVariableId() {
        return BR.orderViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.orderTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setShowRightImg(true);
        binding.orderTitle.titleRightImg.setImageDrawable(getResources().getDrawable(R.drawable.svg_filter));
        binding.orderTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.orderTitle.titleRootLeft, binding.orderTitle.titleRootRight);
    }

    @Override
    protected void onTitleRightClick() {
    }
}
