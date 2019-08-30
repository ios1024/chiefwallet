package com.spark.chiefwallet.app.me;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.spark.acclient.FinanceClient;
import com.spark.acclient.pojo.SpotWalletResult;
import com.spark.casclient.CasClient;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.finance.property.withdrawmoney.WithdrawMoneyActivity;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.ModifyUserNamePopup;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.AppUtils;
import com.spark.chiefwallet.util.CheckErrorUtil;
import com.spark.otcclient.LcTradeClient;
import com.spark.otcclient.pojo.AuthMerchantResult;
import com.spark.ucclient.MemberClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;

import me.spark.mvvm.base.BaseRequestCode;
import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.pojo.User;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LanguageSPUtil;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.SPUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/18
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MeViewModel extends BaseViewModel {
    public MeViewModel(@NonNull Application application) {
        super(application);
    }

    private Context mContext;
    public boolean isVisible2User = false;
    public boolean isOnPause = true;
    private boolean isUpdateUserName = false;

    //登录状态
    public ObservableField<Boolean> isLogOut = new ObservableField<>(App.getInstance().isAppLogin());
    public ObservableField<String> languageSelect = new ObservableField<>("");
    public ObservableField<String> versionName = new ObservableField<>(getApplication().getApplicationContext().getString(R.string.version) + AppUtils.getContextVersionName());
    public ObservableField<String> qiehuanbizhong = new ObservableField<>("0");
    //昵称
    public ObservableField<String> nickname = new ObservableField<>(App.getInstance().isAppLogin() ?
            App.getInstance().getCurrentUser().getUsername() : App.getInstance().getString(R.string.no_login));
    //昵称
    public ObservableField<String> mobilePhone = new ObservableField<>(App.getInstance().isAppLogin() ?
            (App.getInstance().getCurrentUser().getLogintype() == 0 ? "UID:" + App.getInstance().getCurrentUser().getMobilePhone() : "UID:" + App.getInstance().getCurrentUser().getEmail()) : "UID:- -");
    //头像
    public ObservableField<String> avatarUser = new ObservableField<>(App.getInstance().isAppLogin() ?
            App.getInstance().getCurrentUser().getAvatar() : "");

    //
    public ObservableField<String> otcAcconut = new ObservableField<>("- - ");
    public ObservableField<String> otcAcconutTrans = new ObservableField<>("≈  - - ");
    private String otcAcconutText = "- - ";
    private String otcAcconutTransText = "≈  - - ";
    //    private OnRequestListener onRequestListener, onRequestListenerAnnounce;
    private double spotWalletTotal = 0, spotWalletTrans = 0, otcWalletTotal = 0, otcWalletTrans = 0, cfdWalletTotal = 0, cfdWalletTrans = 0;
    private String spotAcconutText = "- - ";
    private String spotAcconutTransText = "≈  - - ";
    private boolean isLoadAcountDate = false;

    //修改昵称
    public BindingCommand nickNameModifyOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (App.getInstance().isAppLogin()) {
                new XPopup.Builder(mContext)
                        .autoOpenSoftInput(true)
                        .asCustom(new ModifyUserNamePopup(mContext, new OnEtContentListener() {
                            @Override
                            public void onCEtContentInput(String content) {
                                showDialog(mContext.getString(R.string.loading));
                                MemberClient.getInstance().modifyNickName(content);
                            }
                        }))
                        .show();
            }
        }
    });

