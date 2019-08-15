package com.spark.chiefwallet.app.emex;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.spark.acclient.CaptchaAcClient;
import com.spark.acclient.FinanceClient;
import com.spark.acclient.pojo.CoinAddressBean;
import com.spark.acclient.pojo.CoinExtractBean;
import com.spark.acclient.pojo.CoinExtractSubmitBean;
import com.spark.acclient.pojo.CoinSupportBean;
import com.spark.acclient.pojo.CoinWithdrawAddressResult;
import com.spark.acclient.pojo.MerberWalletResult;
import com.spark.acclient.pojo.SpotWalletResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.api.AppClient;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.CoinChoosePopup;
import com.spark.chiefwallet.ui.popup.CoinExtractPopup;
import com.spark.chiefwallet.ui.popup.CoinExtractSubmitPopup;
import com.spark.chiefwallet.ui.popup.CoinRechargePopup;
import com.spark.chiefwallet.ui.popup.CoinTransPopup;
import com.spark.chiefwallet.ui.popup.CoinWithdrawAddressPopup;
import com.spark.chiefwallet.ui.popup.SmsVerifyPopup;
import com.spark.chiefwallet.ui.popup.impl.OnCoinChooseListener;
import com.spark.chiefwallet.ui.popup.impl.OnCoinExtractListener;
import com.spark.chiefwallet.ui.popup.impl.OnCoinExtractSubmitListener;
import com.spark.chiefwallet.ui.popup.impl.OnCoinRechargeListener;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.modulespot.pojo.AllThumbResult;
import com.spark.otcclient.AdvertiseScanClient;
import com.spark.otcclient.pojo.LcCoinListResult;
import com.spark.wsclient.base.WsCMD;
import com.spark.wsclient.pojo.B2BThumbBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.SPUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.StringUtils;
import me.spark.mvvm.utils.WebSocketResponse;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/18
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class EmexViewModel extends BaseViewModel {
    public EmexViewModel(@NonNull Application application) {
        super(application);
    }

    public ObservableField<CharSequence> usdtAll = new ObservableField<CharSequence>();
    public ObservableField<String> usdtAllTrans = new ObservableField<>();
    public ObservableField<String> spotAcconut = new ObservableField<>();
    public ObservableField<String> spotAcconutTrans = new ObservableField<>();
    public ObservableField<String> otcAcconut = new ObservableField<>();
    public ObservableField<String> otcAcconutTrans = new ObservableField<>();
    public ObservableField<String> cfdAcconut = new ObservableField<>();
    public ObservableField<String> cfdAcconutTrans = new ObservableField<>();

    private int getCoinSupportType = 0;           //0 - 充币  1 - 提币   2 - 划转
    private String usdtAllText = "------ USDT";
    private String usdtAllTransText = "≈ ---- CNY " + App.getInstance().getString(R.string.total_assets_convert);
    private String spotAcconutText = "------ USDT";
    private String spotAcconutTransText = "≈ ---- CNY";
    private String otcAcconutText = "------ USDT";
    private String otcAcconutTransText = "≈ ---- CNY";
    private String cfdAcconutText = "------ USDT";
    private String cfdAcconutTransText = "≈ ---- CNY";

    private CoinSupportBean.DataBean mCoinSupportBean;
    private CoinExtractPopup mCoinExtractPopup;
    private CoinExtractBean mCoinExtractBean;
    private CoinTransPopup mCoinTransPopup;
    private CoinSupportBean mSpotTransSupportBean;
    private double spotWalletTotal = 0, spotWalletTrans = 0, otcWalletTotal = 0, otcWalletTrans = 0, cfdWalletTotal = 0, cfdWalletTrans = 0;
    private List<AllThumbResult.DataBean> mThumbList = new ArrayList<>();


    private OnRequestListener onRequestListener, onRequestListenerAnnounce;
    private Context mContext;
    private boolean isLoadAcountDate = false;
    public boolean isVisible2User = false;
    public boolean isOnPause = true;
    public boolean isFirstHttpLoadSuccess = false;

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<List<AllThumbResult.DataBean>> mAllThumbResultSingleLiveEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> isHideAccountSwitchEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> isRefresh = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> loginStatue = new SingleLiveEvent<>();
        //http
        public SingleLiveEvent<AllThumbResult> httpBean = new SingleLiveEvent<>();
        //ws
        public SingleLiveEvent<B2BThumbBean> wsBean = new SingleLiveEvent<>();
    }

    //查看完整行情
    public BindingCommand goQuotes = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            EventBusUtils.postJumpEvent(EvKey.goQoutes);
        }
    });

    //充币
    public BindingCommand coinInCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (!App.getInstance().isAppLogin()) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                        .navigation();
                return;
            }
            getCoinSupportType = 0;
            getCoinSupport();
        }
    });

    //提币
    public BindingCommand coinOutCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (!App.getInstance().isAppLogin()) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                        .navigation();
                return;
            }
            getCoinSupportType = 1;
            getCoinSupport();
        }
    });

    //划转
    public BindingCommand coinTransCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (!App.getInstance().isAppLogin()) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                        .navigation();
                return;
            }
            getCoinSupportType = 2;
            getCoinSupport();
        }
    });

    //资金详情的展示与隐藏
    public BindingCommand hideAccountOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (StringUtils.isEmpty(usdtAllTrans.get())) return;
            if (!App.getInstance().isAppLogin()) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                        .navigation();
            } else {
                uc.isHideAccountSwitchEvent.setValue(uc.isHideAccountSwitchEvent.getValue() == null || !uc.isHideAccountSwitchEvent.getValue());
            }
        }
    });

    /**
     * 请求banner数据
     */
    public void loadBanner(Context context, OnRequestListener onRequestListener) {
        this.mContext = context;
        this.onRequestListener = onRequestListener;
        AppClient.getInstance().getBanner();
    }

    public void loadAnnounce(Context context, OnRequestListener onRequestListener) {
        this.mContext = context;
        this.onRequestListenerAnnounce = onRequestListener;
        AppClient.getInstance().getAnnounce();
    }

    public void initText() {
        if (!App.getInstance().isAppLogin()) {
            usdtAll.set("------ USDT");
            usdtAllTrans.set("≈ ---- CNY " + App.getInstance().getString(R.string.total_assets_convert));
            spotAcconut.set("------ USDT");
            spotAcconutTrans.set("≈ ---- CNY");
            otcAcconut.set("------ USDT");
            otcAcconutTrans.set("≈ ---- CNY");
//            cfdAcconut.set("------ USDT");
//            cfdAcconutTrans.set("≈ ---- CNY");
        } else {
            isLoadAcountDate = true;
            if (SPUtils.getInstance().isHideAccount()) {
                usdtAll.set("****** USDT");
                usdtAllTrans.set("≈ **** CNY " + App.getInstance().getString(R.string.total_assets_convert));
                spotAcconut.set("****** USDT USDT");
                spotAcconutTrans.set("≈ **** CNY");
                otcAcconut.set("****** USDT USDT");
                otcAcconutTrans.set("≈ **** CNY");
//                cfdAcconut.set("****** USDT USDT");
//                cfdAcconutTrans.set("≈ **** CNY");
            } else {
                usdtAll.set(initWalletTotal(Double.valueOf(usdtAllText)));
                usdtAllTrans.set(transWalletTotal(Double.valueOf(usdtAllTransText)));
                spotAcconut.set(initAccount(Double.valueOf(spotAcconutText)));
                spotAcconutTrans.set(initAccountTrans(Double.valueOf(spotAcconutTransText)));
                otcAcconut.set(initAccount(Double.valueOf(otcAcconutText)));
                otcAcconutTrans.set(initAccountTrans(Double.valueOf(otcAcconutTransText)));
//                cfdAcconut.set(initAccount(Double.valueOf(cfdAcconutText)));
//                cfdAcconutTrans.set(initAccountTrans(Double.valueOf(cfdAcconutTransText)));
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        //事件拦截
        if ((!isVisible2User || isOnPause) && (!eventBean.getOrigin().equals(EvKey.loginStatue))
                && (!eventBean.getOrigin().equals(EvKey.banner))
                && (!eventBean.getOrigin().equals(EvKey.announce))
                && (!eventBean.getOrigin().equals(EvKey.coinWallet)))
            return;
        switch (eventBean.getOrigin()) {
            case EvKey.banner:
                if (eventBean.isStatue()) {
                    onRequestListener.onSuccess(eventBean.getObject());
                    if (!App.getInstance().isAppLogin()) {
                        initText();
                    } else {
                        updateAccount();
                    }
                } else {
                    onRequestListener.onFail(eventBean.getMessage());
                }
                break;
            case EvKey.announce:
                if (eventBean.isStatue()) {
                    onRequestListenerAnnounce.onSuccess(eventBean.getObject());
                } else {
                    onRequestListenerAnnounce.onFail(eventBean.getMessage());
                }
                break;
            //spot钱包查询业务处理
            case EvKey.coinWallet:
                if (eventBean.getType() != 0 && eventBean.getType() != 1 && eventBean.getType() != 2)
                    return;
                if (eventBean.isStatue()) {
                    if (eventBean.getType() == 0) {
                        updateSpotInfo((SpotWalletResult) eventBean.getObject());
                        FinanceClient.getInstance().getCoinWallet("OTC");
                    } else if (eventBean.getType() == 1) {
                        updateOtcInfo((SpotWalletResult) eventBean.getObject());
//                        FinanceClient.getInstance().getCoinWallet("CFD");
                    }
//                    else if (eventBean.getType() == 2) {
//                        updateCfdInfo((SpotWalletResult) eventBean.getObject());
//                    }
                }
                break;
            //成交全币种缩略图
            case EvKey.klineThumbAll:
                synchronized (this) {
                    if (eventBean.isStatue()) {
                        if (Constant.isHttpAndWs) {
                            if (!TextUtils.isEmpty(Constant.b2bKlinePushJson)) {
                                Constant.b2bKlinePushJson = App.gson.toJson(eventBean.getObject());
                            }
                            uc.httpBean.setValue((AllThumbResult) eventBean.getObject());
                        } else {
                            mThumbList.clear();
                            if (App.getInstance().isAppLogin()) {
                                for (String favor : SPUtils.getInstance().getFavorFindList()) {
                                    for (AllThumbResult.DataBean dataBean : ((AllThumbResult) eventBean.getObject()).getData()) {
                                        if (dataBean.getSymbol().equals(favor)) {
                                            mThumbList.add(dataBean);
                                        }
                                    }
                                }
                            }
                            uc.mAllThumbResultSingleLiveEvent.setValue(mThumbList);
                        }
                    }
                }
                break;
            //退出登录后隐藏自选
            case EvKey.loginStatue:
                if (eventBean.isStatue()) {
                    if (!App.getInstance().isAppLogin()) {
                        mThumbList.clear();
                        uc.mAllThumbResultSingleLiveEvent.setValue(mThumbList);
                        initText();
                    } else {
                        if (!isLoadAcountDate) {
                            updateAccount();
                        } else {
                            initText();
                        }
                    }
                    if (Constant.isHttpAndWs) {
                        uc.loginStatue.setValue(App.getInstance().isAppLogin());
                    }
                }
                break;
            //查询平台支持到币种信息
            case EvKey.coinSupport:
                dismissDialog();
                if (eventBean.isStatue()) {
                    final CoinSupportBean coinSupportBean = (CoinSupportBean) eventBean.getObject();
                    if (getCoinSupportType == 0 || getCoinSupportType == 1) {
                        new XPopup.Builder(mContext)
                                .asCustom(new CoinChoosePopup(mContext, coinSupportBean.getData(), new OnCoinChooseListener() {
                                    @Override
                                    public void onClickOrder() {
                                        //记录
                                        switch (getCoinSupportType) {
                                            case 0:
                                                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_PROPERTY_RECORD)
                                                        .withInt("recordType", 0)
                                                        .navigation();
                                                break;
                                            case 1:
                                                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_PROPERTY_RECORD)
                                                        .withInt("recordType", 1)
                                                        .navigation();
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onClickItem(int position) {
                                        mCoinSupportBean = coinSupportBean.getData().get(position);
                                        //item点击
                                        switch (getCoinSupportType) {
                                            case 0:
                                                getCoinInInfo(coinSupportBean.getData().get(position).getCoinName());
                                                break;
                                            case 1:
                                                getCoinOutInfo(coinSupportBean.getData().get(position).getCoinName());
                                                break;
                                        }
                                    }
                                }))
                                .show();
                    } else {
                        if (coinSupportBean.getData().isEmpty()) {
                            Toasty.showError(App.getInstance().getString(R.string.no_support_coin));
                            return;
                        }
                        mSpotTransSupportBean = coinSupportBean;
                        if (!Constant.lcCoinPairThumbBeanList.isEmpty()) {
                            mCoinTransPopup = new CoinTransPopup(mContext, 0, coinSupportBean);
                            new XPopup.Builder(mContext)
                                    .asCustom(mCoinTransPopup)
                                    .show();
                        } else {
                            AdvertiseScanClient.getInstance().getIndexTradeCoinList();
                        }
                    }
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //查询法币可用币种
            case EvKey.indexCoinList:
                if (eventBean.isStatue()) {
                    Constant.lcCoinPairThumbBeanList.clear();
                    for (LcCoinListResult.DataBean dataBean : ((LcCoinListResult) eventBean.getObject()).getData()) {
                        if (!Constant.lcCoinPairThumbBeanList.contains(dataBean.getCoinName()))
                            Constant.lcCoinPairThumbBeanList.add(dataBean.getCoinName());
                    }
                    mCoinTransPopup = new CoinTransPopup(mContext, 0, mSpotTransSupportBean);
                    new XPopup.Builder(mContext)
                            .asCustom(mCoinTransPopup)
                            .show();
                } else {
                    Toasty.showError(App.getInstance().getString(R.string.network_abnormal));
                }
                break;
            //查询指定币种的钱包地址
            case EvKey.coinAddress:
                dismissDialog();
                if (eventBean.isStatue()) {
                    CoinAddressBean coinAddressBean = (CoinAddressBean) eventBean.getObject();
                    new XPopup.Builder(mContext)
                            .asCustom(new CoinRechargePopup(mContext, coinAddressBean, mCoinSupportBean, new OnCoinRechargeListener() {
                                @Override
                                public void onClickOrder() {
                                    //记录
                                    // TODO: 2019/5/21
                                }

                                @Override
                                public void onClickChoose() {
                                    //选择币种
                                    getCoinSupport();
                                }
                            }))
                            .show();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //币币选定币种提币信息
            case EvKey.merberWallet:
                dismissDialog();
                if (eventBean.isStatue()) {
                    MerberWalletResult merberWalletResult = (MerberWalletResult) eventBean.getObject();
                    mCoinExtractPopup = new CoinExtractPopup(mContext, merberWalletResult, mCoinSupportBean, new OnCoinExtractListener() {
                        @Override
                        public void onReceiveCoinExtract(CoinExtractSubmitBean coinExtractSubmitBean) {
                            new XPopup.Builder(mContext)
                                    .asCustom(new CoinExtractSubmitPopup(mContext, coinExtractSubmitBean, new OnCoinExtractSubmitListener() {
                                        @Override
                                        public void onReceiveCoinExtractSubmit(final CoinExtractBean coinExtractBean) {
                                            mCoinExtractBean = coinExtractBean;
                                            showDialog(mContext.getString(R.string.loading));
                                            CaptchaAcClient.getInstance().phoneCaptcha(App.getInstance().getCurrentUser().getMobilePhone());
                                        }
                                    }))
                                    .show();
                        }
                    });
                    new XPopup.Builder(mContext)
                            .asCustom(mCoinExtractPopup)
                            .show();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //法币选定币种提币信息
            case EvKey.merberOtcWallet:
                dismissDialog();
                if (eventBean.isStatue()) {
                    MerberWalletResult merberWalletResult = (MerberWalletResult) eventBean.getObject();
                    mCoinTransPopup.updateTransNumAvailable(merberWalletResult);
                } else {
                    Toasty.showError(eventBean.getMessage());
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
            //查询用户的某个币种的提币地址信息
            case EvKey.coinWithdrawAddress:
                dismissDialog();
                if (eventBean.isStatue()) {
                    CoinWithdrawAddressResult coinWithdrawAddressResult = (CoinWithdrawAddressResult) eventBean.getObject();
                    new XPopup.Builder(mContext)
                            .asCustom(new CoinWithdrawAddressPopup(mContext, coinWithdrawAddressResult.getData(), new OnEtContentListener() {
                                @Override
                                public void onCEtContentInput(String content) {
                                    mCoinExtractPopup.updateAddress(content);
                                }
                            }))
                            .show();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //提币
            case EvKey.coinWithdrawRequest:
                dismissDialog();
                if (eventBean.isStatue()) {
                    Toasty.showSuccess(App.getInstance().getString(R.string.coin_out_success));
                    updateAccount();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //划转
            case EvKey.coinTransfer:
                if (eventBean.isStatue()) {
                    if (mCoinTransPopup != null && mCoinTransPopup.isShow()) {
                        mCoinTransPopup.dismiss();
                    }
                    Toasty.showSuccess(App.getInstance().getString(R.string.coin_trans_success));
                    updateAccount();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebSocketResponse webSocketResponse) {
        switch (webSocketResponse.getCmd()) {
            //缩略图推送
            case WsCMD.PUSH_THUMB:
                Constant.lastB2BKlinePushTime = System.currentTimeMillis();
                if (!isFirstHttpLoadSuccess) return;
                try {
                    String json = "{\"date\":" + webSocketResponse.getResponse() + "}";
                    B2BThumbBean b2BThumbBean = App.gson.fromJson(json, B2BThumbBean.class);
                    uc.wsBean.setValue(b2BThumbBean);
                } catch (Exception e) {
                    LogUtils.e("PUSH_THUMB", e.toString());
                }

                break;
            default:
                break;
        }
    }


    /**
     * 请求资产信息
     */
    private void updateAccount() {
        spotWalletTotal = 0;
        spotWalletTrans = 0;
        FinanceClient.getInstance().getCoinWallet("SPOT");
    }

    /**
     * 更新币币钱包信息
     *
     * @param spotWalletResult
     */
    private void updateSpotInfo(SpotWalletResult spotWalletResult) {
        if (onRequestListener == null) return;
        spotWalletTotal = 0;
        spotWalletTrans = 0;
        for (SpotWalletResult.DataBean dataBean : spotWalletResult.getData()) {
            spotWalletTotal = new BigDecimal(dataBean.getTotalPlatformAssetBalance()).add(new BigDecimal(spotWalletTotal)).doubleValue();
            spotWalletTrans = new BigDecimal(dataBean.getTotalLegalAssetBalance()).add(new BigDecimal(spotWalletTrans)).doubleValue();
        }
    }

    /**
     * 更新法币钱包信息
     *
     * @param spotWalletResult
     */
    private void updateOtcInfo(SpotWalletResult spotWalletResult) {
        if (onRequestListener == null) return;
        otcWalletTotal = 0;
        otcWalletTrans = 0;
        for (SpotWalletResult.DataBean dataBean : spotWalletResult.getData()) {
            otcWalletTotal = new BigDecimal(dataBean.getTotalPlatformAssetBalance()).add(new BigDecimal(otcWalletTotal)).doubleValue();
            otcWalletTrans = new BigDecimal(dataBean.getTotalLegalAssetBalance()).add(new BigDecimal(otcWalletTrans)).doubleValue();
        }

        usdtAllText = String.valueOf(spotWalletTotal + otcWalletTotal );
        usdtAllTransText = String.valueOf(spotWalletTrans + otcWalletTrans );
        spotAcconutText = String.valueOf(spotWalletTotal);
        spotAcconutTransText = String.valueOf(spotWalletTrans);
        otcAcconutText = String.valueOf(otcWalletTotal);
        otcAcconutTransText = String.valueOf(otcWalletTrans);
        initText();
    }

    /**
     * 更新钱包信息
     *
     * @param spotWalletResult
     */
    private void updateCfdInfo(SpotWalletResult spotWalletResult) {
        if (onRequestListener == null) return;
        cfdWalletTotal = 0;
        cfdWalletTrans = 0;
        for (SpotWalletResult.DataBean dataBean : spotWalletResult.getData()) {
            cfdWalletTotal = new BigDecimal(dataBean.getBalance() + dataBean.getFrozenBalance()).add(new BigDecimal(cfdWalletTotal)).doubleValue();
            cfdWalletTrans = new BigDecimal(dataBean.getTotalLegalAssetBalance()).add(new BigDecimal(cfdWalletTrans)).doubleValue();
        }

        usdtAllText = String.valueOf(spotWalletTotal + otcWalletTotal + cfdWalletTotal);
        usdtAllTransText = String.valueOf(spotWalletTrans + otcWalletTrans + cfdWalletTrans);
        spotAcconutText = String.valueOf(spotWalletTotal);
        spotAcconutTransText = String.valueOf(spotWalletTrans);
        otcAcconutText = String.valueOf(otcWalletTotal);
        otcAcconutTransText = String.valueOf(otcWalletTrans);
        cfdAcconutText = String.valueOf(cfdWalletTotal);
        cfdAcconutTransText = String.valueOf(cfdWalletTrans);
        initText();
    }

    /**
     * 钱包总额
     *
     * @param spotWalletTotal
     * @return
     */
    private CharSequence initWalletTotal(double spotWalletTotal) {
        String close = DfUtils.formatNum(MathUtils.getRundNumber(spotWalletTotal, 4, null));
        if (!close.contains(".")) return close;
        CharSequence text = new SpanUtils()
                .append(close.split("\\.")[0])
                .append("." + close.split("\\.")[1]).setFontSize(16, true)
                .append(" USDT").setFontSize(14, true)
                .create();
        return text;
    }

    /**
     * 查询平台支持到币种信息
     */
    public void getCoinSupport() {
        showDialog(mContext.getString(R.string.loading));
        FinanceClient.getInstance().getCoinSupport();
    }

    /**
     * 充币详情
     *
     * @param coinAddress
     */
    public void getCoinInInfo(String coinAddress) {
        showDialog(mContext.getString(R.string.loading));
        FinanceClient.getInstance().getCoinAddress(coinAddress);
    }


    /**
     * 提币详情
     *
     * @param coinAddress
     */
    public void getCoinOutInfo(String coinAddress) {
        showDialog(mContext.getString(R.string.loading));
        FinanceClient.getInstance().getCoinOutInfo(coinAddress);
    }


    //dataBinding------------------------------------------------------

    private String transWalletTotal(double spotWalletTotal) {
        String close = DfUtils.formatNum(MathUtils.getRundNumber(spotWalletTotal, 4, null));
        return "≈ " + close + " CNY " + App.getInstance().getString(R.string.total_assets_convert);
    }

    private String initAccount(double account) {
        String close = DfUtils.formatNum(MathUtils.getRundNumber(account, 4, null));
        return close + " USDT";
    }

    private String initAccountTrans(double accountTrans) {
        String close = DfUtils.formatNum(MathUtils.getRundNumber(accountTrans, 4, null));
        return "≈ " + close + " CNY";
    }

    //dataBinding------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        isVisible2User = true;
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
        isOnPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }
}
