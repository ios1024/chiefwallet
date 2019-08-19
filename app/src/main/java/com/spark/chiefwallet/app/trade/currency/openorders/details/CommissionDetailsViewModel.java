package com.spark.chiefwallet.app.trade.currency.openorders.details;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.modulespot.SpotCoinClient;
import com.spark.modulespot.pojo.OpenOrderDetailsResult;
import com.spark.modulespot.pojo.OpenOrdersResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.MathUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/4
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CommissionDetailsViewModel extends BaseViewModel {
    public CommissionDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    private OnRequestListener mOnRequestListener;

    public ObservableField<String> dealAllAccount = new ObservableField<>();
    public ObservableField<String> dealType = new ObservableField<>();
    public ObservableField<Boolean> isBuyType = new ObservableField<>();
    public ObservableField<String> dealEvaluationPrice = new ObservableField<>();
    public ObservableField<String> dealNum = new ObservableField<>();
    public ObservableField<String> coinSymbol = new ObservableField<>();
    public ObservableField<String> baseSymbol = new ObservableField<>();
    public ObservableField<String> initDeal = new ObservableField<>();
    public ObservableField<String> dealNumVolume = new ObservableField<>();
    public ObservableField<String> transactionPrice = new ObservableField<>();
    public ObservableField<String> entrust = new ObservableField<>();
    public ObservableField<String> entrustVolume = new ObservableField<>();
    public ObservableField<String> initStatueType = new ObservableField<>();

    private OpenOrdersResult.DataBean.ListBean mCommissionDetailsBean;
    private OpenOrderDetailsResult mOpenOrderDetailsResult;

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> isRefresh = new SingleLiveEvent<>();
    }

    public void initDetails(OpenOrdersResult.DataBean.ListBean commissionDetailsBean, OnRequestListener onRequestListener) {
        this.mCommissionDetailsBean = commissionDetailsBean;
        this.mOnRequestListener = onRequestListener;
        SpotCoinClient.getInstance().openOrderDetails(commissionDetailsBean.getOrderId());

        dealType.set(commissionDetailsBean.getSide() == 0 ? App.getInstance().getString(R.string.buy) : App.getInstance().getString(R.string.sell));
        isBuyType.set(commissionDetailsBean.getSide() == 0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //历史委托成交明细
            case EvKey.openOrderDetails:
                if (eventBean.isStatue()) {
                    mOpenOrderDetailsResult = (OpenOrderDetailsResult) eventBean.getObject();
                    initDate();
                } else {
                    mOnRequestListener.onFail(eventBean.getMessage());
                }
                break;
            case EvKey.logout_success_401:
                if (eventBean.isStatue()) {
                    uc.isRefresh.setValue(true);
                }
                break;
            default:
                break;
        }
    }

    private void initDate() {
        double dealPrice = 0, dealAmount = 0;
        for (OpenOrderDetailsResult.DataBean dataBean : mOpenOrderDetailsResult.getData()) {
            dealPrice += Double.valueOf(dataBean.getPrice());
            dealAmount += Double.valueOf(dataBean.getAmount());
        }
        dealEvaluationPrice.set(DfUtils.numberFormat(dealPrice / mOpenOrderDetailsResult.getData().size(), 4));
        initDeal.set(App.getInstance().getString(R.string.total_turnover) + "(" + mCommissionDetailsBean.getBaseSymbol() + ")");
        coinSymbol.set(mCommissionDetailsBean.getCoinSymbol());
        baseSymbol.set(mCommissionDetailsBean.getBaseSymbol());
        dealNum.set(DfUtils.numberFormat(dealAmount, 8));
        dealNumVolume.set(App.getInstance().getString(R.string.transaction_volume2) + "(" + mCommissionDetailsBean.getSymbol().split("/")[0] + ")");
        transactionPrice.set(App.getInstance().getString(R.string.average_transaction_price) + "(" + mCommissionDetailsBean.getBaseSymbol() + ")");
//        dealAllAccount.set(DfUtils.numberFormat((dealPrice / mOpenOrderDetailsResult.getData().size()) * dealAmount, 4) + " " + mCommissionDetailsBean.getSymbol().split("/")[1]);
        dealAllAccount.set(DfUtils.numberFormat((dealPrice / mOpenOrderDetailsResult.getData().size()) * dealAmount, 4));
        entrustVolume.set(App.getInstance().getString(R.string.entrusted_quantity) + "(" + mCommissionDetailsBean.getCoinSymbol() + ")");
        if (mCommissionDetailsBean.getPriceType() == 0 && mCommissionDetailsBean.getSide() == 0) {
            entrust.set("--");
        } else {
            entrust.set(MathUtils.getRundNumber(mCommissionDetailsBean.getOrderQty(), 4, null));
        }
        if (mCommissionDetailsBean.getStatus() == 5) {
            initStatueType.set(App.getInstance().getString(R.string.in_the_bill_of_lading));
        }


        mOnRequestListener.onSuccess(mOpenOrderDetailsResult);
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
