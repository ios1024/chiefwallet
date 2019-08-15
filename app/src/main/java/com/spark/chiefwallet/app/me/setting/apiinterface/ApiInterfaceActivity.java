package com.spark.chiefwallet.app.me.setting.apiinterface;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.setting.apiinterface.apicreate.ApiCreateFragment;
import com.spark.chiefwallet.app.me.setting.apiinterface.apirecord.ApiRecodeFragment;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityApiInterfaceBinding;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerAdapter;
import com.spark.chiefwallet.util.StatueBarUtils;

import java.util.ArrayList;

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
@Route(path = ARouterPath.ACTIVITY_ME_SAFECENTRE_APIINTERFACE)
public class ApiInterfaceActivity extends BaseActivity<ActivityApiInterfaceBinding, ApiInterfaceVeiwModel> {
    private TitleBean mTitleModel;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {App.getInstance().getString(R.string.str_create_api), App.getInstance().getString(R.string.str_create_api_record)};
    private SlideTabPagerAdapter mAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_api_interface;
    }

    @Override
    public int initVariableId() {
        return BR.apiInterfaceVeiwModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.apiInterfaceTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        binding.apiInterfaceTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.apiInterfaceTitle.titleRootLeft);
        mFragments.add(new ApiCreateFragment());
        mFragments.add(new ApiRecodeFragment());

        mAdapter = new SlideTabPagerAdapter(getSupportFragmentManager(),
                mFragments, mTitles);
        binding.apiInterfaceVp.setAdapter(mAdapter);
        binding.apiInterfaceTab.setViewPager(binding.apiInterfaceVp);

    }
}
