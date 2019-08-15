package com.spark.chiefwallet.app.quotes.kline.introduction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.spark.chiefwallet.R;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.databinding.FragmentIntroductionBinding;

import me.spark.mvvm.base.BaseFragment;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/30
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class IntroductionFragment extends BaseFragment<FragmentIntroductionBinding,IntroductionViewModel>{
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_introduction;
    }

    @Override
    public int initVariableId() {
        return BR.introductionViewModel;
    }
}
