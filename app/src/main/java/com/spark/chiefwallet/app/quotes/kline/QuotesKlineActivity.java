package com.spark.chiefwallet.app.quotes.kline;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.quotes.kline.adapter.KlineDepthBuyAdapter;
import com.spark.chiefwallet.app.quotes.kline.adapter.KlineDepthSellAdapter;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.DepthDataBean;
import com.spark.chiefwallet.bean.TabEntity;
import com.spark.chiefwallet.databinding.ActivityQuotesKlineBinding;
import com.spark.chiefwallet.ui.popup.B2BDrawerPopup;
import com.spark.chiefwallet.ui.tablayout.listener.CustomTabEntity;
import com.spark.chiefwallet.ui.tablayout.listener.OnTabSelectListener;
import com.spark.chiefwallet.util.DateUtils;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.klinelib.KLineChartAdapter;
import com.spark.klinelib.KLineEntity;
import com.spark.klinelib.formatter.DateFormatter;
import com.spark.modulespot.pojo.AllThumbResult;
import com.spark.modulespot.pojo.MarketSymbolResult;

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
@Route(path = ARouterPath.ACTIVITY_QUOTES_KLINE_CHIEF)
public class QuotesKlineActivity extends BaseActivity<ActivityQuotesKlineBinding, QuotesKlineViewModel> {
    @Autowired(name = "quotesThumbClick")
    AllThumbResult.DataBean allThumbResult;

    private B2BDrawerPopup mB2BDrawerPopup;                     //侧拉栏
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
    private final int defaultSize = 20;
    private KlineDepthSellAdapter mDepthSellAdapter;
    private KlineDepthBuyAdapter mDepthBuyAdapter;
    private List<MarketSymbolResult.DataBean.AskBean> mDepthSellList = new ArrayList<>();
    private List<MarketSymbolResult.DataBean.BidBean> mDepthBuyList = new ArrayList<>();
    private List<DepthDataBean> mDepthBuyMapList = new ArrayList<>();
    private List<DepthDataBean> mDepthSellMapList = new ArrayList<>();

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

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        binding.klineTab.setTabData(mTabEntities);


        //K线图ViewInit
        initKlineRB();
        initKlineView();
        switchSymbol();
    }

    private void switchSymbol(){
        viewModel.initDate(allThumbResult);
        loadKlineDate(loadTypePosition);

        //默认配置20个数据
        for (int i = 0; i < defaultSize; i++) {
            MarketSymbolResult.DataBean.AskBean askBean = new MarketSymbolResult.DataBean.AskBean();
            askBean.setIndex(String.valueOf(i + 1));
            mDepthSellList.add(askBean);
            MarketSymbolResult.DataBean.BidBean bidBean = new MarketSymbolResult.DataBean.BidBean();
            bidBean.setIndex(String.valueOf(i + 1));
            mDepthBuyList.add(bidBean);
        }

        mDepthSellAdapter = new KlineDepthSellAdapter(mDepthSellList);
        binding.sellRv.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.sellRv.setAdapter(mDepthSellAdapter);

        mDepthBuyAdapter = new KlineDepthBuyAdapter(mDepthBuyList);
        binding.buyRv.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.buyRv.setAdapter(mDepthBuyAdapter);
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
        viewModel.uc.mMarketSymbolResultSingleLiveEvent.observe(this, new Observer<MarketSymbolResult>() {
            @Override
            public void onChanged(@Nullable MarketSymbolResult marketSymbolResult) {
                int askSize = marketSymbolResult.getData().getAsk().size();
                for (int i = 0; i < defaultSize; i++) {
                    if (i < askSize) {
                        marketSymbolResult.getData().getAsk().get(i).setIndex(mDepthSellList.get(i).getIndex());
                        mDepthSellList.set(i, marketSymbolResult.getData().getAsk().get(i));
                    } else {
                        MarketSymbolResult.DataBean.AskBean askBean = new MarketSymbolResult.DataBean.AskBean();
                        askBean.setIndex(String.valueOf(i + 1));
                        mDepthSellList.set(i, askBean);
                    }
                }

                int bidSize = marketSymbolResult.getData().getBid().size();
                for (int i = 0; i < defaultSize; i++) {
                    if (i < bidSize) {
                        marketSymbolResult.getData().getBid().get(i).setIndex(mDepthBuyList.get(i).getIndex());
                        mDepthBuyList.set(i, marketSymbolResult.getData().getBid().get(i));
                    } else {
                        MarketSymbolResult.DataBean.BidBean bidBean = new MarketSymbolResult.DataBean.BidBean();
                        bidBean.setIndex(String.valueOf(i + 1));
                        mDepthBuyList.set(i, bidBean);
                    }
                }

                mDepthSellAdapter.notifyDataSetChanged();
                mDepthBuyAdapter.notifyDataSetChanged();

                initDepthMapView(mDepthBuyList, mDepthSellList);
            }
        });

        viewModel.uc.mDrawerBean.observe(this, new Observer<AllThumbResult.DataBean>() {
            @Override
            public void onChanged(@Nullable AllThumbResult.DataBean dataBean) {
                if (mB2BDrawerPopup != null && mB2BDrawerPopup.isShow()) {
                    mB2BDrawerPopup.dismiss();
                }
                if (!TextUtils.isEmpty(viewModel.lastResolution)) {
                    viewModel.unSubscribeKline();
                }
                allThumbResult = dataBean;
                switchSymbol();
            }
        });
    }

    private void initDepthMapView(List<MarketSymbolResult.DataBean.BidBean> depthBuyList, List<MarketSymbolResult.DataBean.AskBean> depthSellList) {
        mDepthBuyMapList.clear();
        mDepthSellMapList.clear();
        for (MarketSymbolResult.DataBean.BidBean bidBean : depthBuyList) {
            DepthDataBean depthDataBean = new DepthDataBean();
            depthDataBean.setPrice((float) bidBean.getPrice());
            depthDataBean.setVolume((float) bidBean.getAmount());
            mDepthBuyMapList.add(depthDataBean);
        }
        for (MarketSymbolResult.DataBean.AskBean askBean : depthSellList) {
            DepthDataBean depthDataBean = new DepthDataBean();
            depthDataBean.setPrice((float) askBean.getPrice());
            depthDataBean.setVolume((float) askBean.getAmount());
            mDepthSellMapList.add(depthDataBean);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.depthMapView.setData(mDepthBuyMapList, mDepthSellMapList);
                    }
                });
            }
        }).start();
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

    @OnClick({R.id.title_left,
            R.id.kline_load_full_screen,
            R.id.kline_menu})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left:
                finish();
                break;
            case R.id.kline_load_full_screen:
                //取消上一次Tab的订阅，订阅当前选中Tab
                if (!TextUtils.isEmpty(viewModel.lastResolution)) {
                    viewModel.unSubscribeKline();
                }
                ARouter.getInstance().build(ARouterPath.ACTIVITY_QUOTES_KLINE_CHIEF_HORIZONTAL)
                        .withParcelable("quotesThumbClick", allThumbResult)
                        .withString("lastResolution", viewModel.lastResolution)
                        .navigation();
                break;
            case R.id.kline_menu:
                if (mB2BDrawerPopup == null)
                    //getParentFragment().getFragmentManager() 获取父容器的FragmentManager()
                    mB2BDrawerPopup = new B2BDrawerPopup(this, getSupportFragmentManager());
                new XPopup.Builder(this)
                        .asCustom(mB2BDrawerPopup)
                        .show();
                break;
        }
    }

}
