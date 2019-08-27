package com.spark.chiefwallet.app.me.finance.property.transfer;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.noober.background.view.Const;
import com.spark.acclient.FinanceClient;
import com.spark.acclient.pojo.CoinSupportBean;
import com.spark.acclient.pojo.CoinTransBean;
import com.spark.acclient.pojo.MerberWalletResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityTransferBinding;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.AdvertiseScanClient;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.DfUtils;

@Route(path = ARouterPath.ACTIVITY_ME_TRANSFER)
public class TransferActivity extends BaseActivity<ActivityTransferBinding, TransferViewModel> {
    @Autowired(name = "Coin")
    String Coin;
    @Autowired(name = "mBusiType")
    String mBusiType;

    private String[] coinList;
    private String[] b2bList, c2cList;

    private TitleBean mTitleModel;
    private int from, to;//0 - 法币 1 -币币
    private String balance;//法币 币币 可用

//    private boolean clickType = true;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_transfer;
    }

    @Override
    public int initVariableId() {
        return BR.transferViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.coinChargingTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        mTitleModel = new TitleBean();
        mTitleModel.setShowRightTV(true);
        mTitleModel.setTitleName(Coin + getResources().getString(R.string.coin_in));
        binding.coinChargingTitle.titleLeftImg.setImageDrawable(getResources().getDrawable(R.drawable.svg_back));
        binding.coinChargingTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.coinChargingTitle.titleRootLeft);
//
//        showDialog("请求中..");
//        if (Constant.lcCoinPairThumbBeanList.isEmpty()) {
//            AdvertiseScanClient.getInstance().getIndexTradeCoinList();
//        } else
//            FinanceClient.getInstance().getCoinSupport();


        binding.transSymbol.setText(Coin);
        binding.transSymbol2.setText(Coin);

        binding.transNumAvailable.setText(App.getInstance().getString(R.string.available) + "- -" + Coin);
        if (mBusiType.equals("OTC")) {//法币
            binding.transFromTv.setText(this.getString(R.string.c2c_account));
            binding.transFromTo.setText(this.getString(R.string.b2b_account));

            from = 0; // 法币
            to = 1;
        } else {//币币
            binding.transFromTv.setText(this.getString(R.string.b2b_account));
            binding.transFromTo.setText(this.getString(R.string.c2c_account));
            from = 1;
            to = 0;
        }

        showDialog("请求中...");
        FinanceClient.getInstance().getCoinOutOtcInfo(from == 0 ? "/OTC/" : "/SPOT/", Coin);
    }

    @OnClick({R.id.trans_all, R.id.btn_trans, R.id.trans_reverse})
    public void OnClick(View view) {
        switch (view.getId()) {
            //翻转
            case R.id.trans_reverse:
                int temp = from;
                from = to;
                to = temp;
                binding.transFromTv.setText(from == 0 ? this.getString(R.string.c2c_account) : this.getString(R.string.b2b_account));
                binding.transFromTo.setText(to == 1 ? this.getString(R.string.b2b_account) : this.getString(R.string.c2c_account));
                binding.transNumAvailable.setText(App.getInstance().getString(R.string.available) + "- -" + Coin);
                showDialog("请求中...");
                FinanceClient.getInstance().getCoinOutOtcInfo(from == 0 ? "/OTC/" : "/SPOT/", Coin);
                break;
            //全部提取
            case R.id.trans_all:
                binding.transNum.setText(balance);
                break;
            //划转
            case R.id.btn_trans:

                if (TextUtils.isEmpty(binding.transNum.getText().toString().trim())) {
                    Toasty.showError(this.getString(R.string.trans_number_hint));
                    return;
                }

                if (Double.valueOf(binding.transNum.getText().toString().trim()) == 0) {
                    Toasty.showError(this.getString(R.string.trans_cannot_0));
                    return;
                }

                if (Double.valueOf(binding.transNum.getText().toString().trim()) > Double.valueOf(balance)) {
                    Toasty.showError(this.getString(R.string.trans_correct_number_hint));
                    return;
                }

                CoinTransBean coinTransBean = new CoinTransBean();
                coinTransBean.setAmount(Double.valueOf(binding.transNum.getText().toString().trim()));
                coinTransBean.setCoinName(Coin);
                switch (from) {
                    case 0:
                        coinTransBean.setFrom("OTC");
                        break;
                    case 1:
                        coinTransBean.setFrom("SPOT");
                        break;

                }
                switch (to) {
                    case 0:
                        coinTransBean.setTo("OTC");
                        break;
                    case 1:
                        coinTransBean.setTo("SPOT");
                        break;

                }

                showDialog("请求中...");
                viewModel.updateCoin(from, Coin);
                FinanceClient.getInstance().coinTransfer(coinTransBean);
                break;
            default:
                break;
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.coinData.observe(this, new Observer<MerberWalletResult>() {
            @Override
            public void onChanged(@Nullable MerberWalletResult merberWalletResult) {
                binding.transNum.setText("");
                balance = DfUtils.numberFormatDown(merberWalletResult.getData().getBalance(), 8);
                binding.transNumAvailable.setText(App.getInstance().getString(R.string.available) + balance + " " + Coin);
            }
        });
//        viewModel.uc.coinSupportData.observe(this, new Observer<CoinSupportBean>() {
//            @Override
//            public void onChanged(@Nullable CoinSupportBean coinSupportBean) {
//
//                b2bList = new String[coinSupportBean.getData().size()];
//                for (int i = 0; i < coinSupportBean.getData().size(); i++) {
//                    b2bList[i] = coinSupportBean.getData().get(i).getCoinName();
//                }
//                c2cList = new String[Constant.lcCoinPairThumbBeanList.size()];
//                for (int i = 0; i < Constant.lcCoinPairThumbBeanList.size(); i++) {
//                    c2cList[i] = Constant.lcCoinPairThumbBeanList.get(i);
//                }
//
//                intersect(b2bList, c2cList);
//            }
//        });
//
//    }

//    //交集(注意结果集中若使用LinkedList添加，则需要判断是否包含该元素，否则其中会包含重复的元素)
//    private void intersect(String[] arr1, String[] arr2) {
//        List<String> l = new LinkedList<>();
//        Set<String> common = new HashSet<>();
//        for (String str : arr1) {
//            if (!l.contains(str)) {
//                l.add(str);
//            }
//        }
//        for (String str : arr2) {
//            if (l.contains(str)) {
//                common.add(str);
//            }
//        }
//        String[] result = {};
//        coinList = common.toArray(result);
//        for (int i = 0; i < coinList.length; i++) {
//            if (coinList.equals(Coin)) {
//                return;
//            } else {
//                clickType = false;
//                binding.btnTrans.setEnabled(false);
//                binding.btnTrans.setText(getString(R.string.str_b2b_account_no_support));
//            }
//        }
    }
}
