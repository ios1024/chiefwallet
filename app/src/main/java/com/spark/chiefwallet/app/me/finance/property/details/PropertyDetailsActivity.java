package com.spark.chiefwallet.app.me.finance.property.details;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.spark.acclient.pojo.CoinTransDetailsResult;
import com.spark.acclient.pojo.SpotWalletResult;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.finance.property.adapter.PropertyDetailsAdapter;
import com.spark.chiefwallet.app.me.finance.property.adapter.PropertyLockDetailsAdapter;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityPropertyDetailsBinding;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.ucclient.pojo.LockProoertDetailsResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.http.impl.OnRequestListener;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/4
 * 描    述：
 * 修订历史：
 * ================================================
 */

@Route(path = ARouterPath.ACTIVITY_ME_PROPERTY_DETAILS)
public class PropertyDetailsActivity extends BaseActivity<ActivityPropertyDetailsBinding, PropertyDetailsViewModel> {
    @Autowired(name = "propertDetails")
    SpotWalletResult.DataBean propertDetailsBean;
    @Autowired
    String busiType;

    private TitleBean mTitleModel;
    private int pageIndex = 1;
    private int pageFilterIndex = 1;
    private PropertyDetailsAdapter mPropertyDetailsAdapter;
    private PropertyLockDetailsAdapter mPropertyLockDetailsAdapter;
    private List<CoinTransDetailsResult.DataBean.RecordsBean> mRecordsBeanList = new ArrayList<>();
    private List<LockProoertDetailsResult.DataBean.RecordsBean> mLockRecordsBeanList = new ArrayList<>();

    private String[] filterTypeList;
    private boolean isFlilterResult = false;
    private String filterTypeSelect;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_property_details;
    }

    @Override
    public int initVariableId() {
        return BR.propertyDetailsViewModel;
    }

    @Override
    public void initView() {
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.propertyDetailsTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        if (busiType.equals("OTC")) {
            binding.hide1.setVisibility(View.INVISIBLE);
            binding.hide2.setVisibility(View.INVISIBLE);
        }
        filterTypeList = getResources().getStringArray(R.array.property_details_tab);
        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleName(propertDetailsBean.getCoinId() + getString(R.string.details2));
        binding.propertyDetailsTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.propertyDetailsTitle.titleRootLeft);
        viewModel.init(this, busiType, propertDetailsBean);


        mPropertyDetailsAdapter = new PropertyDetailsAdapter(mRecordsBeanList);
        binding.rvPropertyDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPropertyDetails.setAdapter(mPropertyDetailsAdapter);
        mPropertyDetailsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });

        mPropertyLockDetailsAdapter = new PropertyLockDetailsAdapter(mLockRecordsBeanList);
        binding.rvPropertyLockDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPropertyLockDetails.setAdapter(mPropertyLockDetailsAdapter);

        //下拉刷新
        binding.swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.base));
        binding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        binding.nestedScrollView.setOnScrollChangeListener(
                new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        //判断是否滑到的底部
                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                            loadMore();
                        }
                    }

                });
    }

    @Override
    public void initData() {
        viewModel.getType();
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.refresh.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    refresh();
                }
            }
        });
    }

    /**
     * 下拉刷新
     */
    private void refresh() {
        binding.swipeLayout.setRefreshing(true);
        if (!isFlilterResult) {
            pageIndex = 1;
            viewModel.loadPageDate(pageIndex, new OnRequestListener<CoinTransDetailsResult>() {
                @Override
                public void onSuccess(CoinTransDetailsResult coinTransDetailsResult) {
                    mRecordsBeanList.clear();
                    mRecordsBeanList.addAll(coinTransDetailsResult.getData().getRecords());
                    mPropertyDetailsAdapter.notifyDataSetChanged();
                    setData(true, coinTransDetailsResult.getData().getRecords());
                    binding.swipeLayout.setRefreshing(false);
                }

                @Override
                public void onFail(String message) {
                    Toasty.showError(message);
                    binding.swipeLayout.setRefreshing(false);
                }
            });
        } else {
            pageFilterIndex = 1;
            viewModel.loadPageDateFilter(pageFilterIndex, filterTypeSelect, new OnRequestListener<CoinTransDetailsResult>() {
                @Override
                public void onSuccess(CoinTransDetailsResult coinTransDetailsResult) {
                    mRecordsBeanList.clear();
                    mRecordsBeanList.addAll(coinTransDetailsResult.getData().getRecords());
                    mPropertyDetailsAdapter.notifyDataSetChanged();
                    setData(true, coinTransDetailsResult.getData().getRecords());
                    binding.swipeLayout.setRefreshing(false);
                }

                @Override
                public void onFail(String message) {
                    Toasty.showError(message);
                    binding.swipeLayout.setRefreshing(false);
                }
            });
        }
    }

    /**
     * 上拉加载
     */
    private void loadMore() {

        if (!isFlilterResult) {
            viewModel.loadPageDate(pageIndex, new OnRequestListener<CoinTransDetailsResult>() {
                @Override
                public void onSuccess(CoinTransDetailsResult coinTransDetailsResult) {
                    boolean isRefresh = pageIndex == 1;
                    setData(isRefresh, coinTransDetailsResult.getData().getRecords());
                }

                @Override
                public void onFail(String message) {
                    Toasty.showError(message);
                    binding.swipeLayout.setRefreshing(false);
                }
            });
        } else {
            viewModel.loadPageDateFilter(pageIndex, filterTypeSelect, new OnRequestListener<CoinTransDetailsResult>() {
                @Override
                public void onSuccess(CoinTransDetailsResult coinTransDetailsResult) {
                    boolean isRefresh = pageFilterIndex == 1;
                    setData(isRefresh, coinTransDetailsResult.getData().getRecords());
                }

                @Override
                public void onFail(String message) {
                    Toasty.showError(message);
                    binding.swipeLayout.setRefreshing(false);
                }
            });
        }
    }

    /**
     * * @param isRefresh
     *
     * @param data
     */
    private void setData(boolean isRefresh, List data) {
        if (!isFlilterResult) {
            pageIndex++;
        } else {
            pageFilterIndex++;
        }
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            mPropertyDetailsAdapter.setNewData(data);
            if (size == 0) {
                mPropertyDetailsAdapter.setEmptyView(R.layout.view_rv_empty, binding.rvPropertyDetails);
            }
        } else {
            if (size > 0) {
                mPropertyDetailsAdapter.addData(data);
            }
        }
    }


    @OnClick({R.id.filter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter:
                new XPopup.Builder(this)
                        .maxHeight((int) (XPopupUtils.getWindowHeight(this) * .85f))
                        .asBottomList("请选择", filterTypeList,
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if (position == 0) {
                                            isFlilterResult = false;
                                            filterTypeSelect = "1,2,3,4,5,6";
                                        } else {
                                            isFlilterResult = true;
                                            if (position == 5) {
                                                filterTypeSelect = "6";
                                            } else if (position == 6) {
                                                filterTypeSelect = "5";
                                            } else {
                                                filterTypeSelect = String.valueOf(position);
                                            }
                                        }
                                        refresh();
                                    }
                                })
                        .show();
                break;
        }
    }
}
