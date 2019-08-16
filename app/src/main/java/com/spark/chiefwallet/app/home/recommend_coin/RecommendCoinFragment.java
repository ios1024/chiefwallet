package com.spark.chiefwallet.app.home.recommend_coin;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.home.recommend_coin.adapter.RecommendCoinAdapter;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.databinding.FragmentRecommendCoinBinding;
import com.spark.modulespot.pojo.AllThumbResult;
import com.spark.wsclient.pojo.RecommendCoinBean;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseFragment;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019-08-16
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RecommendCoinFragment extends BaseFragment<FragmentRecommendCoinBinding, RecommendCoinViewModel> {
    private static final String ListJson = "list";
    private RecommendCoinAdapter mRecommendCoinAdapter;
    private List<RecommendCoinBean.DataBean> mDataBeans = new ArrayList<>();

    public static RecommendCoinFragment newInstance(String listJson) {
        RecommendCoinFragment recommendCoinFragment = new RecommendCoinFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ListJson, listJson);
        recommendCoinFragment.setArguments(bundle);
        return recommendCoinFragment;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_recommend_coin;
    }

    @Override
    public int initVariableId() {
        return BR.recommendCoinViewModel;
    }

    @Override
    public void initView() {
        mDataBeans = App.gson.fromJson(getArguments().getString(ListJson), new TypeToken<List<RecommendCoinBean.DataBean>>() {
        }.getType());
        viewModel.initRecommendCoinData(mDataBeans);
        mRecommendCoinAdapter = new RecommendCoinAdapter(mDataBeans);
        binding.rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rv.setAdapter(mRecommendCoinAdapter);
        mRecommendCoinAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AllThumbResult.DataBean dataBean = new AllThumbResult.DataBean();
                dataBean.setUsdLegalAsset(mDataBeans.get(position).getUsdLegalAsset());
                dataBean.setChg(mDataBeans.get(position).getChg());
                dataBean.setChange(mDataBeans.get(position).getChange());
                dataBean.setVolume(mDataBeans.get(position).getVolume());
                dataBean.setLastDayClose(mDataBeans.get(position).getLastDayClose());
                dataBean.setHigh(mDataBeans.get(position).getHigh());
                dataBean.setCnyLegalAsset(mDataBeans.get(position).getCnyLegalAsset());
                dataBean.setLow(mDataBeans.get(position).getLow());
                dataBean.setClose(mDataBeans.get(position).getClose());
                dataBean.setOpen(mDataBeans.get(position).getOpen());
                dataBean.setSymbol(mDataBeans.get(position).getSymbol());
                dataBean.setBaseCoin(mDataBeans.get(position).getBaseCoin());
                dataBean.setBaseCoinScale(mDataBeans.get(position).getBaseCoinScale());
                dataBean.setBaseCoinScreenScale(mDataBeans.get(position).getBaseCoinScreenScale());
                dataBean.setCoinScale(mDataBeans.get(position).getCoinScale());
                dataBean.setCoinScreenScale(mDataBeans.get(position).getCoinScreenScale());

                ARouter.getInstance().build(ARouterPath.ACTIVITY_QUOTES_KLINE_HTTP)
                        .withParcelable("quotesThumbClick", dataBean)
                        .navigation();
            }
        });
    }

    @Override
    public void loadLazyData() {

    }

    @Override
    public void initViewObservable() {
        viewModel.uc.mAllThumbResultSingleLiveEvent.observe(this, new Observer<AllThumbResult>() {
            @Override
            public void onChanged(@Nullable AllThumbResult allThumbResult) {
                if (!isVisibleToUser) return;
                for (int i = 0; i < mDataBeans.size(); i++) {
                    for (AllThumbResult.DataBean dataBean : allThumbResult.getData()) {
                        if (dataBean.getSymbol().equals(mDataBeans.get(i).getSymbol())) {
                            mDataBeans.get(i).setUsdLegalAsset(dataBean.getUsdLegalAsset());
                            mDataBeans.get(i).setChg(dataBean.getChg());
                            mDataBeans.get(i).setChange(dataBean.getChange());
                            mDataBeans.get(i).setVolume(dataBean.getVolume());
                            mDataBeans.get(i).setLastDayClose(dataBean.getLastDayClose());
                            mDataBeans.get(i).setHigh(dataBean.getHigh());
                            mDataBeans.get(i).setCnyLegalAsset(dataBean.getCnyLegalAsset());
                            mDataBeans.get(i).setLow(dataBean.getLow());
                            mDataBeans.get(i).setClose(dataBean.getClose());
                            mDataBeans.get(i).setOpen(dataBean.getOpen());
                            break;
                        }
                    }
                }
                mRecommendCoinAdapter.notifyDataSetChanged();
            }
        });
    }
}
