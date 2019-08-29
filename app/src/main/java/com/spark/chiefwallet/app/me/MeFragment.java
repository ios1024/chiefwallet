package com.spark.chiefwallet.app.me;

import android.arch.lifecycle.Observer;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spark.acclient.FinanceClient;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.databinding.FragmentMeBinding;

import me.spark.mvvm.base.BaseFragment;
import me.spark.mvvm.ui.dialog.utils.StatusBarUtils;
/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/18
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MeFragment extends BaseFragment<FragmentMeBinding, MeViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_me;
    }


    @Override
    public int initVariableId() {
        return BR.meViewModel;
    }

    @Override
    public void initView() {

        viewModel.initContext(getContext());

        viewModel.updateAccount();

        //点击法币
        binding.frenchCurrencyAssets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.qiehuanbizhong.set("0");
                viewModel.initText();
                binding.frenchCurrencyAssets.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                binding.currencyAssets.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                binding.frenchCurrencyAssets.setTextColor(getResources().getColor(R.color.white));
                binding.currencyAssets.setTextColor(getResources().getColor(R.color.welcomelogin));

            }
        });
        //点击币币
        binding.currencyAssets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.qiehuanbizhong.set("1");
                viewModel.initText();
                binding.frenchCurrencyAssets.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                binding.currencyAssets.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                binding.frenchCurrencyAssets.setTextColor(getResources().getColor(R.color.welcomelogin));
                binding.currencyAssets.setTextColor(getResources().getColor(R.color.white));

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        viewModel.isVisible2User = !hidden;
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.safeLevel.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                binding.mePgb.setProgress(integer * 20);
                if (integer < 3) {
                    binding.mePgb.setProgressDrawable(getResources().getDrawable(R.drawable.pgb_danger));
                } else if (integer >= 3 && integer < 5) {
                    binding.mePgb.setProgressDrawable(getResources().getDrawable(R.drawable.pgb_warn));
                } else {
                    binding.mePgb.setProgressDrawable(getResources().getDrawable(R.drawable.pgb_safe));
                }
            }
        });
    }

}
