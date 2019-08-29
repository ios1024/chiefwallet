package com.spark.chiefwallet.app.me.finance.property.withdrawmoney;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.noober.background.view.Const;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.spark.acclient.CaptchaAcClient;
import com.spark.acclient.pojo.CoinExtractBean;
import com.spark.acclient.pojo.CoinExtractSubmitBean;
import com.spark.acclient.pojo.CoinSupportBean;
import com.spark.acclient.pojo.MerberWalletResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.finance.property.coincharging.CoinChargingActivity;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityWithdrawMoneyBinding;
import com.spark.chiefwallet.ui.popup.CoinExtractSubmitPopup;
import com.spark.chiefwallet.ui.popup.impl.OnCoinExtractListener;
import com.spark.chiefwallet.ui.popup.impl.OnCoinExtractSubmitListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.StatueBarUtils;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.StringUtils;

@Route(path = ARouterPath.ACTIVITY_ME_WITHDRAWMONEY)
public class WithdrawMoneyActivity extends BaseActivity<ActivityWithdrawMoneyBinding, WithdrawMoneyViewModel> {
    @Autowired(name = "Coin")
    String Coin;

    private TitleBean mTitleModel;
    private String allMax;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_withdraw_money;
    }

    @Override
    public int initVariableId() {
        return BR.withdrawMoneyViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.coinChargingTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        mTitleModel = new TitleBean();
        mTitleModel.setShowRightTV(true);
        mTitleModel.setTitleName(Coin + getString(R.string.coin_out));
        binding.coinChargingTitle.titleLeftImg.setImageDrawable(getResources().getDrawable(R.drawable.svg_back));
        binding.coinChargingTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.coinChargingTitle.titleRootLeft);
        viewModel.getCoinOutInfo(this, Coin);

    }

    @OnClick({R.id.btn_trans, R.id.tv_feedback,R.id.coin_extract_choose_all})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.coin_extract_choose_all:
                binding.coinExtractNumber.setText(allMax);
                break;
            case R.id.tv_feedback:

                String title = App.getInstance().getResources().getString(R.string.exchief_customer_service);
/**
 * 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入。
 * 三个参数分别为：来源页面的url，来源页面标题，来源页面额外信息（保留字段，暂时无用）。
 * 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
 */
                ConsultSource source = new ConsultSource(null, App.getInstance().getCurrentUser().getMobilePhone(), "custom information string");
