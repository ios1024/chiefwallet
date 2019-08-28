package com.spark.chiefwallet.app.trade.currency;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.XPopup;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.trade.currency.adapter.CurrentCommissionAdapter;
import com.spark.chiefwallet.app.trade.currency.adapter.MarketBuyAdapter;
import com.spark.chiefwallet.app.trade.currency.adapter.MarketSellAdapter;
import com.spark.chiefwallet.databinding.FragmentCurrentBinding;
import com.spark.chiefwallet.ui.PointLengthFilter;
import com.spark.chiefwallet.ui.RvLoadMoreView;
import com.spark.chiefwallet.ui.popup.B2BBuyTypePopup;
import com.spark.chiefwallet.ui.popup.B2BDrawerPopup;
import com.spark.chiefwallet.ui.popup.CancelOrderPopup;
import com.spark.chiefwallet.ui.popup.GearPositionPopup;
import com.spark.chiefwallet.ui.popup.impl.OnTypeChooseListener;
import com.spark.chiefwallet.ui.seekbar.SeekBar;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.modulespot.B2BWsClient;
import com.spark.modulespot.pojo.AllThumbResult;
import com.spark.modulespot.pojo.MarketSymbolResult;
import com.spark.modulespot.pojo.MarketSymbolResult.DataBean.AskBean;
import com.spark.modulespot.pojo.MarketSymbolResult.DataBean.BidBean;
import com.spark.modulespot.pojo.OpenOrdersResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseFragment;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CurrencyFragment extends BaseFragment<FragmentCurrentBinding, CurrencyViewModel> {
    private B2BDrawerPopup mB2BDrawerPopup;                     //侧拉栏
    private MarketSellAdapter mMarketSellAdapter;               //盘口 - 卖
    private MarketBuyAdapter mMarketBuyAdapter;                 //盘口 - 买
    private List<AskBean> mMarketSellList = new ArrayList<>();
    private List<BidBean> mMarketBuyList = new ArrayList<>();
    private List<AskBean> mMarketSellTempList = new ArrayList<>();
    private List<BidBean> mMarketBuyTempList = new ArrayList<>();
    private int defaultSize = 50;
    private int pageIndex = 1;
    private static final int PAGE_SIZE = 50;
    private List<OpenOrdersResult.DataBean.ListBean> openOrdersResultList = new ArrayList<>();
    private CurrentCommissionAdapter mOpenOrdersAdapter;               //当前委托
    private PointLengthFilter mPriceFilter;                     //位数限制
    private PointLengthFilter mNumFilter;
    private boolean isDepthMerge = false;

    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_current;
    }

    @Override
    public int initVariableId() {
        return BR.currentViewModel;
    }

    @Override
    public void initView() {
        binding.currentRoot.showLoading();
        viewModel.initContext(getActivity());
    }

    @Override
    public void loadLazyData() {
        //价格 - 数量  监听
        initTextChangedListener();
        //默认选中 买入
        binding.btnBuy.setSelected(true);
        viewModel.initBuyText();

        mMarketSellAdapter = new MarketSellAdapter(mMarketSellList);
        LinearLayoutManager layoutReverse = new LinearLayoutManager(getActivity());
        layoutReverse.setReverseLayout(true);
        binding.rvSell.setLayoutManager(layoutReverse);
        binding.rvSell.setAdapter(mMarketSellAdapter);
        mMarketSellAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mMarketSellList.get(position).getPrice() != 0) {
                    viewModel.updatePriceEt(MathUtils.getRundNumber(mMarketSellList.get(position).getPrice(), Constant.currencySymbolRate, null));
                }
            }
        });

        mMarketBuyAdapter = new MarketBuyAdapter(mMarketBuyList);
        binding.rvBuy.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvBuy.setAdapter(mMarketBuyAdapter);
        mMarketBuyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mMarketBuyList.get(position).getPrice() != 0) {
                    viewModel.updatePriceEt(MathUtils.getRundNumber(mMarketBuyList.get(position).getPrice(), Constant.currencySymbolRate, null));
                }
            }
        });

        initSeekBarListener();

        mOpenOrdersAdapter = new CurrentCommissionAdapter(openOrdersResultList);
        binding.rvOpenOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvOpenOrders.setAdapter(mOpenOrdersAdapter);
        mOpenOrdersAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                new XPopup.Builder(getActivity())
                        .asCustom(new CancelOrderPopup(getActivity(), openOrdersResultList.get(position)))
                        .show();
            }
        });

        //下拉刷新
        binding.swipeLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.base));
        binding.swipeLayout.setEnabled(App.getInstance().isAppLogin());
        binding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeLayout.setRefreshing(true);
                refresh();
            }
        });
        //上拉加载
        mOpenOrdersAdapter.setLoadMoreView(new RvLoadMoreView());
        mOpenOrdersAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, binding.rvOpenOrders);

        if (Constant.isHttpAndWs) {
            if (!TextUtils.isEmpty(Constant.b2bKlinePushJson)) {
                viewModel.initWsData(App.gson.fromJson(Constant.b2bKlinePushJson, AllThumbResult.class));
            } else {
                B2BWsClient.getInstance().getB2BKlinePush();
            }
        }
    }

    private void initTextChangedListener() {
        //数量监听
        binding.etNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.isEmpty(s.toString())) {
                    viewModel.updateNumberEt(s);
                }
            }
        });

        //价格监听
        binding.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.isEmpty(s.toString())) {
                    viewModel.updatePriceEt(s.toString());
                }
            }
        });
    }

    //seekbar监听
    private void initSeekBarListener() {
        binding.buySeekBar.setOnProgressChangedListener(new SeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(SeekBar bubbleSeekBar, int progress, float progressFloat) {
                viewModel.updateTradeValue(progressFloat);
            }

            @Override
            public void getProgressOnFinally(SeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });
    }

    /**
     * 下拉刷新
     */
    private void refresh() {
        if (mOpenOrdersAdapter == null) {
            binding.swipeLayout.setRefreshing(false);
            return;
        }
        pageIndex = 1;
        mOpenOrdersAdapter.setEnableLoadMore(false);
        viewModel.findOpenOrders(pageIndex, new OnRequestListener<OpenOrdersResult>() {
            @Override
            public void onSuccess(OpenOrdersResult openOrdersResult) {
                openOrdersResultList.clear();
                openOrdersResultList.addAll(openOrdersResult.getData().getList());
                mOpenOrdersAdapter.notifyDataSetChanged();
                setData(true, openOrdersResult.getData().getList());
                mOpenOrdersAdapter.setEnableLoadMore(true);
                binding.swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFail(String message) {
                Toasty.showError(message);
                mOpenOrdersAdapter.setEnableLoadMore(true);
                binding.swipeLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 上拉加载
     */
    private void loadMore() {
        viewModel.findOpenOrders(pageIndex, new OnRequestListener<OpenOrdersResult>() {
            @Override
            public void onSuccess(OpenOrdersResult openOrdersResult) {
                boolean isRefresh = pageIndex == 1;
                setData(isRefresh, openOrdersResult.getData().getList());
            }

            @Override
            public void onFail(String message) {
                mOpenOrdersAdapter.loadMoreFail();
            }
        });
    }

    /**
     * * @param isRefresh
     *
     * @param data
     */
    private void setData(boolean isRefresh, List data) {
        pageIndex++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            mOpenOrdersAdapter.setNewData(data);
            binding.commissionLl.setVisibility(size != 0 ? View.VISIBLE : View.GONE);
            if (size == 0) {
                mOpenOrdersAdapter.setEmptyView(R.layout.view_rv_empty_2, binding.rvOpenOrders);
            }
        } else {
            if (size > 0) {
                mOpenOrdersAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mOpenOrdersAdapter.loadMoreEnd(isRefresh);
        } else {
            mOpenOrdersAdapter.loadMoreComplete();
        }
    }


    @Override
    public void initViewObservable() {
        //切换币种监听
        viewModel.uc.mDataBeanSingleLiveEvent.observe(this, new Observer<AllThumbResult.DataBean>() {
            @Override
            public void onChanged(@Nullable AllThumbResult.DataBean dataBean) {
                if (mMarketSellAdapter == null || mMarketBuyList == null) return;

                mMarketSellList.clear();
                mMarketBuyList.clear();
                //默认配置50个数据
                for (int i = 0; i < defaultSize; i++) {
                    AskBean askBean = new AskBean();
                    askBean.setType(0);
                    mMarketSellList.add(askBean);
                    BidBean bidBean = new BidBean();
                    bidBean.setType(0);
                    mMarketBuyList.add(bidBean);
                }

                mMarketSellAdapter.notifyDataSetChanged();
                mMarketBuyAdapter.notifyDataSetChanged();
                binding.rvSell.scrollToPosition(0);
                binding.rvBuy.scrollToPosition(0);

                if (mB2BDrawerPopup != null && mB2BDrawerPopup.isShow()) {
                    mB2BDrawerPopup.dismiss();
                }
                if (binding.currentRoot.isLoadingCurrentState()) {
                    binding.currentRoot.showContent();

                }
                if (App.getInstance().isAppLogin()) {
                    refresh();
                }
            }
        });

        //盘口数据监听
        viewModel.uc.mMarketSymbolResultSingleLiveEvent.observe(this, new Observer<MarketSymbolResult>() {
            @Override
            public void onChanged(@Nullable MarketSymbolResult marketSymbolResult) {
                mMarketSellList.clear();
                mMarketBuyList.clear();
                int askSize = marketSymbolResult.getData().getAsk().size();
                mMarketSellTempList.clear();
                for (int i = 0; i < defaultSize; i++) {
                    if (i < askSize) {
                        marketSymbolResult.getData().getAsk().get(i).setType(0);
                        mMarketSellTempList.add(marketSymbolResult.getData().getAsk().get(i));
                    } else {
                        AskBean askBean = new AskBean();
                        askBean.setType(0);
                        mMarketSellTempList.add(askBean);
                    }
                }

                int bidSize = marketSymbolResult.getData().getBid().size();
                mMarketBuyTempList.clear();
                for (int i = 0; i < defaultSize; i++) {
                    if (i < bidSize) {
                        marketSymbolResult.getData().getBid().get(i).setType(0);
                        mMarketBuyTempList.add(marketSymbolResult.getData().getBid().get(i));
                    } else {
                        BidBean bidBean = new BidBean();
                        bidBean.setType(0);
                        mMarketBuyTempList.add(bidBean);
                    }
                }

                if (isDepthMerge) {
                    LogUtils.e("depth_merge", "COME");
                    for (int i = 0; i < mMarketSellTempList.size(); i++) {
                        if (i == 0) {
                            mMarketSellList.add(mMarketSellTempList.get(i));
                        } else {
                            if (DfUtils.numberFormatDoubleDown(mMarketSellTempList.get(i).getPrice(), 2)
                                    == DfUtils.numberFormatDoubleDown(mMarketSellList.get(mMarketSellList.size() - 1).getPrice(), 2)) {
                                AskBean askBean = mMarketSellTempList.get(i);
                                askBean.setAmount(askBean.getAmount() + mMarketSellTempList.get(i - 1).getAmount());
                                mMarketSellList.remove(mMarketSellList.size() - 1);
                                mMarketSellList.add(askBean);
                            } else {
                                mMarketSellList.add(mMarketSellTempList.get(i));
                            }
                        }
                    }
                    mMarketSellTempList.clear();
                    mMarketSellTempList.addAll(mMarketSellList);
                    mMarketSellList.clear();
                    for (int i = 0; i < defaultSize; i++) {
                        if (i < mMarketSellTempList.size()) {
                            mMarketSellList.add(mMarketSellTempList.get(i));
                        } else {
                            AskBean askBean = new AskBean();
                            askBean.setType(0);
                            mMarketSellList.add(askBean);
                        }
                    }


                    for (int i = 0; i < mMarketBuyTempList.size(); i++) {
                        if (i == 0) {
                            mMarketBuyList.add(mMarketBuyTempList.get(i));
                        } else {
                            if (DfUtils.numberFormatDoubleDown(mMarketBuyTempList.get(i).getPrice(), 2)
                                    == DfUtils.numberFormatDoubleDown(mMarketBuyList.get(mMarketBuyList.size() - 1).getPrice(), 2)) {
                                BidBean bidBean = mMarketBuyTempList.get(i);
                                bidBean.setAmount(bidBean.getAmount() + mMarketBuyTempList.get(i - 1).getAmount());
                                mMarketBuyList.remove(mMarketBuyList.size() - 1);
                                mMarketBuyList.add(bidBean);
                            } else {
                                mMarketBuyList.add(mMarketBuyTempList.get(i));
                            }
                        }
                    }
                    mMarketBuyTempList.clear();
                    mMarketBuyTempList.addAll(mMarketBuyList);
                    mMarketBuyList.clear();
                    for (int i = 0; i < defaultSize; i++) {
                        if (i < mMarketBuyTempList.size()) {
                            mMarketBuyList.add(mMarketBuyTempList.get(i));
                        } else {
                            BidBean bidBean = new BidBean();
                            bidBean.setType(0);
                            mMarketBuyList.add(bidBean);
                        }
                    }

                } else {
                    mMarketSellList.addAll(mMarketSellTempList);
                    mMarketBuyList.addAll(mMarketBuyTempList);
                }

                mMarketSellAdapter.notifyDataSetChanged();
                mMarketBuyAdapter.notifyDataSetChanged();
            }
        });

        //登录状态更新
        viewModel.uc.loginStatue.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    binding.swipeLayout.setEnabled(true);
                    refresh();
                } else {
                    binding.swipeLayout.setEnabled(false);
                    openOrdersResultList.clear();
                    setData(true, openOrdersResultList);
                }
            }
        });

        //K线页面返回 买入 - 卖出 按钮点击监听
        viewModel.uc.fromKlineStatue.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                initBuyType(integer);
            }
        });

        //当前委托监听
        viewModel.uc.orderStatue.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    refresh();
                }
            }
        });

        viewModel.uc.mFilterSingleLiveEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mPriceFilter = new PointLengthFilter(integer);
                mNumFilter = new PointLengthFilter(integer);
                binding.etPrice.setFilters(new InputFilter[]{mPriceFilter});
                binding.etNum.setFilters(new InputFilter[]{mNumFilter});
            }
        });

        viewModel.uc.mMarketBuySingleLiveEvent.observe(this, new Observer<List<BidBean>>() {
            @Override
            public void onChanged(@Nullable List<BidBean> bidBeans) {
                LogUtils.e("market", "bidBeans");
                int bidSize = bidBeans.size();
                for (int i = 0; i < defaultSize; i++) {
                    if (i < bidSize) {
                        bidBeans.get(i).setType(0);
                        mMarketBuyList.set(i, bidBeans.get(i));
                    } else {
                        BidBean bidBean = new BidBean();
                        bidBean.setType(0);
                        mMarketBuyList.set(i, bidBean);
                    }
                }
                mMarketBuyAdapter.notifyDataSetChanged();
            }
        });

        viewModel.uc.mMarketSellSingleLiveEvent.observe(this, new Observer<List<AskBean>>() {
            @Override
            public void onChanged(@Nullable List<AskBean> askBeans) {
                LogUtils.e("market", "askBeans");
                int bidSize = askBeans.size();
                for (int i = 0; i < defaultSize; i++) {
                    if (i < bidSize) {
                        askBeans.get(i).setType(0);
                        mMarketSellList.set(i, askBeans.get(i));
                    } else {
                        AskBean askBean = new AskBean();
                        askBean.setType(0);
                        mMarketSellList.set(i, askBean);
                    }
                }
                mMarketSellAdapter.notifyDataSetChanged();
            }
        });

    }

    @OnClick({R.id.btn_buy,
            R.id.btn_sell,
            R.id.current_open_drawer,
            R.id.current_buy_type,
            R.id.tv_market_price,
            R.id.gear_position,
            R.id.depth_merge})
    public void onClick(View v) {
        switch (v.getId()) {
            //买入
            case R.id.btn_buy:
                initBuyType(0);
                break;
            //卖出
            case R.id.btn_sell:
                initBuyType(1);
                break;
            //币币交易左侧drawer
            case R.id.current_open_drawer:
                if (mB2BDrawerPopup == null)
                    //getParentFragment().getFragmentManager() 获取父容器的FragmentManager()
                    mB2BDrawerPopup = new B2BDrawerPopup(getContext(), getParentFragment().getFragmentManager());
                new XPopup.Builder(getContext())
                        .asCustom(mB2BDrawerPopup)
                        .show();
                break;
            //购买类型
            case R.id.current_buy_type:
                new XPopup.Builder(getContext())
                        .atView(v)
                        .asCustom(new B2BBuyTypePopup(getContext(), viewModel.buyType.get(), new OnTypeChooseListener() {
                            @Override
                            public void onChooseType(int type, String content) {
                                viewModel.priceType.set(type);
                                viewModel.currentBuyType.set(content);
                                viewModel.typeVisiable.set(type == 0);
                                setSeekBar();
                                viewModel.updateNumberAvailable();
                            }
                        }))
                        .show();
                break;
            case R.id.tv_market_price:
                break;
            case R.id.gear_position:
                new XPopup.Builder(getContext())
                        .atView(v)
                        .asCustom(new GearPositionPopup(getContext(), new OnTypeChooseListener() {
                            @Override
                            public void onChooseType(int type, String content) {
                                defaultSize = type;
                                binding.gearTv.setText(String.valueOf(type));
                            }
                        }))
                        .show();
                break;
            case R.id.depth_merge:
                isDepthMerge = !isDepthMerge;
                binding.depthMerge.setBackground(getResources().getDrawable(isDepthMerge ? R.drawable.depth_merge_select_2dp_bg : R.drawable.depth_merge_2dp_bg));
                break;
        }
    }

    /**
     * 买入 卖出 按钮点击 UI刷新
     *
     * @param type 0 - 买入  1 - 卖出
     */
    private void initBuyType(int type) {
        binding.btnBuy.setSelected(type == 0 ? true : false);
        binding.btnSell.setSelected(type == 1 ? true : false);
        viewModel.buyType.set(type);
        setSeekBar();
        viewModel.initBuyText();
        if (type == 0) {
            binding.currentBuy.setBackground(getResources().getDrawable(R.drawable.rise_2dp_bg));
            if (viewModel.currentBuyType.get().contains(getString(R.string.sell))) {
                viewModel.currentBuyType.set(viewModel.currentBuyType.get().replace(getString(R.string.sell), getString(R.string.buy)));
            }
        } else {
            binding.currentBuy.setBackground(getResources().getDrawable(R.drawable.fall_2dp_bg));
            if (viewModel.currentBuyType.get().contains(getString(R.string.buy))) {
                viewModel.currentBuyType.set(viewModel.currentBuyType.get().replace(getString(R.string.buy), getString(R.string.sell)));
            }
        }
        viewModel.updateNumberAvailable();
    }

    private void setSeekBar() {
        binding.buySeekBar.setProgress(0);
        binding.buySeekBar.setThumbColor(ContextCompat.getColor(getActivity(), viewModel.buyType.get() == 0 ? R.color.rise : R.color.fall));
        binding.buySeekBar.setBubbleColor(ContextCompat.getColor(getActivity(), viewModel.buyType.get() == 0 ? R.color.rise : R.color.fall));
        binding.buySeekBar.setSecondTrackColor(ContextCompat.getColor(getActivity(), viewModel.buyType.get() == 0 ? R.color.rise : R.color.fall));
    }
}
