package com.spark.chiefwallet.app.me.safe.business;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityCertificationDetailsBinding;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.AdvertiseSelfClient;
import com.spark.otcclient.LcTradeClient;
import com.spark.otcclient.pojo.AdvertiseCoinListResult;
import com.spark.otcclient.pojo.AdvertiseCoinResult;

import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.utils.SpanUtils;

@Route(path = ARouterPath.ACTIVITY_ME_CERTIFICATIONDETAILS)
public class CertificationDetailsActivity extends BaseActivity<ActivityCertificationDetailsBinding, CertificationDetailsViewModel> {
    private TitleBean mTitleModel;

    private int type = 0;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_certification_details;
    }

    @Override
    public int initVariableId() {
        return BR.certificationDetailsViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.certificationTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);
        //查询自己认证商家状态
        showDialog("请求中");
        LcTradeClient.getInstance().getBusinessFind();
        //TitleSet
        mTitleModel = new TitleBean();
        setTitleListener(binding.certificationTitle.titleRootLeft);

        LcTradeClient.getInstance().authMerchantType();


        viewModel.initContext(this);
        binding.tvBusiness.setText(new SpanUtils().
                append(getString(R.string.personal_business_certification)).setForegroundColor(Color.BLACK).
                append(getString(R.string.personal_certifications)).create());


        if (!TextUtils.isEmpty(App.getInstance().getCurrentUser().getMobilePhone())) {
            type++;
            binding.tvRealname1.setText(getString(R.string.verified));
        } else
            binding.tvRealname1.setText(getString(R.string.unverified));

        if (App.getInstance().getCurrentUser().getCertifiedType() != null) {
            type++;
            binding.tvRealname.setText(getString(R.string.verified));
        } else
            binding.tvRealname.setText(getString(R.string.unverified));


        if (type == 2) {
            binding.applicationSubmission.setEnabled(true);
            binding.applicationSubmission.setBackgroundColor(ContextCompat.getColor(this, R.color.base));
        } else {
            binding.applicationSubmission.setEnabled(false);
            binding.applicationSubmission.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_bg));
        }

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.authentCodePopopShow.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                }
            }
        });
        viewModel.uc.mbusMoney.observe(this, new Observer<AdvertiseCoinResult>() {
            @Override
            public void onChanged(@Nullable AdvertiseCoinResult advertiseCoinListResult) {
                binding.tvBusiness2.setText(new SpanUtils().
                        append(getString(R.string.personal_business_certification2)).setForegroundColor(Color.BLACK).
                        append(getString(R.string.personal_certifications2)).
                        append("   " + advertiseCoinListResult.getData().getAmount() + "  " + advertiseCoinListResult.getData().getCoinName() + "   ").setForegroundColor(ContextCompat.getColor(CertificationDetailsActivity.this, R.color.base)).
                        append(getString(R.string.personal_certifications3)).create());
            }
        });

        viewModel.uc.TitleName.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mTitleModel.setTitleName(s + "");
                binding.certificationTitle.setViewTitle(mTitleModel);

            }
        });
    }
}