//    //退出登录
//    public BindingCommand logoutOnClickCommand = new BindingCommand(new BindingAction() {
//        @Override
//        public void call() {
//            if (App.getInstance().isAppLogin()) {
//                new XPopup.Builder(mContext)
//                        .asConfirm(mContext.getString(R.string.tips), mContext.getString(R.string.confirm_2_logout),
//                                mContext.getString(R.string.cancel), mContext.getString(R.string.ensure),
//                                new OnConfirmListener() {
//                                    @Override
//                                    public void onConfirm() {
//                                        showDialog(mContext.getString(R.string.loading));
//                                        CasClient.getInstance().logout(false);
//                                    }
//                                }, null, false)
//                        .show();
//            }
//        }
//    });

    //登录
    public BindingCommand loginOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (App.getInstance().isAppLogin()) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_MYMESSAGE)
                        .navigation();
            } else {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                        .navigation();
            }
        }
    });
    //我的资产
    public BindingCommand propertyOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_PROPERTY : ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        }
    });
    //财务记录
    public BindingCommand recordOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_RECORD : ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        }
    });
    //我的订单
    public BindingCommand orderOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_TRADE_CC_OPEN_ORDERS)
                    .withString("symbol", "")
                    .navigation();
        }
    });
    //我的广告
    public BindingCommand adOnClickCommand = new BindingCommand(new BindingAction() {

        @Override
        public void call() {
            showDialog();
            LcTradeClient.getInstance().authMerchantFind();

        }
    });

    //身份认证
    public BindingCommand authenticationOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_AUTHENTICATION : ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        }
    });
    //安全中心
    public BindingCommand safeCertreOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_SAFECENTRE : ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        }
    });

    //收款设置
    public BindingCommand receiptOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_RECEIPT : ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        }
    });
    //提币地址
    public BindingCommand coinAddressOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_COINADDRESS_COINADDRESS : ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        }
    });
    //Api接口
    public BindingCommand apiInterfaceOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_SAFECENTRE_APIINTERFACE)
                    .navigation();
        }
    });

    //计价方式
    public BindingCommand valueTypeOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {

        }
    });
    //语言
    public BindingCommand languageOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LANGUAGE)
                    .navigation();
        }
    });
    //设置
    public BindingCommand setupOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_ABOUSETUP)
                    .navigation();
        }
    });
    //关于
    public BindingCommand aboutOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_ABOUT)
                    .navigation();
        }
    }); //在线客服
    public BindingCommand customerserviceOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
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
            Unicorn.openServiceActivity(mContext, title, source);
        }
    });

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Integer> safeLevel = new SingleLiveEvent<>();
    }

    public void initContext(Context context) {
        mContext = context;
//        switch (LanguageSPUtil.getInstance(App.getInstance()).getSelectLanguage()) {
//            case 0:
//                languageSelect.set(App.getInstance().getString(R.string.simplified_chinese));
//                break;
//            case 1:
//                languageSelect.set(App.getInstance().getString(R.string.str_english));
//                break;
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        if (!isVisible2User && isOnPause && (!eventBean.getOrigin().equals(EvKey.loginStatue)))
            return;
        switch (eventBean.getOrigin()) {
            /**
             * 查看认证商家信息
             * certifiedBusinessStatus : 认证状态 0：未认证 1：认证-待审核 2：认证-审核成功 3：认证-审核失败 5：退保-待审核 6：退保-审核失败 7:退保-审核成功
             */
            case EvKey.authMerchantFind2:
                dismissDialog();
                if (eventBean.getCode() == 30548) {
                    new XPopup.Builder(mContext)
                            .dismissOnBackPressed(false)
                            .dismissOnTouchOutside(false)
                            .asConfirm(mContext.getString(R.string.tips), mContext.getString(R.string.prompt_for_certified_merchants),
                                    mContext.getString(R.string.cancel), mContext.getString(R.string.ensure),
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_CERTIFICATIONDETAILS : ARouterPath.ACTIVITY_ME_LOGIN)
                                                    .navigation();
                                        }
                                    }, null, false)

                            .show();

                }
                AuthMerchantResult authMerchantResult = (AuthMerchantResult) eventBean.getObject();
                int certifiedBusinessStatus = authMerchantResult.getData().getCertifiedBusinessStatus();

                if (certifiedBusinessStatus == 0) {
                    new XPopup.Builder(mContext)
                            .dismissOnBackPressed(false)
                            .dismissOnTouchOutside(false)
                            .asConfirm(mContext.getString(R.string.tips), mContext.getString(R.string.prompt_for_certified_merchants),
                                    mContext.getString(R.string.cancel), mContext.getString(R.string.ensure),
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_CERTIFICATIONDETAILS : ARouterPath.ACTIVITY_ME_LOGIN)
                                                    .navigation();
                                        }
                                    }, null, false)

                            .show();
                }
                if (certifiedBusinessStatus == 2 || certifiedBusinessStatus == 5 || certifiedBusinessStatus == 6) {
                    ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_AD : ARouterPath.ACTIVITY_ME_LOGIN)
                            .navigation();
                } else {
                    ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_CERTIFICATIONDETAILS : ARouterPath.ACTIVITY_ME_LOGIN)
                            .navigation();
                }
                break;
            //spot钱包查询业务处理
            case EvKey.coinWallet:
