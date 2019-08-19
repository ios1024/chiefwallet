package com.spark.chiefwallet.app.quotes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.quotes.viewpager.QuotesVPFragment;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.QuotesFilterBean;
import com.spark.chiefwallet.databinding.FragmentQuotesBinding;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerNoCacheAdapter;
import com.spark.modulespot.pojo.SpotCoinResult;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseFragment;
import me.spark.mvvm.base.BaseRequestCode;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LogUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/18
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class QuotesFragment extends BaseFragment<FragmentQuotesBinding, QuotesViewModel> {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private SlideTabPagerNoCacheAdapter mAdapter;
    private boolean isInitTabSuccess = false;
    private int mCoinFilter = 0;  //0 - 默认 1 - 正序 2 - 倒序
    private int mCloseFilter = 0;
    private int mChangeFilter = 0;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_quotes;
    }

    @Override
    public int initVariableId() {
        return BR.quotesViewModel;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        viewModel.isVisible2User = !hidden;
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void initData() {
        isInitTabSuccess = false;
        if (!binding.quotesRoot.isLoadingCurrentState())
            binding.quotesRoot.showLoading();
        //先判断splash页面是否请求到币币可用币种数据
        if (!Constant.coinPairThumbBeanList.isEmpty()) {
            initThumb();
        } else {
            //查询平台支持的币币可用币种
            viewModel.getSpotCoinAll(getActivity(), new OnRequestListener<SpotCoinResult>() {
                @Override
                public void onSuccess(SpotCoinResult spotCoinResult) {
                    if (Constant.coinPairThumbBeanList.isEmpty()) return;
                    initThumb();
                }

                @Override
                public void onFail(String message) {
                    binding.quotesRoot.showError(R.drawable.svg_no_network, getString(R.string.network_abnormal), message, getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
                }
            });
        }
    }

    private void initThumb() {
        if (isInitTabSuccess) return;
        isInitTabSuccess = true;

        mTitles = Constant.coinPairThumbBeanList.toArray(new String[Constant.coinPairThumbBeanList.size()]);
        LogUtils.e("mTitles", Arrays.toString(mTitles));
        for (String title : mTitles) {
//            if (Constant.isHttpAndWs) {
//                mFragments.add(QuotesReceiveFragment.newInstance(title));
//            } else {
//                mFragments.add(QuotesThumbFragment.newInstance(title));
//            }
            mFragments.add(QuotesVPFragment.newInstance(title));
        }
        mAdapter = new SlideTabPagerNoCacheAdapter(getChildFragmentManager(),
                mFragments, mTitles);
        binding.quotesVp.setAdapter(mAdapter);
        binding.quotesTab.setViewPager(binding.quotesVp);
        binding.quotesVp.setOffscreenPageLimit(mTitles.length - 1);
        if (!binding.quotesRoot.isContentCurrentState()) {
            binding.quotesRoot.showContent();
        }
    }

    @OnClick({R.id.coin_search,
            R.id.coin_filter,
            R.id.close_filter,
            R.id.change_filter})
    public void onClick(View view) {
        if (!isInitTabSuccess) return;
        switch (view.getId()) {
            case R.id.coin_search:
                if (TextUtils.isEmpty(Constant.searchQuotesJson)) return;
                ARouter.getInstance().build(ARouterPath.ACTIVITY_QUOTES_SERACH)
                        .navigation();
                break;
            case R.id.coin_filter:
                mCoinFilter += 1;
                if (mCoinFilter == 3) mCoinFilter = 0;
                switch (mCoinFilter) {
                    case 0:
                        binding.imgCoinFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                        break;
                    case 1:
                        binding.imgCoinFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter_up));
                        break;
                    case 2:
                        binding.imgCoinFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter_down));
                        break;
                }
                mCloseFilter = 0;
                binding.imgCloseFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                mChangeFilter = 0;
                binding.imgChangeFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                quotesFilter(0);
                break;
            case R.id.close_filter:
                mCloseFilter += 1;
                if (mCloseFilter == 3) mCloseFilter = 0;
                switch (mCloseFilter) {
                    case 0:
                        binding.imgCloseFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                        break;
                    case 1:
                        binding.imgCloseFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter_up));
                        break;
                    case 2:
                        binding.imgCloseFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter_down));
                        break;
                }
                mCoinFilter = 0;
                binding.imgCoinFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                mChangeFilter = 0;
                binding.imgChangeFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                quotesFilter(1);
                break;
            case R.id.change_filter:
                mChangeFilter += 1;
                if (mChangeFilter == 3) mChangeFilter = 0;
                switch (mChangeFilter) {
                    case 0:
                        binding.imgChangeFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                        break;
                    case 1:
                        binding.imgChangeFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter_up));
                        break;
                    case 2:
                        binding.imgChangeFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter_down));
                        break;
                }
                mCoinFilter = 0;
                binding.imgCoinFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                mCloseFilter = 0;
                binding.imgCloseFilter.setImageDrawable(getResources().getDrawable(R.drawable.svg_quotes_filter));
                quotesFilter(2);
                break;
        }
    }

    /**
     * 筛选
     *
     * @param type 0 - 币种  1 - 最新价  2 - 涨跌幅
     */
    private void quotesFilter(int type) {
        QuotesFilterBean quotesFilterBean = new QuotesFilterBean();
        quotesFilterBean.setType(type);
        switch (type) {
            case 0:
                quotesFilterBean.setFilterType(mCoinFilter);
                break;
            case 1:
                quotesFilterBean.setFilterType(mCloseFilter);
                break;
            case 2:
                quotesFilterBean.setFilterType(mChangeFilter);
                break;
        }
        EventBusUtils.postSuccessEvent(EvKey.quotesFilter, BaseRequestCode.OK, "", quotesFilterBean);
    }
}
