package com.spark.chiefwallet.app.me.finance.ad;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityAdBinding;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerAdapter;
import com.spark.chiefwallet.ui.popup.BusinessManagementPopup;
import com.spark.chiefwallet.ui.popup.impl.OnTypeChooseListener;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.LcTradeClient;

import java.util.ArrayList;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.utils.EventBusUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_ME_AD)
public class AdActivity extends BaseActivity<ActivityAdBinding, AdViewModel> {
    @Autowired(name = "adUp")
    public boolean adUp;
    private TitleBean mTitleModel;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private SlideTabPagerAdapter mAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_ad;
    }

    @Override
    public int initVariableId() {
        return BR.adViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);
        mTitles = getResources().getStringArray(R.array.ad_tab);

        //TitleSet
//        mTitleModel = new TitleBean();
//        mTitleModel.setShowRightImg(true);
//        mTitleModel.setShowRightTV(true);
//        binding.adTitle.titleRightImg.setImageDrawable(getResources().getDrawable(R.drawable.svg_business_management));
//        binding.adTitle.titleRightTv.setText(getString(R.string.business_management));
//        binding.adTitle.titleRightTv.setTextColor(ContextCompat.getColor(AdActivity.this, R.color.base));
//        binding.adTitle.setViewTitle(mTitleModel);
//        setTitleListener(binding.adTitle.titleRootLeft, binding.adTitle.titleRootRight);

        mFragments.add(AdFragment.newInstance(0));
        mFragments.add(AdFragment.newInstance(1));
        mAdapter = new SlideTabPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);

        binding.adVp.setAdapter(mAdapter);

        binding.adTab.setViewPager(binding.adVp);
        if (adUp) {
            binding.adVp.setCurrentItem(1);
        }
    }

    @OnClick({R.id.title_left, R.id.ll_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left:
                //关闭
                finish();
                break;
            case R.id.ll_right:
                //展开菜单
                new XPopup.Builder(this)
                        .atView(binding.llRight)
                        .asCustom(new BusinessManagementPopup(this)).show();
                break;
            default:
                break;
        }
    }

//
//    @Override
//    protected void onTitleRightClick() {
//        //查询自己认证商家状态
////        showDialog("请求中");
////        LcTradeClient.getInstance().getBusinessFind();
//
//
//
//    }
}
