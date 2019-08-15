package com.spark.chiefwallet.app.trade.legal_currency.adcreate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.trade.legal_currency.adcreate.upload.AdUploadFragment;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityAdCreateBinding;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerAdapter;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.pojo.AdSelfDownFindResult;
import com.spark.otcclient.pojo.PayListBean;

import java.util.ArrayList;

import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.utils.EventBusUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/27
 * 描    述：
 * 修订历史：
 * ================================================
 */

@Route(path = ARouterPath.ACTIVITY_TRADE_AD_CREATE)
public class AdCreateActivity extends BaseActivity<ActivityAdCreateBinding, AdCreateViewModel> {
    private TitleBean mTitleModel;
    private String[] mTitles = new String[]{"发布购买", "发布出售"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private SlideTabPagerAdapter mAdapter;
    @Autowired(name = "ads")
    public AdSelfDownFindResult.DataBean.RecordsBean ads;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_ad_create;
    }

    @Override
    public int initVariableId() {
        return BR.adCreateViewModel;
    }

    @Override
    public void initView() {
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.adCreateTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        binding.adCreateTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.adCreateTitle.titleRootLeft);

        mFragments.add(AdUploadFragment.newInstance(0, ads));
        mFragments.add(AdUploadFragment.newInstance(1, ads));

        mAdapter = new SlideTabPagerAdapter(getSupportFragmentManager(),
                mFragments, mTitles);
        binding.adCreateVp.setAdapter(mAdapter);
        binding.adCreateTab.setViewPager(binding.adCreateVp);
    }

    @Override
    public void initData() {
        super.initData();
        if (ads != null) {
            mTitleModel.setTitleName("修改广告");
            binding.adCreateTab.setVisibility(View.GONE);
            if (ads.getAdvertiseType() == 0) binding.adCreateVp.setCurrentItem(0);
            else binding.adCreateVp.setCurrentItem(1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
