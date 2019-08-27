package com.spark.chiefwallet.app.me.system.setup;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivitySetUpBinding;
import com.spark.chiefwallet.util.StatueBarUtils;

import me.spark.mvvm.base.BaseActivity;

@Route(path = ARouterPath.ACTIVITY_ME_ABOUSETUP)
public class SetUpActivity extends BaseActivity<ActivitySetUpBinding, SetUpViewModel> {
    private TitleBean mTitleModel;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_set_up;
    }

    @Override
    public int initVariableId() {
        return BR.setUpViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        viewModel.initContext(this);


        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.setupTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleName(App.getInstance().getString(R.string.setting));
        binding.setupTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.setupTitle.titleRootLeft);
    }
}
