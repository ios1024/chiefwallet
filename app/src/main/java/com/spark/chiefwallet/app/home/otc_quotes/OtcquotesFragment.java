package com.spark.chiefwallet.app.home.otc_quotes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.databinding.FragmentOtcQuotesBinding;

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
public class OtcquotesFragment extends BaseFragment<FragmentOtcQuotesBinding, OtcquotesViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_otc_quotes;
    }

    @Override
    public int initVariableId() {
        return BR.otcquotesViewModel;
    }
}
