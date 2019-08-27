package com.spark.chiefwallet.app.me.setting.coinaddress.add;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.spark.acclient.pojo.CoinSupportBean;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityCoinAddressAddBinding;
import com.spark.chiefwallet.ui.popup.CoinChoosePopup;
import com.spark.chiefwallet.ui.popup.impl.OnCoinChooseListener;
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
@Route(path = ARouterPath.ACTIVITY_ME_COINADDRESS_COINADDRESS_ADD)
public class CoinAddressAddActivity extends BaseActivity<ActivityCoinAddressAddBinding, CoinAddressAddViewModel> {
    @Autowired(name = "Coin")
    String Coin;

    private TitleBean mTitleModel;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_coin_address_add;
    }

    @Override
    public int initVariableId() {
        return BR.coinAddressAddViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.coinAddressAddTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //TitleSet
        mTitleModel = new TitleBean();
        binding.coinAddressAddTitle.setViewTitle(mTitleModel);
        mTitleModel.setTitleName("新增地址");
        setTitleListener(binding.coinAddressAddTitle.titleRootLeft);

        viewModel.setSelectCoinAddress(Coin);
        viewModel.initContext(this);
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.mCoinSupportBeanSingleLiveEvent.observe(this, new Observer<CoinSupportBean>() {
            @Override
            public void onChanged(@Nullable final CoinSupportBean coinSupportBean) {
                new XPopup.Builder(CoinAddressAddActivity.this)
                        .asCustom(new CoinChoosePopup(CoinAddressAddActivity.this, coinSupportBean.getData(), false, new OnCoinChooseListener() {
                            @Override
                            public void onClickOrder() {
                                //记录
                            }

                            @Override
                            public void onClickItem(int position) {
                                //item点击
                                viewModel.setSelectCoinAddress(coinSupportBean.getData().get(position).getCoinName());
                            }
                        }))
                        .show();
            }
        });
    }
}
