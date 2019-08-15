package com.spark.chiefwallet.app.me;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.spark.casclient.CasClient;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.ModifyUserNamePopup;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.AppUtils;
import com.spark.chiefwallet.util.CheckErrorUtil;
import com.spark.ucclient.MemberClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseRequestCode;
import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.pojo.User;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LanguageSPUtil;

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
    //昵称
    public ObservableField<String> nickname = new ObservableField<>(App.getInstance().isAppLogin() ?
            App.getInstance().getCurrentUser().getUsername() : App.getInstance().getString(R.string.no_login));
    //昵称
    public ObservableField<String> mobilePhone = new ObservableField<>(App.getInstance().isAppLogin() ?
            (App.getInstance().getCurrentUser().getLogintype() == 0 ? App.getInstance().getCurrentUser().getMobilePhone() : App.getInstance().getCurrentUser().getEmail()) : "");

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

    //退出登录
    public BindingCommand logoutOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (App.getInstance().isAppLogin()) {
                new XPopup.Builder(mContext)
                        .asConfirm(mContext.getString(R.string.tips), mContext.getString(R.string.confirm_2_logout),
                                mContext.getString(R.string.cancel), mContext.getString(R.string.ensure),
                                new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        showDialog(mContext.getString(R.string.loading));
                                        CasClient.getInstance().logout(false);
                                    }
                                }, null, false)
                        .show();
            }
        }
    });

    //登录
    public BindingCommand loginOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (App.getInstance().isAppLogin()) {

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
            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_AD : ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
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
    //关于
    public BindingCommand aboutOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_ABOUT)
                    .navigation();
        }
    });

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Integer> safeLevel = new SingleLiveEvent<>();
    }

    public void initContext(Context context) {
        mContext = context;
        switch (LanguageSPUtil.getInstance(App.getInstance()).getSelectLanguage()) {
            case 0:
                languageSelect.set(App.getInstance().getString(R.string.simplified_chinese));
                break;
            case 1:
                languageSelect.set(App.getInstance().getString(R.string.str_english));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        if (!isVisible2User && isOnPause && (!eventBean.getOrigin().equals(EvKey.loginStatue)))
            return;
        switch (eventBean.getOrigin()) {
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
                        nickname.set(curUser.getUsername());
                        mobilePhone.set(curUser.getLogintype() == 0 ? curUser.getMobilePhone() : curUser.getEmail());
                        initSafeLevel();
                    } else {
                        nickname.set(App.getInstance().getString(R.string.no_login));
                        mobilePhone.set("");
                    }
                }
                break;
            //退出登录
            case EvKey.logout:
                dismissDialog();
                if (eventBean.isStatue()) {
                    App.getInstance().deleteCurrentUser();
                    EventBusUtils.postSuccessEvent(EvKey.loginStatue, BaseRequestCode.OK, "");
                    Toasty.showSuccess(App.getInstance().getString(R.string.logout_success));
                } else {
                    CheckErrorUtil.checkError(eventBean);
                }
                break;
            case EvKey.logout_success_401:
                if (eventBean.isStatue()) {
                    dismissDialog();
                }
                break;
            default:
                break;
        }
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
        isOnPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
    }
}
