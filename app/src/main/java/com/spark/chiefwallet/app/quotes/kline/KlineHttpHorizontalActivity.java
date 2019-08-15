package com.spark.chiefwallet.app.quotes.kline;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.databinding.ActivityKlineHttpHorizontalBinding;
import com.spark.chiefwallet.ui.popup.KlineMorePopup;
import com.spark.chiefwallet.ui.popup.KlineSettingPopup;
import com.spark.chiefwallet.ui.popup.animator.PopupAlphaAnimator;
import com.spark.chiefwallet.ui.popup.impl.OnKlineMoreListener;
import com.spark.chiefwallet.ui.popup.impl.OnKlineSettingListener;
import com.spark.chiefwallet.util.DateUtils;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.klinelib.KLineChartAdapter;
import com.spark.klinelib.KLineEntity;
import com.spark.klinelib.draw.Status;
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
@Route(path = ARouterPath.ACTIVITY_QUOTES_KLINE_HTTP_HORIZONTAL)
public class KlineHttpHorizontalActivity extends BaseActivity<ActivityKlineHttpHorizontalBinding, KlineHttpHorizontalViewModel> {
    @Autowired(name = "quotesThumbClick")
    AllThumbResult.DataBean allThumbResult;

    @Autowired(name = "lastResolution")
    String lastResolution;

    private int loadTypePosition = 2;
    private KLineChartAdapter mKLineChartAdapter; // K线的适配器
    private List<KLineEntity> mKLineEntityList = new ArrayList<>();
    private KlineMorePopup mKlineMorePopup;
    private KlineSettingPopup mKlineSettingPopup;
    private int klineSubscribeTimeCompare;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_kline_http_horizontal;
    }

    @Override
    public int initVariableId() {
        return BR.klineHttpHorizontalViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, getResources().getColor(R.color.kline_bg));

        //mCoinPairPushBean注入vm
        viewModel.initDate(allThumbResult,lastResolution);
        //K线图ViewInit
        initKlineRB();
        initKlineView();
        loadKlineDate(loadTypePosition);
        //K线图
    }

    private void initKlineRB() {
        binding.klineTimeTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) return;
                switch (group.getCheckedRadioButtonId()) {
                    //分时
                    case R.id.kline_time_tab_ts:
                        loadTypePosition = 0;
                        break;
                    //1分钟
                    case R.id.kline_time_1min:
                        loadTypePosition = 1;
                        break;
                    //5分钟
                    case R.id.kline_time_5min:
                        loadTypePosition = 2;
                        break;
                    //1小时
                    case R.id.kline_time_1hour:
                        loadTypePosition = 3;
                        break;
                    //1天
                    case R.id.kline_time_1day:
                        loadTypePosition = 4;
                        break;
                }
                binding.klineLoadMoreTv.setSelected(false);
                binding.klineLoadMoreTv.setText(getString(R.string.more));
                loadKlineDate(loadTypePosition);
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
        binding.klineChartView.setGridRows(4);
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
                klineSubscribeTimeCompare = DateUtils.compare(kLineEntity.Date, mKLineEntityList.get(mKLineEntityList.size() - 1).getDate(), loadTypePosition);
                switch (klineSubscribeTimeCompare) {
                    //更新最后一条
                    case 0:
                        if (loadTypePosition == 0 || loadTypePosition == 1 || loadTypePosition == 2) {
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
                        binding.klineChartView.setMainDrawLine(loadTypePosition == 0 ? true : false);
                        mKLineChartAdapter.replaceData(kLineEntities);
//                        binding.klineChartView.startAnimation();
                        binding.klineChartView.refreshEnd();
                    }
                });
            }
        }).start();
    }

    @OnClick({R.id.kline_load_more,
            R.id.kline_load_setting,
            R.id.close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                finish();
                break;
            //更多
            case R.id.kline_load_more:
                if (mKlineMorePopup == null) {
                    mKlineMorePopup = new KlineMorePopup(this, new OnKlineMoreListener() {
                        @Override
                        public void onSelectMore(String more, int position) {
                            mKlineMorePopup.dismiss();
                            //清空RB选中
                            binding.klineTimeTab.clearCheck();
                            binding.klineLoadMoreTv.setSelected(true);
                            binding.klineLoadMoreTv.setText(more);
                            binding.klineLoadMoreTv.setVisibility(View.VISIBLE);
                            loadTypePosition = position;
                            loadKlineDate(loadTypePosition);
                        }
                    });
                }

                new XPopup.Builder(this)
                        .atView(binding.llTabKline)
                        .customAnimator(new PopupAlphaAnimator())
                        .hasShadowBg(false)
                        .asCustom(mKlineMorePopup)
                        .show();
                break;
            //设置
            case R.id.kline_load_setting:
                if (mKlineSettingPopup == null) {
                    mKlineSettingPopup = new KlineSettingPopup(this, new OnKlineSettingListener() {
                        @Override
                        public void onSelectSetting(int type, int position) {
                            mKlineSettingPopup.dismiss();
                            switch (type) {
                                //主图
                                case 0:
                                    switch (position) {
                                        //主图 - MA
                                        case 0:
                                            binding.klineChartView.changeMainDrawType(Status.MA);
                                            break;
                                        //主图 - BOLL
                                        case 1:
                                            binding.klineChartView.changeMainDrawType(Status.BOLL);
                                            break;
                                        //主图 - 隐藏
                                        case 2:
                                            binding.klineChartView.changeMainDrawType(Status.NONE);
                                            break;
                                    }
                                    break;
                                //幅图
                                case 1:
                                    switch (position) {
                                        //副图 - MACD
                                        case 0:
                                            binding.klineChartView.setChildDraw(0);
                                            break;
                                        //副图 - KDJ
                                        case 1:
                                            binding.klineChartView.setChildDraw(1);
                                            break;
                                        //副图 - RSI
                                        case 2:
                                            binding.klineChartView.setChildDraw(2);
                                            break;
                                        //副图 - 隐藏
                                        case 3:
                                            binding.klineChartView.hideChildDraw();
                                            break;
                                    }
                                    break;

                            }
                        }
                    });
                }
                new XPopup.Builder(this)
                        .atView(binding.llTabKline)
                        .customAnimator(new PopupAlphaAnimator())
                        .hasShadowBg(false)
                        .asCustom(mKlineSettingPopup)
                        .show();
                break;
        }
    }
}
