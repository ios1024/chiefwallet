package com.spark.chiefwallet.app.me.user.register;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityRegisterBinding;
import com.spark.chiefwallet.ui.SmoothCheckBox;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.RegexUtils;
import com.spark.chiefwallet.util.StatueBarUtils;

import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.utils.StringUtils;

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
                viewModel.phoneNum.set("");
                viewModel.verifyCode.set("");
                viewModel.newPwd.set("");
                viewModel.newPwd.set("");
                viewModel.newPwdAgain.set("");
                binding.wechatCb.setChecked(false);
                viewModel.tiaolie.set("0");
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
                viewModel.phoneNum.set("");
                viewModel.verifyCode.set("");
                viewModel.newPwd.set("");
                viewModel.newPwd.set("");
                viewModel.newPwdAgain.set("");
                binding.wechatCb.setChecked(false);
                viewModel.tiaolie.set("0");

            }
        });

        binding.wechatCb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    viewModel.tiaolie.set("1");
                } else {
                    viewModel.tiaolie.set("0");
                }
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

    @OnClick({R.id.get_sms_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_sms_code:
                switch (viewModel.type) {
                    case 0:
                        if (StringUtils.isEmpty(viewModel.countryName.get())) {
                            Toasty.showError(getString(R.string.choose_country));
                            return;
                        }
                        if (StringUtils.isEmpty(viewModel.phoneNum.get())) {
                            Toasty.showError(getString(R.string.phone_num_hint));
                            return;
                        }
                        if (!RegexUtils.isMobileExact(viewModel.phoneNum.get())) {
                            Toasty.showError(getString(R.string.valid_phone));
                            return;
                        }
                        viewModel.getPhoneCode(0);
                        break;
                    case 1:
                        if (StringUtils.isEmpty(viewModel.countryName.get())) {
                            Toasty.showError(getString(R.string.choose_country));
                            return;
                        }
                        if (StringUtils.isEmpty(viewModel.phoneNum.get())) {
                            Toasty.showError(getString(R.string.email_address_hint));
                            return;
                        }
                        if (!RegexUtils.isEmail(viewModel.phoneNum.get())) {
                            Toasty.showError(getString(R.string.valid_email));
                            return;
                        }
                        viewModel.getPhoneCode(1);
                        break;

                }


                break;
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.mGetCodeSuccessLiveEvent.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                binding.getSmsCode.setVisibility(View.INVISIBLE);
                binding.llCountdownView.setVisibility(View.VISIBLE);
                binding.countdownView.start(60 * 1000);
                binding.countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                    @Override
                    public void onEnd(CountdownView cv) {
                        binding.getSmsCode.setVisibility(View.VISIBLE);
                        binding.llCountdownView.setVisibility(View.INVISIBLE);
                    }
                });
                Toasty.showSuccess(App.getInstance().getApplicationContext().getString(R.string.str_phone_code_success));
            }
        });
        //密码显示开关
        viewModel.uc.pwdSwitchEvent.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                binding.passpwdSwitch.setImageDrawable(aBoolean ?
                        getResources().getDrawable(R.drawable.svg_show) :
                        getResources().getDrawable(R.drawable.svg_hide));
                binding.Password.setTransformationMethod(aBoolean ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
                binding.Password.setSelection(viewModel.newPwd.get().length());
            }
        }); //密码显示开关
        viewModel.uc.newpwdSwitchEvent.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                binding.pwdSwitch.setImageDrawable(aBoolean ?
                        getResources().getDrawable(R.drawable.svg_show) :
                        getResources().getDrawable(R.drawable.svg_hide));
                binding.duplicatePassword.setTransformationMethod(aBoolean ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
                binding.duplicatePassword.setSelection(viewModel.newPwdAgain.get().length());
            }
        });

    }
}
