package com.spark.chiefwallet.app.quotes.viewpager;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.quotes.adapter.QuotesVPAdapter;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.QuotesFilterBean;
import com.spark.chiefwallet.databinding.FragmentQuotesVpBinding;
import com.spark.modulespot.pojo.AllThumbResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.spark.mvvm.base.BaseFragment;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class QuotesVPFragment extends BaseFragment<FragmentQuotesVpBinding, QuotesVPViewModel> {
    private static final String TYPE = "type";
    private List<AllThumbResult.DataBean> mThumbList = new ArrayList<>();
    private QuotesVPAdapter mQuotesThumbAdapter;
    private int mType = 0;
    private int mFilterType = 0;

    public static QuotesVPFragment newInstance(String quotesType) {
        QuotesVPFragment quotesVPFragment = new QuotesVPFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE, quotesType);
        quotesVPFragment.setArguments(bundle);
        return quotesVPFragment;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_quotes_vp;
    }

    @Override
    public int initVariableId() {
        return BR.quotesVPViewModel;
    }

    @Override
    public void initView() {
        viewModel.initType(getArguments().getString(TYPE));
    }

    @Override
    public void loadLazyData() {
        binding.thumbRoot.showLoading();

        mQuotesThumbAdapter = new QuotesVPAdapter(mThumbList);
        binding.thumbRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.thumbRv.setAdapter(mQuotesThumbAdapter);
        mQuotesThumbAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_QUOTES_KLINE_CHIEF)
                        .withParcelable("quotesThumbClick", mThumbList.get(position))
                        .navigation();
            }
        });

        if (getArguments().getString(TYPE).equals(App.getInstance().getString(R.string.favorites))
                && !App.getInstance().isAppLogin()) {
            binding.thumbRoot.showError(R.drawable.svg_no_data, getString(R.string.no_login), getString(R.string.log2view), getString(R.string.login), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                            .navigation();
                }
            });
        }
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.mAllThumbResultSingleLiveEvent.observe(this, new Observer<List<AllThumbResult.DataBean>>() {
            @Override
            public void onChanged(@Nullable List<AllThumbResult.DataBean> dataBeans) {
                if (!isVisibleToUser) return;
                mThumbList.clear();
                mThumbList.addAll(dataBeans);
                filterData();
                if (mThumbList.isEmpty()) {
                    mQuotesThumbAdapter.setEmptyView(R.layout.view_rv_empty_2, binding.thumbRv);
                }
                if (!binding.thumbRoot.isContentCurrentState())
                    binding.thumbRoot.showContent();
            }
        });

        viewModel.uc.isLogin.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (!getArguments().getString(TYPE).equals(App.getInstance().getString(R.string.favorites)))
                    return;
                if (aBoolean) {
                    binding.thumbRoot.showLoading();
                } else {
                    binding.thumbRoot.showError(R.drawable.svg_no_data, getString(R.string.no_login), getString(R.string.log2view), getString(R.string.login), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.thumbRoot.showLoading();
                            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                                    .navigation();
                        }
                    });
                }
            }
        });

        viewModel.uc.mQuotesFilterBeanSingleLiveEvent.observe(this, new Observer<QuotesFilterBean>() {
            @Override
            public void onChanged(@Nullable QuotesFilterBean quotesFilterBean) {
                Constant.isQuotesFilter = true;
                mType = quotesFilterBean.getType();
                mFilterType = quotesFilterBean.getFilterType();
                filterData();
            }
        });
    }

    /**
     * 筛选
     */
    private void filterData() {
        switch (mType) {
            case 0:
                switch (mFilterType) {
                    case 0:
                        break;
                    case 1:
                        Collections.sort(mThumbList, new Comparator<AllThumbResult.DataBean>() {
                            @Override
                            public int compare(AllThumbResult.DataBean o1, AllThumbResult.DataBean o2) {
                                // 按照币种进行升序排列
                                if (StringUtils.letterToNumber(o1.getSymbol().substring(0, 1).toUpperCase()) > StringUtils.letterToNumber(o2.getSymbol().substring(0, 1).toUpperCase())) {
                                    return 1;
                                }
                                if (StringUtils.letterToNumber(o1.getSymbol().substring(0, 1).toUpperCase()) == StringUtils.letterToNumber(o2.getSymbol().substring(0, 1).toUpperCase())) {
                                    return 0;
                                }
                                return -1;
                            }
                        });
                        break;
                    case 2:
                        Collections.sort(mThumbList, new Comparator<AllThumbResult.DataBean>() {
                            @Override
                            public int compare(AllThumbResult.DataBean o1, AllThumbResult.DataBean o2) {
                                // 按照币种进行降序排列
                                if (StringUtils.letterToNumber(o1.getSymbol().substring(0, 1).toUpperCase()) < StringUtils.letterToNumber(o2.getSymbol().substring(0, 1).toUpperCase())) {
                                    return 1;
                                }
                                if (StringUtils.letterToNumber(o1.getSymbol().substring(0, 1).toUpperCase()) == StringUtils.letterToNumber(o2.getSymbol().substring(0, 1).toUpperCase())) {
                                    return 0;
                                }
                                return -1;
                            }
                        });
                        break;
                }
                break;
            case 1:
                switch (mFilterType) {
                    case 0:
                        break;
                    case 1:
                        Collections.sort(mThumbList, new Comparator<AllThumbResult.DataBean>() {
                            @Override
                            public int compare(AllThumbResult.DataBean o1, AllThumbResult.DataBean o2) {
                                // 按照最新价进行升序排列
                                if (o1.getClose() > o2.getClose()) {
                                    return 1;
                                }
                                if (o1.getClose() == o2.getClose()) {
                                    return 0;
                                }
                                return -1;
                            }
                        });
                        break;
                    case 2:
                        Collections.sort(mThumbList, new Comparator<AllThumbResult.DataBean>() {
                            @Override
                            public int compare(AllThumbResult.DataBean o1, AllThumbResult.DataBean o2) {
                                // 按照最新价进行降序排列
                                if (o1.getClose() < o2.getClose()) {
                                    return 1;
                                }
                                if (o1.getClose() == o2.getClose()) {
                                    return 0;
                                }
                                return -1;
                            }
                        });
                        break;
                }
                break;
            case 2:
                switch (mFilterType) {
                    case 0:
                        break;
                    case 1:
                        Collections.sort(mThumbList, new Comparator<AllThumbResult.DataBean>() {
                            @Override
                            public int compare(AllThumbResult.DataBean o1, AllThumbResult.DataBean o2) {
                                // 按照涨跌幅进行升序排列
                                if (o1.getChg() > o2.getChg()) {
                                    return 1;
                                }
                                if (o1.getChg() == o2.getChg()) {
                                    return 0;
                                }
                                return -1;
                            }
                        });
                        break;
                    case 2:
                        Collections.sort(mThumbList, new Comparator<AllThumbResult.DataBean>() {
                            @Override
                            public int compare(AllThumbResult.DataBean o1, AllThumbResult.DataBean o2) {
                                // 按照涨跌幅进行降序排列
                                if (o1.getChg() < o2.getChg()) {
                                    return 1;
                                }
                                if (o1.getChg() == o2.getChg()) {
                                    return 0;
                                }
                                return -1;
                            }
                        });
                        break;
                }
                break;
        }

        mQuotesThumbAdapter.notifyDataSetChanged();
        Constant.isQuotesFilter = false;
    }
}
