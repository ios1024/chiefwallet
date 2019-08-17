package com.spark.chiefwallet.app.me.user.pwdforget;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityPwdForgetBinding;
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
 * 创建日期：2019/5/20
 * 描    述：忘记密码
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_ME_FORGET_PWD)
public class PwdForgetActivity extends BaseActivity<ActivityPwdForgetBinding, PwdForgetViewModel> {
    @Autowired(name = "type")
    String type; //0 -找回密码  1 - 重置密码


    private TitleBean mTitleModel;

    @Override
    public int initContentView(Bundle savedInstanceate) {
        return R.layout.activity_pwd_forget;
    }

    @Override
    public int initVariableId() {
        return BR.pwdForgetViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.loginTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        if (type.equals("1")) {
            mTitleModel.setTitleName(getString(R.string.login_pwd_update));
        } else
            mTitleModel.setTitleName(getString(R.string.retrieve_the_password));

        binding.loginTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.loginTitle.titleRootLeft);

        viewModel.initContext(this);


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
//                binding.wechatCb.setChecked(false);
//                viewModel.tiaolie.set("0");
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
//                binding.wechatCb.setChecked(false);
//                viewModel.tiaolie.set("0");

            }
        });
    }

    @Override
    public void initViewObservable() {
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

    @OnClick({R.id.get_sms_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_sms_code:
                if (StringUtils.isEmpty(viewModel.countryName.get())) {
                    Toasty.showError(getString(R.string.choose_country));
                    return;
                }
                switch (viewModel.type) {
                    case 0:
                        if (StringUtils.isEmpty(viewModel.phoneNum.get())) {
                            Toasty.showError(getString(R.string.phone_num_hint));
                            return;
                        }
                        viewModel.getPhoneCode(0);

                        break;
                    case 1:
                        if (StringUtils.isEmpty(viewModel.phoneNum.get())) {
                            Toasty.showError(getString(R.string.email_address_hint));
                            return;
                        }
                        viewModel.getPhoneCode(1);

                        break;
                }

//                if (!RegexUtils.isMobileExact(viewModel.phoneNum.get())) {
//                    Toasty.showError(getString(R.string.valid_phone));
//                    return;
//                }
                break;
        }
    }
}
