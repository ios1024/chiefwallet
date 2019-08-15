package com.spark.chiefwallet.app.me.finance.record;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.finance.record.invitefriends.InviteFriendFragment;
import com.spark.chiefwallet.app.me.finance.record.rebaterecord.RebateRecordFragment;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityRecordBinding;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerAdapter;
import com.spark.chiefwallet.util.StatueBarUtils;

import java.util.ArrayList;

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

@Route(path = ARouterPath.ACTIVITY_ME_RECORD)
public class RecordActivity extends BaseActivity<ActivityRecordBinding, RecordViewModel> {
    private TitleBean mTitleModel;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private SlideTabPagerAdapter mAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_record;
    }

    @Override
    public int initVariableId() {
        return BR.recordViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.recordTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        mTitles = getResources().getStringArray(R.array.my_order_tab);
        //TitleSet
        mTitleModel = new TitleBean();
        binding.recordTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.recordTitle.titleRootLeft);
        mFragments.add(new InviteFriendFragment());
        mFragments.add(new RebateRecordFragment());

        mAdapter = new SlideTabPagerAdapter(getSupportFragmentManager(),
                mFragments, mTitles);
        binding.recordVp.setAdapter(mAdapter);
        binding.recordTab.setViewPager(binding.recordVp);
    }
}
