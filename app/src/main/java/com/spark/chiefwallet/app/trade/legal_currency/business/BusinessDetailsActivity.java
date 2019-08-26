package com.spark.chiefwallet.app.trade.legal_currency.business;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.XPopup;
import com.spark.acclient.pojo.SpotWalletResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.trade.legal_currency.adapter.LcBusinessAdAdapter;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityLcBusinessDetailsBinding;
import com.spark.chiefwallet.ui.popup.LcTradePopup;
import com.spark.chiefwallet.ui.popup.TradePwdPopup;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.popup.impl.OnOrderCreateListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.LcTradeClient;
import com.spark.otcclient.pojo.FindAdvertiseResult;
import com.spark.otcclient.pojo.FindPageResult;
import com.spark.otcclient.pojo.OrderCreateBean;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.http.impl.OnRequestListener;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/8/23
 * 描    述：
 * 修订历史：商家展示页
 * ================================================
 */

@Route(path = ARouterPath.ACTIVITY_TRADE_BUSINESS_DETAILS)
public class BusinessDetailsActivity extends BaseActivity<ActivityLcBusinessDetailsBinding, BusinessDetailsViewModel> {
    @Autowired(name = "memberId")
    int memberId;

    private TitleBean mTitleModel;
    private LcBusinessAdAdapter mLcVPAdapterSell;
    private LcBusinessAdAdapter mLcVPAdapterBuy;
    private List<FindAdvertiseResult.DataBean> mBeanListSell = new ArrayList<>();
    private List<FindAdvertiseResult.DataBean> mBeanListBuy = new ArrayList<>();

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lc_business_details;
    }

    @Override
    public int initVariableId() {
        return BR.businessDetailsViewModel;
    }

    @Override
    public void initView() {
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.propertyDetailsTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        binding.propertyDetailsTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.propertyDetailsTitle.titleRootLeft);

        mLcVPAdapterSell = new LcBusinessAdAdapter(mBeanListSell);
        binding.rvSellList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSellList.setAdapter(mLcVPAdapterSell);
        mLcVPAdapterSell.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Constant.lcBuyOrSell = 0;
                FindAdvertiseResult.DataBean dataBean = (FindAdvertiseResult.DataBean) adapter.getItem(position);
                goToCreate(dataBean.getCoinName(), dataBean);
            }
        });

        mLcVPAdapterBuy = new LcBusinessAdAdapter(mBeanListBuy);
        binding.rvBuyList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBuyList.setAdapter(mLcVPAdapterBuy);
        mLcVPAdapterBuy.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Constant.lcBuyOrSell = 1;
                FindAdvertiseResult.DataBean dataBean = (FindAdvertiseResult.DataBean) adapter.getItem(position);
                goToCreate(dataBean.getCoinName(), dataBean);
            }
        });

        //下拉刷新
        binding.swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.base));
        binding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    @Override
    public void initData() {
    }

    @Override
    public void initViewObservable() {
//        viewModel.uc.refresh.observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean aBoolean) {
//                if (aBoolean) {
//                    refresh();
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * 下拉刷新
     */
    private void refresh() {
        binding.swipeLayout.setRefreshing(true);

        viewModel.init(this, memberId, new OnRequestListener<FindAdvertiseResult>() {
            @Override
            public void onSuccess(FindAdvertiseResult findAdvertiseResult) {
                mBeanListSell.clear();
                mBeanListBuy.clear();
                for (FindAdvertiseResult.DataBean dataBean : findAdvertiseResult.getData()) {
                    if (dataBean.getAdvertiseType() == 0) {
                        mBeanListSell.add(dataBean);
                    }
                    if (dataBean.getAdvertiseType() == 1) {
                        mBeanListBuy.add(dataBean);
                    }
                }
                mLcVPAdapterSell.notifyDataSetChanged();
                mLcVPAdapterBuy.notifyDataSetChanged();
                binding.swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFail(String message) {
                Toasty.showError(message);
                binding.swipeLayout.setRefreshing(false);
            }
        });
    }

    private void goToCreate(final String coinName, FindAdvertiseResult.DataBean dataBean) {
        if (!App.getInstance().isAppLogin()) {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        } else {
            final FindPageResult.DataBean.RecordsBean recordsBean = new FindPageResult.DataBean.RecordsBean();
            recordsBean.setCoinName(coinName);
            recordsBean.setPrice(dataBean.getPrice());
            recordsBean.setMaxLimit(dataBean.getMaxLimit());
            recordsBean.setMinLimit(dataBean.getMinLimit());
            recordsBean.setRemainAmount(dataBean.getRemainAmount());
            recordsBean.setId(dataBean.getId());
            recordsBean.setRemark(dataBean.getRemark());

            viewModel.findData(coinName, new OnRequestListener<SpotWalletResult>() {
                @Override
                public void onSuccess(SpotWalletResult spotWalletResult) {
                    new XPopup.Builder(BusinessDetailsActivity.this)
                            .autoOpenSoftInput(true)
                            .asCustom(new LcTradePopup(BusinessDetailsActivity.this, spotWalletResult.getData(), Constant.lcBuyOrSell, recordsBean, new OnOrderCreateListener() {
                                @Override
                                public void onOrderCreate(final OrderCreateBean orderCreateBean) {
                                    showKeyboard(false);
                                    if (orderCreateBean.getOrderType().equals("1")) {
                                        showDialog(getString(R.string.loading));
                                        LcTradeClient.getInstance().orderCreate(orderCreateBean, coinName);
                                    } else {
                                        if (orderCreateBean.getTradePwd() == null) {
                                            new XPopup.Builder(BusinessDetailsActivity.this)
                                                    .autoOpenSoftInput(true)
                                                    .asCustom(new TradePwdPopup(BusinessDetailsActivity.this, new OnEtContentListener() {
                                                        @Override
                                                        public void onCEtContentInput(String content) {
                                                            showKeyboard(false);
                                                            orderCreateBean.setTradePwd(content);
                                                            showDialog(BusinessDetailsActivity.this.getString(R.string.loading));
                                                            LcTradeClient.getInstance().orderCreate(orderCreateBean, coinName);
                                                        }
                                                    }))
                                                    .show();
                                        } else {
                                            showDialog(getString(R.string.loading));
                                            LcTradeClient.getInstance().orderCreate(orderCreateBean, coinName);
                                        }
                                    }
                                }
                            }))
                            .show();
                }

                @Override
                public void onFail(String message) {
                    Toasty.showError(message);
                }
            });
        }
    }
}