//                if (eventBean.getType() != 0 && eventBean.getType() != 1 && eventBean.getType() != 2)
//                    return;
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
            case EvKey.modifyUserName:
                if (eventBean.isStatue()) {
                    isUpdateUserName = true;
                    //刷新用户信息
                    MemberClient.getInstance().userInfo();

                } else {
                    dismissDialog();
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //刷新用户信息
            case EvKey.userInfo:
                dismissDialog();
                if (!isUpdateUserName) return;
                isUpdateUserName = false;
                if (eventBean.isStatue()) {
                    User user = (User) eventBean.getObject();
                    if (user != null) {
                        App.getInstance().setCurrentUser(user);
                    }
                    nickname.set(user.getUsername());
                    Toasty.showSuccess(App.getInstance().getApplicationContext().getString(R.string.successfully_modified));
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //监听登录状态
            case EvKey.loginStatue:
                if (eventBean.isStatue()) {
                    User curUser = App.getInstance().getCurrentUser();
                    isLogOut.set(App.getInstance().isAppLogin());
                    if (isLogOut.get()) {
                        updateAccount();
                        nickname.set(curUser.getUsername());
                        avatarUser.set(curUser.getAvatar());
                        mobilePhone.set(curUser.getLogintype() == 0 ? "UID:" + curUser.getMobilePhone() : "UID:" + curUser.getEmail());
                        initSafeLevel();
                    } else {
                        if (!isLoadAcountDate) {
                            updateAccount();
                        } else {
                            initText();
                        }
                        nickname.set(App.getInstance().getString(R.string.no_login));
                        avatarUser.set("");
                        mobilePhone.set("");
                    }
                }
                break;
//            //退出登录
//            case EvKey.logout:
//                dismissDialog();
//                if (eventBean.isStatue()) {
//                    App.getInstance().deleteCurrentUser();
//                    EventBusUtils.postSuccessEvent(EvKey.loginStatue, BaseRequestCode.OK, "");
//                    Toasty.showSuccess(App.getInstance().getString(R.string.logout_success));
//                } else {
//                    CheckErrorUtil.checkError(eventBean);
//                }
//                break;
            case EvKey.logout_success_401:
                if (eventBean.isStatue()) {
                    dismissDialog();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 请求资产信息
     */
    public void updateAccount() {
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
//        if (onRequestListener == null) return;
        spotWalletTotal = 0;
        spotWalletTrans = 0;
        for (SpotWalletResult.DataBean dataBean : spotWalletResult.getData()) {
            spotWalletTotal = new BigDecimal(dataBean.getTotalPlatformAssetBalance()).add(new BigDecimal(spotWalletTotal)).doubleValue();
            spotWalletTrans = new BigDecimal(dataBean.getCnyAssetBalance()).add(new BigDecimal(spotWalletTrans)).doubleValue();
        }
    }

    /**
     * 更新法币钱包信息
     *
     * @param spotWalletResult
     */
    private void updateOtcInfo(SpotWalletResult spotWalletResult) {
//        if (onRequestListener == null) return;
        otcWalletTotal = 0;
        otcWalletTrans = 0;
        for (SpotWalletResult.DataBean dataBean : spotWalletResult.getData()) {
            otcWalletTotal = new BigDecimal(dataBean.getTotalPlatformAssetBalance()).add(new BigDecimal(otcWalletTotal)).doubleValue();
            //1.人民币 CNY 2.美元 USDT 3.欧元 EUR 4.赛地 GHS 5.尼日利亚 NGN
            switch (SPUtils.getInstance().getPricingCurrency()) {
                case "1":
                    otcWalletTrans = new BigDecimal(dataBean.getCnyAssetBalance()).add(new BigDecimal(otcWalletTrans)).doubleValue();

                    break;
                case "2":
                    otcWalletTrans = new BigDecimal(dataBean.getUsdtAssetBalance()).add(new BigDecimal(otcWalletTrans)).doubleValue();

                    break;
                case "3":
                    otcWalletTrans = new BigDecimal(dataBean.getEurAssetBalance()).add(new BigDecimal(otcWalletTrans)).doubleValue();

                    break;
                case "4":
                    otcWalletTrans = new BigDecimal(dataBean.getGhsAssetBalance()).add(new BigDecimal(otcWalletTrans)).doubleValue();

                    break;
                case "5":
                    otcWalletTrans = new BigDecimal(dataBean.getNgnAssetBalance()).add(new BigDecimal(otcWalletTrans)).doubleValue();

                    break;
                default:
                    otcWalletTrans = new BigDecimal(dataBean.getCnyAssetBalance()).add(new BigDecimal(otcWalletTrans)).doubleValue();
                    break;
            }
        }

        spotAcconutText = String.valueOf(spotWalletTotal);
        spotAcconutTransText = String.valueOf(spotWalletTrans);
        otcAcconutText = String.valueOf(otcWalletTotal);
        otcAcconutTransText = String.valueOf(otcWalletTrans);
        initText();
    }

    public void initText() {

        if (!App.getInstance().isAppLogin()) {

            otcAcconut.set("- - ");
            otcAcconutTrans.set("≈  - - ");
//            cfdAcconut.set("------ USDT");
//            cfdAcconutTrans.set("≈ ---- CNY");
        } else {
            isLoadAcountDate = true;
//            if (SPUtils.getInstance().isHideAccount()) {
//                otcAcconut.set("****** USDT USDT");
//                otcAcconutTrans.set("≈ **** CNY");
//                cfdAcconut.set("****** USDT USDT");
//                cfdAcconutTrans.set("≈ **** CNY");
//            } else {
            if (qiehuanbizhong.get().equals("0")) {
                if (spotAcconutText.equals("- - ")) return;
                otcAcconut.set(initAccount(Double.valueOf(otcAcconutText)));
                otcAcconutTrans.set(initAccountTrans(Double.valueOf(otcAcconutTransText)));
            } else {
                if (spotAcconutText.equals("- - ")) return;
                otcAcconut.set(initAccount(Double.valueOf(spotAcconutText)));
                otcAcconutTrans.set(initAccountTrans(Double.valueOf(spotAcconutTransText)));
            }

//                cfdAcconut.set(initAccount(Double.valueOf(cfdAcconutText)));
//                cfdAcconutTrans.set(initAccountTrans(Double.valueOf(cfdAcconutTransText)));
//            }
        }
    }

    private String initAccount(double account) {
        String close = DfUtils.formatNum(MathUtils.getRundNumber(account, 4, null));
        return close;
    }

    private String initAccountTrans(double accountTrans) {
        String close = DfUtils.formatNum(MathUtils.getRundNumber(accountTrans, 4, null));
        //1.人民币 CNY 2.美元 USDT 3.欧元 EUR 4.赛地 GHS 5.尼日利亚 NGN
        if (SPUtils.getInstance().getPricingCurrency().equals("1")) {
            return "≈" + close + Constant.CNY;
        } else if (SPUtils.getInstance().getPricingCurrency().equals("2")) {
            return "≈" + close + Constant.USD;

        } else if (SPUtils.getInstance().getPricingCurrency().equals("3")) {
            return "≈" + close + Constant.EUR;

        } else if (SPUtils.getInstance().getPricingCurrency().equals("4")) {
            return "≈" + close + Constant.GHS;

        } else if (SPUtils.getInstance().getPricingCurrency().equals("5")) {
            return "≈" + close + Constant.NGN;
        } else
            return "≈" + close + Constant.CNY;

    }

    private void initSafeLevel() {
        int level = 2;
        if (App.getInstance().getCurrentUser().getEmail() != null) {
            level += 1;
        }
        if (App.getInstance().getCurrentUser().getGoogleAuthStatus() != 0) {
            level += 1;
        }
        if (App.getInstance().getCurrentUser().getFundsVerified() == 1) {
            level += 1;
        }
        uc.safeLevel.setValue(level);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isVisible2User = true;
        if (App.getInstance().isAppLogin()) {
            initSafeLevel();
        }
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
        updateAccount();
        isOnPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }
}
