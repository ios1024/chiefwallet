package com.spark.chiefwallet.app.me.finance.property.withdrawmoney;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.spark.acclient.CaptchaAcClient;
import com.spark.acclient.FinanceClient;
import com.spark.acclient.pojo.CoinExtractBean;
import com.spark.acclient.pojo.CoinExtractSubmitBean;
import com.spark.acclient.pojo.CoinSupportBean;
import com.spark.acclient.pojo.MerberWalletResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.finance.property.transfer.TransferViewModel;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.CoinChoosePopup;
import com.spark.chiefwallet.ui.popup.CoinExtractPopup;
import com.spark.chiefwallet.ui.popup.CoinExtractSubmitPopup;
import com.spark.chiefwallet.ui.popup.CoinTransPopup;
import com.spark.chiefwallet.ui.popup.SmsVerifyPopup;
import com.spark.chiefwallet.ui.popup.impl.OnCoinChooseListener;
import com.spark.chiefwallet.ui.popup.impl.OnCoinExtractListener;
import com.spark.chiefwallet.ui.popup.impl.OnCoinExtractSubmitListener;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.AdvertiseScanClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

public class WithdrawMoneyViewModel extends BaseViewModel {
    public WithdrawMoneyViewModel(@NonNull Application application) {
        super(application);
    }

    public Context mContext;
    public String mCoin;
    private CoinExtractBean mCoinExtractBean;
    public ObservableField<String> ChoiceAddress = new ObservableField<>("");


    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<MerberWalletResult> merberWalletBean = new SingleLiveEvent<>();
        public SingleLiveEvent<CoinSupportBean> coinSupportBean = new SingleLiveEvent<>();
    }

    //提币地址
    public BindingCommand coinAddressOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_COINADDRESS_COINADDRESS)
                    .withString("Coin", mCoin)
                    .navigation();
        }
    });
    //ADD地址
    public BindingCommand cAddressOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_COINADDRESS_COINADDRESS_ADD)
                    .withString("Coin", mCoin)
                    .navigation();
        }
    });

    /**
     * 查询平台支持到币种信息
     */
    public void getCoinOutInfo(Context mContext, String Coin) {
        this.mContext = mContext;
        this.mCoin = Coin;
        showDialog();
        FinanceClient.getInstance().getCoinOutInfo(Coin);
        /**
         * 查询平台支持到币种信息
         */
        FinanceClient.getInstance().getCoinSupport();

    }

    //返回提币地址
    public String choiceAddress() {
        return ChoiceAddress.get();
    }

    public void btntrans(Context mContext, CoinExtractSubmitBean coinExtractSubmitBean) {
        new XPopup.Builder(mContext)
                .asCustom(new CoinExtractSubmitPopup(mContext, coinExtractSubmitBean, new OnCoinExtractSubmitListener() {
                    @Override
                    public void onReceiveCoinExtractSubmit(CoinExtractBean coinExtractBean) {
                        mCoinExtractBean = coinExtractBean;
                        showDialog(App.getInstance().getString(R.string.loading));
                        CaptchaAcClient.getInstance().phoneCaptcha(App.getInstance().getCurrentUser().getMobilePhone());
                    }
                }))
                .show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        if (Constant.ispropertyPauseVisiable) return;//数据拦截
        switch (eventBean.getOrigin()) {
//            币币选定币种提币信息
            case EvKey.merberWallet:
                if (eventBean.isStatue()) {
                    MerberWalletResult merberWalletResult = (MerberWalletResult) eventBean.getObject();
                    uc.merberWalletBean.setValue(merberWalletResult);
                } else {
                    Toasty.showError(eventBean.getMessage());
                    finish();
                }
                break;
            //查询平台支持到币种信息
            case EvKey.coinSupport:
                dismissDialog();
                if (eventBean.isStatue()) {
                    final CoinSupportBean coinSupportBean = (CoinSupportBean) eventBean.getObject();
                    uc.coinSupportBean.setValue(coinSupportBean);
                } else {
                    Toasty.showError(eventBean.getMessage());
                    finish();
                }
                break;
            //获取短信验证码
            case EvKey.acPhoneCaptcha:
                dismissDialog();
                if (eventBean.isStatue()) {
                    new XPopup.Builder(mContext)
                            .asCustom(new SmsVerifyPopup(mContext, App.getInstance().getCurrentUser().getMobilePhone(), new OnEtContentListener() {
                                @Override
                                public void onCEtContentInput(String content) {
                                    showDialog(mContext.getString(R.string.loading));
                                    FinanceClient.getInstance().withdrawRequest(content, mCoinExtractBean);
                                }
                            })).show();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //返回的地址
            case EvKey.CoinAddress:
                ChoiceAddress.set(eventBean.getMessage());
                break;
            //提筆
            case EvKey.coinWithdrawRequest:
                dismissDialog();
                if (eventBean.isStatue()) {
                    Toasty.showSuccess(App.getInstance().getString(R.string.coin_out_success));
                    finish();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegister(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        Constant.ispropertyPauseVisiable = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Constant.ispropertyPauseVisiable = true;
    }
}
