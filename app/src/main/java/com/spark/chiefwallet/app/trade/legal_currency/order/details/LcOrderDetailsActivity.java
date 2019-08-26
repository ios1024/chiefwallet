package com.spark.chiefwallet.app.trade.legal_currency.order.details;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.databinding.ActivityLcOrderDetailsBinding;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.pojo.LcOrderResult;
import com.spark.otcclient.pojo.OrderDetailsResult;

import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.http.impl.OnRequestListener;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/12
 * 描    述：
 * 修订历史：
 * ================================================
 */

@Route(path = ARouterPath.ACTIVITY_TRADE_LC_DETAILS_ORDER)
public class LcOrderDetailsActivity extends BaseActivity<ActivityLcOrderDetailsBinding, LcOrderDetailsViewModel> {
    @Autowired(name = "orderDetails")
    LcOrderResult.DataBean.RecordsBean orderDetailsBean;
    private String titleText;
    private Drawable statusDrawable;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lc_order_details;
    }

    @Override
    public int initVariableId() {
        return BR.lcOrderDetailsViewModel;
    }

    @Override
    public void initView() {
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);
        viewModel.initContext(this);
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.isRefresh.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean){
                    initData();
                }
            }
        });
    }

    @Override
    public void initData() {
        if (!binding.root.isLoadingCurrentState()) binding.root.showLoading();
        viewModel.initViewDate(orderDetailsBean, new OnRequestListener<OrderDetailsResult>() {
            @Override
            public void onSuccess(OrderDetailsResult orderDetailsResult) {
                if (binding.root.isLoadingCurrentState()) binding.root.showContent();
            }

            @Override
            public void onFail(String message) {
                Toasty.showError(message);
                binding.root.showError(R.drawable.svg_no_data, App.getInstance().getApplicationContext().getString(R.string.str_http_error), message, App.getInstance().getApplicationContext().getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
            }
        });
    }
}
