package com.spark.chiefwallet.app.me.user.register;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityRegisterBinding;
import com.spark.chiefwallet.util.StatueBarUtils;

import me.spark.mvvm.base.BaseActivity;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_ME_REGISTER)
public class RegisterActivity extends BaseActivity<ActivityRegisterBinding, RegisterViewModel> {
    private TitleBean mTitleModel;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_register;
    }

    @Override
    public int initVariableId() {
        return BR.registerViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.registerTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setShowRightTV(true);
//        mTitleModel.setRightTV(getString(R.string.register_email));
        mTitleModel.setTitleName(getString(R.string.register));
        binding.registerTitle.titleLeftImg.setImageDrawable(getResources().getDrawable(R.drawable.svg_back));
        binding.registerTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.registerTitle.titleRootLeft);

        viewModel.initContext(this);

        viewModel.getArticleList();


        //点击切换
        binding.llMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.typeContext(0);
                binding.mobileTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                binding.mobileTv.setTextColor(getResources().getColor(R.color.mobile_login));
                binding.mailboxTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                binding.mailboxTv.setTextColor(getResources().getColor(R.color.welcomelogin));
                binding.mailboxView.setVisibility(View.GONE);
                binding.mobileView.setVisibility(View.VISIBLE);
                binding.phoneNum.setHint(App.getInstance().getResources().getString(R.string.phone_num_hint));
            }
        });
        binding.llMailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.typeContext(1);
                binding.mobileTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                binding.mobileTv.setTextColor(getResources().getColor(R.color.welcomelogin));
                binding.mailboxTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                binding.mailboxTv.setTextColor(getResources().getColor(R.color.mobile_login));
                binding.mailboxView.setVisibility(View.VISIBLE);
                binding.mobileView.setVisibility(View.GONE);
                binding.phoneNum.setHint(App.getInstance().getResources().getString(R.string.email_address_hint));

            }
        });
    }

    @Override
    protected void onTitleRightClick() {

        switch (viewModel.type) {
            //手机注册
            case 0:
                binding.registerTitle.titleRightTv.setText(getString(R.string.register_phone));
                viewModel.type = 1;
                viewModel.phoneNum.set("");
                viewModel.countryName.set("");
                viewModel.title.set(getString(R.string.register_email));
                viewModel.subTitle.set(getString(R.string.register_description_email));
                viewModel.registerType.set(getString(R.string.email_num));
                viewModel.registerHint.set(getString(R.string.email_num_hint));
                break;
            //邮箱注册
            case 1:
                binding.registerTitle.titleRightTv.setText(getString(R.string.register_email));
                viewModel.type = 0;
                viewModel.phoneNum.set("");
                viewModel.countryName.set("");
                viewModel.title.set(getString(R.string.register_phone));
                viewModel.subTitle.set(getString(R.string.register_description));
                viewModel.registerType.set(getString(R.string.phone_num));
                viewModel.registerHint.set(getString(R.string.phone_num_hint));
                break;
        }
    }
}
