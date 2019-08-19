package com.spark.chiefwallet.app.quotes.kline;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.databinding.ActivityQuotesKlineBinding;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerAdapter;
import com.spark.chiefwallet.ui.popup.KlineMorePopup;
import com.spark.chiefwallet.ui.popup.KlineSettingPopup;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.klinelib.KLineChartAdapter;
import com.spark.klinelib.KLineEntity;
import com.spark.modulespot.pojo.AllThumbResult;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseActivity;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/30
 * 描    述：
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_QUOTES_KLINE_CHIEF)
public class QuotesKlineActivity extends BaseActivity<ActivityQuotesKlineBinding, QuotesKlineViewModel> {
    @Autowired(name = "quotesThumbClick")
    AllThumbResult.DataBean allThumbResult;

    private String[] mTitles;
    private int loadTypePosition = 2;
    private KLineChartAdapter mKLineChartAdapter; // K线的适配器
    private List<KLineEntity> mKLineEntityList = new ArrayList<>();
    private KlineMorePopup mKlineMorePopup;
    private KlineSettingPopup mKlineSettingPopup;
    private int klineSubscribeTimeCompare;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private SlideTabPagerAdapter mAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_quotes_kline;
    }

    @Override
    public int initVariableId() {
        return BR.klineHttpViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, getResources().getColor(R.color.white));
    }
}
