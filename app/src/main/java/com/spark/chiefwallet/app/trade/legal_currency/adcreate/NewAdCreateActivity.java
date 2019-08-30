package com.spark.chiefwallet.app.trade.legal_currency.adcreate;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.sevenheaven.iosswitch.ShSwitchView;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.safe.business.surrender.SurrenderActivity;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityNewAdCreateBinding;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerAdapter;
import com.spark.chiefwallet.ui.popup.B2BDrawerPopup;
import com.spark.chiefwallet.ui.popup.ChoosePaymentMethodPopup;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.PayControlClient;
import com.spark.otcclient.pojo.AdSelfDownFindResult;
import com.spark.otcclient.pojo.PayListBean;

import java.util.ArrayList;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.StringUtils;

@Route(path = ARouterPath.ACTIVITY_TRADE_AD_NEWCREATE)
public class NewAdCreateActivity extends BaseActivity<ActivityNewAdCreateBinding, NewAdCreateViewModel> {
    private TitleBean mTitleModel;
    @Autowired(name = "ads")
    public AdSelfDownFindResult.DataBean.RecordsBean ads;

    public int AD_TYPE = 0;//广告类型 0 - 发布购买  1- 发布出售

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_new_ad_create;
    }

    @Override
    public int initVariableId() {
        return BR.newAdCreateViewModel;
    }

    @Override
    public void initData() {
        super.initData();
        if (ads != null) {
            mTitleModel.setTitleName(App.getInstance().getString(R.string.str_ad_update));
        }

    }

    @Override
    public void initView() {
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.adCreateTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleName(getString(R.string.str_push_ad));
        binding.adCreateTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.adCreateTitle.titleRootLeft);

//        ads = getArguments().getParcelable("ads");

        viewModel.initContext(this, AD_TYPE, ads);
        viewModel.getTradeCoinList();

        //是否为固定价格
        binding.isFixedPriceSwitch.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                viewModel.isFixedPrice.set(isOn);
            }
        });
        //是否自动回复
        binding.isAutoResponseSwitch.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean isOn) {
                viewModel.isAutoResponse.set(isOn);
            }
        });
        binding.adFixPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.tradePrice.set(s.toString());
            }
        });
        binding.adPremiumPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ads == null || ads.getPriceType() == 1) {
                    if (StringUtils.isEmpty(s.toString())) {
                        viewModel.tradePrice.set("");
                    } else {
                        viewModel.tradePrice.set(DfUtils.numberFormat(viewModel.ratePrice * (Double.valueOf(s.toString()) / 100), 2));
                    }
                }
            }
        });
        binding.adTradeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.countChange();
            }
        });

        binding.tvSurplus.setText(new SpanUtils().append(getString(R.string.str_appeal_input_1)).append(" 300 ").setForegroundColor(ContextCompat.getColor(NewAdCreateActivity.this, R.color.base)).append(getString(R.string.str_appeal_input_2)).create());
        binding.tvRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvSurplus.setText(new SpanUtils().append(getString(R.string.str_appeal_input_1)).append(" " + (300 - editable.length()) + " ").setForegroundColor(ContextCompat.getColor(NewAdCreateActivity.this, R.color.base)).append(getString(R.string.str_appeal_input_2)).create());
            }

        });

        if (ads != null) {
            binding.isFixedPriceSwitch.setOn(ads.getPriceType() == 0);
            viewModel.isAutoResponse.set(ads.getAutoReply() == 1 ? true : false);
            viewModel.fixedPrice.set(ads.getPriceType() == 0 ? ads.getPrice() + "" : 0 + "");
            //viewModel.isFixedPrice.set(ads.getPriceType() == 0 ? true : false);
        }
    }

    @Override
    public void loadLazyData() {
        viewModel.getTradeCoinList();
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.isRefresh.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    loadLazyData();
                }
            }
        });
        viewModel.uc.adType.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                AD_TYPE = integer;
                viewModel.initContext(NewAdCreateActivity.this, AD_TYPE, ads);
                viewModel.getTradeCoinList();
            }
        });
    }

    @OnClick({R.id.ad_pay_mode})
    public void OnClick(final View view) {
        switch (view.getId()) {
            case R.id.ad_pay_mode:
                /*new XPopup.Builder(getContext())
                        .atView(view)
                        .asCustom(new AdPayModePopup(getContext(), new OnTypeChooseListener() {
                            @Override
                            public void onChooseType(int type, String content) {
                                viewModel.updatePayType(type, content);
                            }
                        }))
                        .show();*/
                //选择收款方式跳转界面
//                ARouter.getInstance().build(ARouterPath.ACTIVITY_TRADE_AD_SELECT_PAYWAY)
//                        .withInt("AD_TYPE", AD_TYPE)
//                        .withParcelableArrayList("payWaySettingsSelected", viewModel.mSelectList)
//                        .navigation(this, 1);
                PayControlClient.getInstance().queryList();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                int AD_TYPE = bundle.getInt("AD_TYPE");
                ArrayList<PayListBean.DataBean> mSelectList = bundle.getParcelableArrayList("payWaySettingsSelected");
                EventBusUtils.postSuccessEvent(EvKey.payWaySettingsSelected, 200, "", AD_TYPE, mSelectList);
            }
        }
    }
}
