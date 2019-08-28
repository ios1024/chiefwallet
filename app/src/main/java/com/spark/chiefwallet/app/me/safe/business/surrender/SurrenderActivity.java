package com.spark.chiefwallet.app.me.safe.business.surrender;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivitySurrenderBinding;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.LcTradeClient;

import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.utils.SpanUtils;

@Route(path = ARouterPath.ACTIVITY_BUSINESS_SURREND)
public class SurrenderActivity extends BaseActivity<ActivitySurrenderBinding, SurrenderViewModel> {


    private TitleBean mTitleModel;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_surrender;
    }

    @Override
    public int initVariableId() {
        return BR.surrenderViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.certificationTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleName(getString(R.string.publish_advertisements2));
        binding.certificationTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.certificationTitle.titleRootLeft);

        binding.tvSurplus.setText(new SpanUtils().append("剩余").append("300").setForegroundColor(ContextCompat.getColor(SurrenderActivity.this, R.color.base)).append("字").create());

        binding.etReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvSurplus.setText(new SpanUtils().append("剩余").append(300 - editable.length() + "").setForegroundColor(ContextCompat.getColor(SurrenderActivity.this, R.color.base)).append("字").create());
            }

        });
        binding.tvSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.etReason.getText())) {
                    Toasty.showError(getString(R.string.please_enter_your_reasons));
                } else
                    LcTradeClient.getInstance().getBusinessout(binding.etReason.getText() + "");
            }
        });
    }
}
