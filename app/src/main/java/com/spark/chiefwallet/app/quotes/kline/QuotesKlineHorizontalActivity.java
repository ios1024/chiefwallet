package com.spark.chiefwallet.app.quotes.kline;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TabEntity;
import com.spark.chiefwallet.databinding.ActivityQuotesKlineHorizontalBinding;
import com.spark.chiefwallet.ui.tablayout.listener.CustomTabEntity;
import com.spark.chiefwallet.ui.tablayout.listener.OnTabSelectListener;
import com.spark.chiefwallet.util.DateUtils;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.klinelib.KLineChartAdapter;
import com.spark.klinelib.KLineEntity;
import com.spark.klinelib.formatter.DateFormatter;
import com.spark.modulespot.pojo.AllThumbResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
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
@Route(path = ARouterPath.ACTIVITY_QUOTES_KLINE_CHIEF_HORIZONTAL)
public class QuotesKlineHorizontalActivity extends BaseActivity<ActivityQuotesKlineHorizontalBinding, QuotesKlineHorizontalViewModel> {
    @Autowired(name = "quotesThumbClick")
    AllThumbResult.DataBean allThumbResult;

    @Autowired(name = "lastResolution")
    String lastResolution;

    private String[] mTitles = {"1分", "5分", "15分", "30分", "1小时", "1天", "1周"};
    private int[] mIconUnselectIds = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher};
    private int[] mIconSelectIds = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private int loadTypePosition = 0;
    private KLineChartAdapter mKLineChartAdapter; // K线的适配器
    private List<KLineEntity> mKLineEntityList = new ArrayList<>();

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_quotes_kline_horizontal;
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

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        binding.klineTab.setTabData(mTabEntities);

        viewModel.initDate(allThumbResult,lastResolution);
        //K线图ViewInit
        initKlineRB();
        initKlineView();
        loadKlineDate(loadTypePosition);
    }

    private void initKlineRB() {
        binding.klineTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                loadTypePosition = position;
                loadKlineDate(loadTypePosition);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    /**
     * 初始化Kline
     */
    private void initKlineView() {
        mKLineChartAdapter = new KLineChartAdapter();
        binding.klineChartView.setAdapter(mKLineChartAdapter);
        binding.klineChartView.setDateTimeFormatter(new DateFormatter());
        binding.klineChartView.setGridRows(3);
        binding.klineChartView.setGridColumns(4);
        binding.klineChartView.setBaseCoinScale(allThumbResult.getBaseCoinScreenScale());
    }

    private void loadKlineDate(int loadTypePosition) {
        binding.klineChartView.justShowLoading();
        viewModel.loadKlineHistory(loadTypePosition);
    }

    @Override
    public void initViewObservable() {
        //K线历史数据
        viewModel.uc.klineHistoryList.observe(this, new Observer<List<KLineEntity>>() {
            @Override
            public void onChanged(@Nullable List<KLineEntity> kLineEntities) {
                mKLineEntityList.clear();
                mKLineEntityList.addAll(kLineEntities);
                updateKlineView(kLineEntities);
            }
        });
        //K线订阅数据
        viewModel.uc.klineSubscribe.observe(this, new Observer<KLineEntity>() {
            @Override
            public void onChanged(@Nullable KLineEntity kLineEntity) {
                int klineSubscribeTimeCompare = DateUtils.compare(kLineEntity.Date, mKLineEntityList.get(mKLineEntityList.size() - 1).getDate(), loadTypePosition);
                switch (klineSubscribeTimeCompare) {
                    //更新最后一条
                    case 0:
                        if (loadTypePosition == 0 || loadTypePosition == 1) {
                            mKLineEntityList.set(mKLineEntityList.size() - 1, kLineEntity);
                            mKLineChartAdapter.replaceData(mKLineEntityList);
                        }
                        break;
                    //添加
                    case 1:
                        mKLineEntityList.add(kLineEntity);
                        mKLineChartAdapter.replaceData(mKLineEntityList);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * K线历史数据更新
     *
     * @param kLineEntities
     */
    private void updateKlineView(final List<KLineEntity> kLineEntities) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mKLineChartAdapter.replaceData(kLineEntities);
                        binding.klineChartView.refreshEnd();
                    }
                });
            }
        }).start();
    }

    @OnClick({R.id.title_left})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left:
                finish();
                break;
        }
    }

}