/**
 * 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable()，
 * 如果返回为false，该接口不会有任何动作
 *
 * @param context 上下文
 * @param title   聊天窗口的标题
 * @param source  咨询的发起来源，包括发起咨询的url，title，描述信息等
 */
                Unicorn.openServiceActivity(WithdrawMoneyActivity.this, title, source);
                break;
            case R.id.btn_trans:
                if (Double.valueOf(binding.coinExtractNumber.getText().toString().trim()) > Double.valueOf(allMax)) {
                    Toasty.showError(Coin + this.getString(R.string.insufficient_quantity));
                    return;
                }
                if (TextUtils.isEmpty(viewModel.choiceAddress().toString().trim())) {
                    Toasty.showError(this.getString(R.string.mention_address_cannot_empty));
                    return;
                }
                if (TextUtils.isEmpty(binding.coinExtractTag.getText().toString().trim())) {
                    Toasty.showError(this.getString(R.string.label_cannot_empty));
                    return;
                }
                if (TextUtils.isEmpty(binding.coinExtractNumber.getText().toString().trim())) {
                    Toasty.showError(this.getString(R.string.number_cannot_empty));
                    return;
                }

                CoinExtractSubmitBean coinExtractSubmitBean = new CoinExtractSubmitBean();
                coinExtractSubmitBean.setAddress(viewModel.choiceAddress().toString().trim());
                coinExtractSubmitBean.setTag(binding.coinExtractTag.getText().toString().trim());
                coinExtractSubmitBean.setAmount(String.valueOf(Double.valueOf(binding.coinExtractNumber.getText().toString().trim())));
                coinExtractSubmitBean.setCoinName(Coin);
                coinExtractSubmitBean.setServiceCharge(binding.coinExtractServiceCharge.getText().toString());

                viewModel.btntrans(this, coinExtractSubmitBean);

                break;
            default:
                break;
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.coinSupportBean.observe(this, new Observer<CoinSupportBean>() {
            @Override
            public void onChanged(@Nullable final CoinSupportBean coinSupportBean) {
                for (int i = 0; i < coinSupportBean.getData().size(); i++)
                    if (coinSupportBean.getData().get(i).getCoinName().equals(Coin)) {
                        binding.coinExtractServiceCharge.setText(coinSupportBean.getData().get(i).getWithdrawFee() + " " + Coin);
                        binding.tvMax.setText(coinSupportBean.getData().get(i).getMaxWithdrawAmount() + " " + Coin);
                        binding.tvMix.setText(coinSupportBean.getData().get(i).getMinWithdrawAmount() + " " + Coin);
                        final double tvMax = coinSupportBean.getData().get(i).getMaxWithdrawAmount();
                        final double addMax = coinSupportBean.getData().get(i).getMaxWithdrawAmount();
                        final double tvMin = coinSupportBean.getData().get(i).getMinWithdrawAmount();

                        binding.jinrixiane.setText(coinSupportBean.getData().get(i).getDailyMaxWithdrawAmount() + " " + Coin);
                        final int withdrawFeeType = coinSupportBean.getData().get(i).getWithdrawFeeType();
                        final double withdrawFee = coinSupportBean.getData().get(i).getWithdrawFee();
                        //1 - 固定金额 2 - 按比列
                        if (withdrawFeeType == 1) {
                            binding.coinExtractServiceCharge.setText(String.valueOf(DfUtils.numberFormat(coinSupportBean.getData().get(i).getWithdrawFee(), 8)) + Coin);
                        }
//                        else {
//                            binding.coinExtractServiceCharge.setText(coinSupportBean.getData().get(i).getMinWithdrawFee() + " - " + coinSupportBean.getData().get(i).getMaxWithdrawFee() + " " + Coin);
//                        }
                        binding.coinExtractNumber.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                                if (!StringUtils.isEmpty(s.toString())) {
                                    if (s.toString().trim().equals("") || s.toString().trim().equals(".") || Double.valueOf(s.toString().trim()) == 0) {
                                        return;
                                    }
                                    if (Double.valueOf(s.toString().trim()) < tvMin) {
                                        binding.coinExtractNumber.setText(String.valueOf(tvMin));
                                    } else if (Double.valueOf(s.toString().trim()) > tvMax) {
                                        binding.coinExtractNumber.setText(String.valueOf(tvMax));
                                    }

                                    binding.coinExtractServiceCharge.setText(withdrawFeeType == 1 ?
                                            String.valueOf(DfUtils.roundUp(withdrawFee, 8)) :
                                            String.valueOf(DfUtils.roundUp(Double.valueOf(s.toString()) * withdrawFee, 8)));
                                } else {
                                    binding.coinExtractServiceCharge.setText(withdrawFeeType == 1 ?
                                            String.valueOf(DfUtils.roundUp(withdrawFee, 8)) :
                                            "0");
                                }
                            }
                        });
                    }
            }
        });
        viewModel.uc.merberWalletBean.observe(this, new Observer<MerberWalletResult>() {
            @Override
            public void onChanged(@Nullable MerberWalletResult merberWalletResult) {

                allMax = DfUtils.numberFormatDown(merberWalletResult.getData().getBalance(), 8);
                binding.coinExtractAvailable.setText(DfUtils.numberFormatDown(merberWalletResult.getData().getBalance(), 8) + " " + Coin);

//                binding.coinExtractChooseAll.setText(DfUtils.numberFormatDown(merberWalletResult.getData().getBalance(), 8));
            }
        });
    }
}
