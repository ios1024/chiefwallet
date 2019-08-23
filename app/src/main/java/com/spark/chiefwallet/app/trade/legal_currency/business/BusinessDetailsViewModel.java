package com.spark.chiefwallet.app.trade.legal_currency.business;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.spark.acclient.FinanceClient;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.AdvertiseScanClient;
import com.spark.otcclient.pojo.FindMerchantDetailsResult;
import com.spark.otcclient.pojo.FindPageResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/4
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class BusinessDetailsViewModel extends BaseViewModel {
    public BusinessDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    private Context mContext;
    private OnRequestListener onRequestListener;
    private OnRequestListener onRequestListenerWallet;

    public ObservableField<String> nameShort = new ObservableField<>();
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> registerTime = new ObservableField<>();
    public ObservableField<String> totalSuccessCount = new ObservableField<>();
    public ObservableField<String> tvRate = new ObservableField<>();
    public ObservableField<String> totalSuccessCount30 = new ObservableField<>();
    public ObservableField<String> tvRelease = new ObservableField<>();
    public ObservableField<Boolean> isAuthEmail = new ObservableField<>();
    public ObservableField<Boolean> isAuthPhone = new ObservableField<>();
    public ObservableField<Boolean> isAuthID = new ObservableField<>();
    public ObservableField<Boolean> isAuthHigh = new ObservableField<>();
    private String coinName;
    private int memberId;


    public void init(Context context, int memberId, OnRequestListener onRequestListener) {
        this.mContext = context;
        this.memberId = memberId;
        this.onRequestListener = onRequestListener;
        AdvertiseScanClient.getInstance().findMerchantDetails(memberId);
        AdvertiseScanClient.getInstance().getAdvertiseFindUrl(memberId);
    }

    private void initViewDate(FindMerchantDetailsResult findMerchantDetailsResult) {
        nameShort.set(findMerchantDetailsResult.getData().getNameFirstChar());
        username.set(findMerchantDetailsResult.getData().getRealName());
        registerTime.set(App.getInstance().getString(R.string.str_business_register_time) + findMerchantDetailsResult.getData().getRegistTime());
        totalSuccessCount.set((findMerchantDetailsResult.getData().getTotalSuccessBuyOrder() + findMerchantDetailsResult.getData().getTotalSuccessSellOrder()) + App.getInstance().getString(R.string.str_business_total_count));
        tvRate.set(findMerchantDetailsResult.getData().formatRangeTimeOrderRate());
        totalSuccessCount30.set(findMerchantDetailsResult.getData().getRangeTimeSuccessOrder() + App.getInstance().getString(R.string.str_business_total_count));
        tvRelease.set(findMerchantDetailsResult.getData().getAvgReleaseTime() + App.getInstance().getString(R.string.text_minute));
        isAuthEmail.set(findMerchantDetailsResult.getData().isEmailAuthStatus());
        isAuthPhone.set(findMerchantDetailsResult.getData().isPhoneAuthStatus());
        isAuthID.set(findMerchantDetailsResult.getData().isRealnameAuthStatus());
        isAuthHigh.set(findMerchantDetailsResult.getData().isAdvanceAuthStaus());
    }

    /**
     * 查询可用余额
     */
    public void findData(String coinName, OnRequestListener onRequestListener) {
        showDialog(App.getInstance().getString(R.string.loading));
        this.onRequestListenerWallet = onRequestListener;
        this.coinName = coinName;
        FinanceClient.getInstance().getLcWalletAvailable(coinName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //商家信息
            case EvKey.findMerchantDetails:
                if (eventBean.isStatue()) {
                    FindMerchantDetailsResult findMerchantDetailsResult = (FindMerchantDetailsResult) eventBean.getObject();
                    //dealNumber.set(findMerchantDetailsResult.getData().formatRangeTimeOrder());
                    initViewDate(findMerchantDetailsResult);
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //广告列表
            case EvKey.advertiseFind:
                if (eventBean.isStatue()) {
                    onRequestListener.onSuccess(eventBean.getObject());
                } else {
                    onRequestListener.onFail(eventBean.getMessage());
                }
                break;
            //otc钱包查询业务处理
            case EvKey.coinWalletOtcAvailable:
                dismissDialog();
                if (!eventBean.getMessage().equals(coinName)) return;
                if (eventBean.isStatue()) {
                    onRequestListenerWallet.onSuccess(eventBean.getObject());
                } else {
                    onRequestListenerWallet.onFail(eventBean.getMessage());
                }
                break;
            case EvKey.logout_success_401:
                AdvertiseScanClient.getInstance().findMerchantDetails(memberId);
                AdvertiseScanClient.getInstance().getAdvertiseFindUrl(memberId);
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
