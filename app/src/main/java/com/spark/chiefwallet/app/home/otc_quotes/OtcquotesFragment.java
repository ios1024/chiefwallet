package com.spark.chiefwallet.app.home.otc_quotes;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.home.otc_quotes.adapter.OtcquotesAdapter;
import com.spark.chiefwallet.databinding.FragmentOtcQuotesBinding;
import com.spark.wsclient.pojo.B2BThumbBean;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseFragment;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.SPUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019-08-16
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class OtcquotesFragment extends BaseFragment<FragmentOtcQuotesBinding, OtcquotesViewModel> {
    private OtcquotesAdapter mOtcquotesAdapter;
    private List<B2BThumbBean.DateBean> mDateBeans = new ArrayList<>();

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_otc_quotes;
    }

    @Override
    public int initVariableId() {
        return BR.otcquotesViewModel;
    }

    @Override
    public void initView() {
        mOtcquotesAdapter = new OtcquotesAdapter(mDateBeans);
        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rv.setAdapter(mOtcquotesAdapter);
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.mRefresh.observe(this, new Observer<B2BThumbBean>() {
            @Override
            public void onChanged(@Nullable B2BThumbBean b2BThumbBean) {
                mDateBeans.clear();
                String currencytype;
                //1.人民币 CNY 2.美元 USDT 3.欧元 EUR 4.赛地 GHS 5.尼日利亚 NGN
                switch (SPUtils.getInstance().getPricingCurrency()) {
                    case "1":
                        currencytype = Constant.CNY;
                        binding.latestCurrency.setText(getString(R.string.latest_price) + "(" + Constant.CNY + ")");
                        break;
                    case "2":
                        currencytype = Constant.USD;
                        binding.latestCurrency.setText(getString(R.string.latest_price) + "(" + Constant.USD + ")");
                        break;
                    case "3":
                        currencytype = Constant.EUR;
                        binding.latestCurrency.setText(getString(R.string.latest_price) + "(" + Constant.EUR + ")");
                        break;
                    case "4":
                        currencytype = Constant.GHS;
                        binding.latestCurrency.setText(getString(R.string.latest_price) + "(" + Constant.GHS + ")");
                        break;
                    case "5":
                        currencytype = Constant.NGN;
                        binding.latestCurrency.setText(getString(R.string.latest_price) + "(" + Constant.NGN + ")");
                        break;
                    default:
                        currencytype = Constant.CNY;
                        binding.latestCurrency.setText(getString(R.string.latest_price) + "(" + Constant.CNY + ")");
                        break;
                }

                for (B2BThumbBean.DateBean dateBean : b2BThumbBean.getDate()) {
                    if (dateBean.getSymbol().endsWith(currencytype)) {
                        mDateBeans.add(dateBean);
                    }
                }
                mOtcquotesAdapter.notifyDataSetChanged();
            }
        });
    }
}
