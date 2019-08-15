package com.spark.chiefwallet.app.quotes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.quotes.viewpager.QuotesReceiveFragment;
import com.spark.chiefwallet.app.quotes.viewpager.QuotesThumbFragment;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.FragmentQuotesBinding;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerNoCacheAdapter;
import com.spark.modulespot.pojo.SpotCoinResult;

import java.util.ArrayList;
import java.util.Arrays;

import me.spark.mvvm.base.BaseFragment;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.http.impl.OnRequestListener;
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
    private TitleBean mTitleModel;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private SlideTabPagerNoCacheAdapter mAdapter;
    private boolean isInitTabSuccess = false;

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
        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleName(getResources().getString(R.string.quotes));
        mTitleModel.setShowLeftImg(false);
        mTitleModel.setShowRightImg(true);
        binding.quotesTitle.titleRightImg.setImageDrawable(getResources().getDrawable(R.drawable.svg_search));
        binding.quotesTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.quotesTitle.titleRootLeft, binding.quotesTitle.titleRootRight);
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
                    binding.quotesRoot.showError(R.drawable.svg_no_network, "网络异常", message, "请重试", new View.OnClickListener() {
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
            if (Constant.isHttpAndWs) {
                mFragments.add(QuotesReceiveFragment.newInstance(title));
            } else {
                mFragments.add(QuotesThumbFragment.newInstance(title));
            }
        }
        mAdapter = new SlideTabPagerNoCacheAdapter(getChildFragmentManager(),
                mFragments, mTitles);
        binding.quotesVp.setAdapter(mAdapter);
        binding.quotesTab.setupWithViewPager(binding.quotesVp);
        binding.quotesVp.setOffscreenPageLimit(mTitles.length - 1);
        if (!binding.quotesRoot.isContentCurrentState()) {
            binding.quotesRoot.showContent();
        }
    }

    @Override
    protected void onTitleRightClick() {
        //搜索页面
        ARouter.getInstance().build(ARouterPath.ACTIVITY_QUOTES_SERACH)
                .navigation();
    }
}
