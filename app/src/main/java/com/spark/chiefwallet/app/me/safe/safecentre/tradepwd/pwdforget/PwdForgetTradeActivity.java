package com.spark.chiefwallet.app.me.safe.safecentre.tradepwd.pwdforget;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityTradePwdForgetBinding;
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
 * 创建日期：2019/7/31
 * 描    述：忘记密码-根据手机号码重置资金密码
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_ME_TRADE_PWD_FORGET)
public class PwdForgetTradeActivity extends BaseActivity<ActivityTradePwdForgetBinding, PwdForgetTradeViewModel> {
    private TitleBean mTitleModel;

    @Override
    public int initContentView(Bundle savedInstanceate) {
        return R.layout.activity_trade_pwd_forget;
    }

    @Override
    public int initVariableId() {
        return BR.pwdForgetTradeViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.loginTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        binding.loginTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.loginTitle.titleRootLeft);

        viewModel.initContext(this);
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
    }

    @OnClick({R.id.get_sms_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_sms_code:
                if (StringUtils.isEmpty(viewModel.countryCode.get())) {
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
                viewModel.getPhoneCode();
                break;
        }
    }
}
