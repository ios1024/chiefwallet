package com.spark.chiefwallet.app.me.finance.property.transfer;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.spark.acclient.FinanceClient;
import com.spark.acclient.pojo.CoinSupportBean;
import com.spark.acclient.pojo.MerberWalletResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.CoinChoosePopup;
import com.spark.chiefwallet.ui.popup.CoinTransPopup;
import com.spark.chiefwallet.ui.popup.impl.OnCoinChooseListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.AdvertiseScanClient;
import com.spark.otcclient.pojo.LcCoinListResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

public class TransferViewModel extends BaseViewModel {
    public TransferViewModel(@NonNull Application application) {
        super(application);
    }

    private int typeCoin;
    private String mCoin;

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<MerberWalletResult> coinData = new SingleLiveEvent<>();
    }

    public void updateCoin(int mtypeCoin, String mCoin) {
        this.typeCoin = mtypeCoin;
        this.mCoin = mCoin;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //法币选定币种提币信息
            case EvKey.merberOtcWallet:
                dismissDialog();
                if (eventBean.isStatue()) {
                    MerberWalletResult merberWalletResult = (MerberWalletResult) eventBean.getObject();
                    uc.coinData.setValue(merberWalletResult);
                } else {
                    Toasty.showError(eventBean.getMessage());
                    finish();
                }
                break;
//            //查询平台支持到币种信息
//            case EvKey.coinSupport:
//                dismissDialog();
//                if (eventBean.isStatue()) {
//                    final CoinSupportBean coinSupportBean = (CoinSupportBean) eventBean.getObject();
//                    if (coinSupportBean.getData().isEmpty()) {
//                        Toasty.showError(App.getInstance().getString(R.string.no_support_coin));
//                        return;
//                    }
//                    uc.coinSupportData.setValue(coinSupportBean);
//
//                } else {
//                    Toasty.showError(eventBean.getMessage());
//                }
//                break;
//            //查询法币可用币种
//            case EvKey.indexCoinList:
//                dismissDialog();
//                if (eventBean.isStatue()) {
//                    Constant.lcCoinPairThumbBeanList.clear();
//                    for (LcCoinListResult.DataBean dataBean : ((LcCoinListResult) eventBean.getObject()).getData()) {
//                        if (!Constant.lcCoinPairThumbBeanList.contains(dataBean.getCoinName()))
//                            Constant.lcCoinPairThumbBeanList.add(dataBean.getCoinName());
//                    }
//                } else {
//                    Toasty.showError(App.getInstance().getString(R.string.network_abnormal));
//                }
//                break;
            //划转
            case EvKey.coinTransfer:
                dismissDialog();
                if (eventBean.isStatue()) {
                    showDialog("请求中...");
                    FinanceClient.getInstance().getCoinOutOtcInfo(typeCoin == 0 ? "/OTC/" : "/SPOT/", mCoin);
                    Toasty.showSuccess(App.getInstance().getString(R.string.coin_trans_success));
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
}
