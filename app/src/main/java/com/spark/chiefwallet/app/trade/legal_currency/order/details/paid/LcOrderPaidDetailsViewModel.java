package com.spark.chiefwallet.app.trade.legal_currency.order.details.paid;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.api.pojo.PayTypeBean;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.TradePwdPopup;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.AppUtils;
import com.spark.otcclient.AdvertiseScanClient;
import com.spark.otcclient.LcTradeClient;
import com.spark.otcclient.pojo.FindMerchantDetailsResult;
import com.spark.otcclient.pojo.LcOrderResult;
import com.spark.otcclient.pojo.OrderDetailsResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.DateUtils;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.StringUtils;
import me.spark.mvvm.utils.Utils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/12
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LcOrderPaidDetailsViewModel extends BaseViewModel {
    public LcOrderPaidDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> titleRightTV = new ObservableField<>();
    public ObservableField<String> btnTV = new ObservableField<>();
    public ObservableField<String> money = new ObservableField<>();
    public ObservableField<String> money2 = new ObservableField<>();
    public ObservableField<String> nameShort = new ObservableField<>();
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> price = new ObservableField<>();
    public ObservableField<String> number = new ObservableField<>();
    public ObservableField<String> createTime = new ObservableField<>();
    public ObservableField<String> orderSn = new ObservableField<>();
    public ObservableField<String> referenceSn = new ObservableField<>();

    public ObservableField<String> typeTips = new ObservableField<>();
    public ObservableField<String> tradeUserTips = new ObservableField<>();

    public ObservableField<Boolean> isBuyOrSell = new ObservableField<>(true);
    public ObservableField<Boolean> isWeChatPay = new ObservableField<>(false);
    public ObservableField<Boolean> isAliPay = new ObservableField<>(false);
    public ObservableField<Boolean> isBankPay = new ObservableField<>(false);
    public ObservableField<String> time_tips_1 = new ObservableField<>();
    public ObservableField<String> orderEnd6 = new ObservableField<>();
    public ObservableField<String> orderBtn1 = new ObservableField<>();
    public ObservableField<String> orderBtn2 = new ObservableField<>();
    public ObservableField<String> aliPayAddr = new ObservableField<>();
    public ObservableField<String> weChatAddr = new ObservableField<>();
    public ObservableField<String> weChatName = new ObservableField<>();
    public ObservableField<String> aliPayName = new ObservableField<>();
    public ObservableField<String> bankPayAddr = new ObservableField<>();
    public ObservableField<String> bankPayName = new ObservableField<>();
    public ObservableField<String> bankPayOpenBank = new ObservableField<>();
    public ObservableField<String> bankPayBranch = new ObservableField<>();
    public ObservableField<String> dealNumber = new ObservableField<>();

    private String weChatCodeUrl, aliPayCodeUrl;
    private PayTypeBean mPayTypeBean;
    private LcOrderResult.DataBean.RecordsBean mRecordsBean;
    private OnRequestListener mOnRequestListener;
    private Context mContext;
    private int tick_flush = 5;//每隔tick_flush秒钟加载一次数据
    private boolean isGoToNext = false;//是否跳转到下一界面
    private OrderDetailsResult orderDetailsResult;//订单详情

    public BindingCommand orderSnOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            AppUtils.copy2Clipboard(Utils.getContext(), orderSn.get());
        }
    });
    public BindingCommand referenceSnOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            AppUtils.copy2Clipboard(Utils.getContext(), referenceSn.get());
        }
    });

    public BindingCommand finishClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            stopFlush();
            finish();
        }
    });

    public BindingCommand titleRightOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            switch (mRecordsBean.getOrderType()) {
                //取消订单
                case "0":
                    new XPopup.Builder(mContext)
                            .asConfirm(mContext.getString(R.string.tips), mContext.getString(R.string.str_confirm_cancel),
                                    mContext.getString(R.string.cancel), mContext.getString(R.string.ensure),
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            LcTradeClient.getInstance().orderCancel(mRecordsBean.getOrderSn());
                                        }
                                    }, null, false)
                            .show();
                    break;
                //订单申诉
                case "1":
                    toAppeal();
                    break;
            }
        }
    });

    public BindingCommand submitOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            switch (mRecordsBean.getOrderType()) {
                //订单申诉
                case "0":
                    toAppeal();
                    break;
                //确认放行
                case "1":
                    new XPopup.Builder(mContext)
                            .autoOpenSoftInput(true)
                            .asCustom(new TradePwdPopup(mContext, new OnEtContentListener() {
                                @Override
                                public void onCEtContentInput(String content) {
                                    LcTradeClient.getInstance().orderRelease(mRecordsBean.getOrderSn(), content);
                                }
                            }))
                            .show();
                    break;
            }
        }
    });

    /**
     * 去申诉
     */
    private void toAppeal() {
        if (orderDetailsResult != null) {
            if (StringUtils.isNotEmpty(orderDetailsResult.getData().getPayTime())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                Date payTime = new Date(Long.valueOf(orderDetailsResult.getData().getPayTime()));

                long timeCountDown = System.currentTimeMillis() - payTime.getTime() - 30 * 60 * 1000;
                if (timeCountDown >= 0) {
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_TRADE_LC_ORCER_APPEAL)
                            .withParcelable("orderDetails", mRecordsBean)
                            .navigation();
                } else {
                    Toasty.showError(App.getInstance().getString(R.string.str_appeal_time));
                }
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                Date createTime = new Date(Long.valueOf(orderDetailsResult.getData().getCreateTime()));

                long timeCountDown = System.currentTimeMillis() - createTime.getTime() - 30 * 60 * 1000;
                if (timeCountDown >= 0) {
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_TRADE_LC_ORCER_APPEAL)
                            .withParcelable("orderDetails", mRecordsBean)
                            .navigation();
                } else {
                    Toasty.showError(App.getInstance().getString(R.string.str_appeal_time));
                }
            }

        }
    }

    public BindingCommand chatOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance()
                    .build(ARouterPath.ACTIVITY_TRADE_LC_CHAT)
                    .withParcelable("orderDetails", mRecordsBean)
                    .navigation();
        }
    });

    public BindingCommand alipayOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            AppUtils.copy2Clipboard(Utils.getContext(), aliPayAddr.get());
        }
    });

    public BindingCommand weChatPayOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            AppUtils.copy2Clipboard(Utils.getContext(), weChatAddr.get());
        }
    });

    public BindingCommand cardOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            AppUtils.copy2Clipboard(Utils.getContext(), bankPayAddr.get());
        }
    });

    public BindingCommand weChatOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_TRADE_LC_ORCER_RECEIPT_CODE)
                    .withString("codeUrl", weChatCodeUrl)
                    .navigation();
        }
    });

    public BindingCommand alPayOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_TRADE_LC_ORCER_RECEIPT_CODE)
                    .withString("codeUrl", aliPayCodeUrl)
                    .navigation();
        }
    });

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> isRefresh = new SingleLiveEvent<>();
    }

    public void initContext(Context context) {
        this.mContext = context;
    }

    public void initViewDate(LcOrderResult.DataBean.RecordsBean orderDetailsBean, OnRequestListener onRequestListener) {
        LogUtils.e("orderDetailsBean", orderDetailsBean.toString());
        this.mRecordsBean = orderDetailsBean;
        this.mOnRequestListener = onRequestListener;
        LcTradeClient.getInstance().orderDetails(true, orderDetailsBean.getOrderSn());
        handler_timeCurrent.sendEmptyMessageDelayed(1, tick_flush * 1000);//tick_flush秒钟刷新一次
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            case EvKey.lcOrderTypeDetails:
                if (eventBean.isStatue()) {
                    orderDetailsResult = (OrderDetailsResult) eventBean.getObject();
                    //status订单状态 0-已取消 1-未付款 2-已付款 3-已完成 4-申诉中
                    int status = orderDetailsResult.getData().getStatus();
                    if (status == 2) {
                        initDate(orderDetailsResult);
                    } else {
                        if (!isGoToNext) {
                            isGoToNext = true;
                            mRecordsBean.setStatus(status);
                            ARouter.getInstance()
                                    .build(ARouterPath.ACTIVITY_TRADE_LC_DETAILS_ORDER)
                                    .withParcelable("orderDetails", mRecordsBean)
                                    .navigation();
                        }

                        stopFlush();
                        finish();
                    }
                } else {
                    if (eventBean.getCode() == 30540) {
                        if (!isGoToNext) {
                            isGoToNext = true;
                            ARouter.getInstance()
                                    .build(ARouterPath.ACTIVITY_TRADE_LC_DETAILS_ORDER)
                                    .withParcelable("orderDetails", mRecordsBean)
                                    .navigation();
                        }

                        stopFlush();
                        finish();
                    } else {
                        mOnRequestListener.onFail(eventBean.getMessage());
                    }
                }
                break;
            case EvKey.lcOrderCancel:
                if (eventBean.isStatue()) {
                    Toasty.showSuccess(App.getInstance().getApplicationContext().getString(R.string.str_cancel_success));
                    stopFlush();
                    finish();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            case EvKey.lcOrderRelease:
                if (eventBean.isStatue()) {
                    Toasty.showSuccess(App.getInstance().getApplicationContext().getString(R.string.str_release_success));
                    stopFlush();
                    finish();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            case EvKey.lcOrderAppeal:
                dismissDialog();
                if (eventBean.isStatue()) {
                    stopFlush();
                    finish();
                }
                break;
            case EvKey.findMerchantDetails:
                if (eventBean.isStatue()) {
                    FindMerchantDetailsResult findMerchantDetailsResult = (FindMerchantDetailsResult) eventBean.getObject();
                    dealNumber.set(findMerchantDetailsResult.getData().formatRangeTimeOrder());
                } else {
                    Toasty.showError(eventBean.getMessage());
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

    private Handler handler_timeCurrent = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    startFlush();
                    handler_timeCurrent.sendEmptyMessageDelayed(1, tick_flush * 1000);//tick_flush秒钟刷新一次
                    break;
            }

        }
    };

    /**
     * 开始刷新
     */
    private void startFlush() {
        stopFlush();
        LogUtils.e("开始刷新=============已付款");
        LcTradeClient.getInstance().orderDetails(true, mRecordsBean.getOrderSn());
    }

    /**
     * 停止刷新
     */
    private void stopFlush() {
        handler_timeCurrent.removeCallbacksAndMessages(null);
    }

    private void initDate(OrderDetailsResult orderDetailsResult) {
        title.set(mRecordsBean.getOrderType().equals("0") ? App.getInstance().getString(R.string.str_paid) : App.getInstance().getString(R.string.str_release_please));
        time_tips_1.set(mRecordsBean.getOrderType().equals("0") ? "" : App.getInstance().getString(R.string.str_find_pay_info));
        titleRightTV.set(mRecordsBean.getOrderType().equals("0") ? App.getInstance().getString(R.string.str_cancel_order) : App.getInstance().getString(R.string.str_order_appeal));
        isBuyOrSell.set(mRecordsBean.getOrderType().equals("0"));
        btnTV.set(mRecordsBean.getOrderType().equals("0") ? App.getInstance().getString(R.string.str_order_appeal) : App.getInstance().getString(R.string.str_confirm_release));
        typeTips.set(mRecordsBean.getOrderType().equals("0") ? App.getInstance().getString(R.string.str_wait_release) : App.getInstance().getString(R.string.str_check_release));
        tradeUserTips.set(mRecordsBean.getOrderType().equals("0") ? App.getInstance().getString(R.string.str_seller_deal) : App.getInstance().getString(R.string.str_buyer_paid));

        mPayTypeBean = App.gson.fromJson("{\"payTypeBean\":" + orderDetailsResult.getData().getPayData() + "}", PayTypeBean.class);

        if (mPayTypeBean != null) {
            for (PayTypeBean.PayTypeBeanBean payTypeBeanBean : mPayTypeBean.getPayTypeBean()) {
                String actualPayment = orderDetailsResult.getData().getActualPayment();
                if (StringUtils.isNotEmpty(actualPayment)) {
                    if (actualPayment.contains(Constant.wechat)) {
                        isWeChatPay.set(true);
                        weChatCodeUrl = payTypeBeanBean.getQrCodeUrl();
                        weChatAddr.set(payTypeBeanBean.getPayAddress());
                        weChatName.set(payTypeBeanBean.getRealName());
                    } else if (actualPayment.contains(Constant.alipay)) {
                        isAliPay.set(true);
                        aliPayCodeUrl = payTypeBeanBean.getQrCodeUrl();
                        aliPayAddr.set(payTypeBeanBean.getPayAddress());
                        aliPayName.set(payTypeBeanBean.getRealName());
                    } else if (actualPayment.contains(Constant.card)) {
                        isBankPay.set(true);
                        bankPayAddr.set(payTypeBeanBean.getPayAddress());
                        bankPayName.set(payTypeBeanBean.getRealName());
                        bankPayOpenBank.set(payTypeBeanBean.getBank());
                        bankPayBranch.set(payTypeBeanBean.getBranch());
                    }
                } else {
                    isWeChatPay.set(false);
                    isAliPay.set(false);
                    isBankPay.set(false);
                }
            }
        }

        money.set("￥ " + DfUtils.formatNum(String.valueOf(mRecordsBean.getMoney())));
        CharSequence text = new SpanUtils()
                .append(App.getInstance().getString(R.string.str_single_price))
                .append(mRecordsBean.getPrice() + " CNY").setForegroundColor(ContextCompat.getColor(mContext, R.color.black))
                .create();
        money2.set(text.toString());
        //money2.set(DfUtils.numberFormat(mRecordsBean.getMoney(), mRecordsBean.getMoney() == 0 ? 0 : 8) + " CNY");
        nameShort.set(mRecordsBean.getTradeToUsername().substring(0, 1));
        name.set(mRecordsBean.getTradeToUsername());
        price.set(DfUtils.numberFormat(mRecordsBean.getPrice(), mRecordsBean.getPrice() == 0 ? 0 : 8) + " CNY");
        CharSequence numberText = new SpanUtils()
                .append(App.getInstance().getString(R.string.str_number))
                .append(mRecordsBean.getNumber() + " " + mRecordsBean.getCoinName()).setForegroundColor(ContextCompat.getColor(mContext, R.color.black))
                .create();
        number.set(numberText.toString());
        //number.set(DfUtils.numberFormat(mRecordsBean.getNumber(), mRecordsBean.getNumber() == 0 ? 0 : 8) + " " + mRecordsBean.getCoinName());
        createTime.set(DateUtils.formatDate("yyyy.MM.dd HH:mm", mRecordsBean.getCreateTime()));
        orderSn.set(mRecordsBean.getOrderSn());
        referenceSn.set(mRecordsBean.getPayRefer());
        mOnRequestListener.onSuccess(orderDetailsResult);
        AdvertiseScanClient.getInstance().findMerchantDetails(mRecordsBean.getMemberId());
        orderEnd6.set(mRecordsBean.getOrderSn().length() > 6 ? mRecordsBean.getOrderSn().substring(mRecordsBean.getOrderSn().length() - 6) : mRecordsBean.getOrderSn());

        orderBtn1.set(mRecordsBean.getOrderType().equals("0") ? App.getInstance().getString(R.string.str_cancel_order) : App.getInstance().getString(R.string.str_appeal));
        orderBtn2.set(mRecordsBean.getOrderType().equals("0") ? App.getInstance().getString(R.string.str_appeal) : App.getInstance().getString(R.string.str_release) + mRecordsBean.getCoinName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFlush();
        EventBusUtils.unRegister(this);
    }
}
