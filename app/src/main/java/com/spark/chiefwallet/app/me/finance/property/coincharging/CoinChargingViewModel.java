package com.spark.chiefwallet.app.me.finance.property.coincharging;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.spark.acclient.FinanceClient;
import com.spark.acclient.pojo.CoinAddressBean;
import com.spark.acclient.pojo.CoinSupportBean;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.finance.property.contract.ContractAccountViewModel;
import com.spark.chiefwallet.ui.popup.CoinRechargePopup;
import com.spark.chiefwallet.ui.popup.impl.OnCoinRechargeListener;
import com.spark.chiefwallet.ui.toast.Toasty;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.StringUtils;

public class CoinChargingViewModel extends BaseViewModel {

    private CoinAddressBean coinAddressBean;
    public UIChangeObservable uc = new UIChangeObservable();
    public ObservableField<String> ImgUrl = new ObservableField<>();

    public class UIChangeObservable {
        public SingleLiveEvent<CoinAddressBean> coinAddressBean = new SingleLiveEvent<>();
    }

    public CoinChargingViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * 充币详情
     *
     * @param coinAddress
     */
    public void getCoinInInfo(Context mcontext, String coinAddress) {
        showDialog(mcontext.getString(R.string.loading));
        FinanceClient.getInstance().getCoinAddress(coinAddress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //查询指定币种的钱包地址
            case EvKey.coinAddress:
                dismissDialog();
                if (eventBean.isStatue()) {
                    CoinAddressBean coinAddressBean = (CoinAddressBean) eventBean.getObject();
                    ImgUrl.set(initImgUrl(coinAddressBean.getData().getCoinId()));
                    uc.coinAddressBean.setValue(coinAddressBean);
                } else {
                    Toasty.showError(eventBean.getMessage());
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public String initImgUrl(String coinId) {
        String url = "";
        if (!StringUtils.isEmpty(Constant.accountJson)) {
            CoinSupportBean coinSupportBean = BaseApplication.gson.fromJson(Constant.accountJson, CoinSupportBean.class);
            for (CoinSupportBean.DataBean dataBean : coinSupportBean.getData()) {
                if (dataBean.getCoinName().equals(coinId)) {
                    url = dataBean.getIconUrl();
                }
            }
        }
        return url;
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
