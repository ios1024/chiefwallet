package com.spark.chiefwallet.app.me.user.register.password;

import android.graphics.Color;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityRegisterPwdBinding;
import com.spark.chiefwallet.util.StatueBarUtils;

import me.spark.mvvm.base.BaseActivity;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/28
 * 描    述：设置密码 - 密码
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_ME_REGISTER_PWD)
public class RegisterPwdActivity extends BaseActivity<ActivityRegisterPwdBinding, RegisterPwdViewModel> {
    @Autowired(name = "phoneNum")
    String phoneNum;
    @Autowired(name = "strCountry")
    String strCountry;
    @Autowired(name = "code")
    String mCode;
    @Autowired(name = "type")
    int type;

    private TitleBean mTitleModel;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_register_pwd;
    }

    @Override
    public int initVariableId() {
        return BR.registerPwdViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.registerPwdTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setShowLeftImg(true);
        binding.registerPwdTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.registerPwdTitle.titleRootLeft);

        viewModel.initData(this,phoneNum, strCountry,mCode,type);
    }
}
