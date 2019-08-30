package com.spark.chiefwallet.app.me.safe.safecentre;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.ucclient.MemberClient;
import com.spark.ucclient.SecurityClient;
import com.spark.ucclient.pojo.AuthInfoEntity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.pojo.User;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/29
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SafeCentreViewModel extends BaseViewModel {
    public SafeCentreViewModel(@NonNull Application application) {
        super(application);
    }

    public ObservableField<String> phoneNum = new ObservableField<>();
    public ObservableField<Boolean> phoneNumHasSet = new ObservableField<>(false);
    public ObservableField<String> emailPwd = new ObservableField<>();
    public ObservableField<Boolean> emailPwdHasSet = new ObservableField<>();
    public ObservableField<String> googleCertification = new ObservableField<>(App.getInstance().getApplicationContext().getString(R.string.no_setting));
    public ObservableField<Boolean> googleCertificationHasSet = new ObservableField<>(false);
    public ObservableField<String> loginPwd = new ObservableField<>(App.getInstance().getApplicationContext().getString(R.string.has_setting));
    public ObservableField<Boolean> loginPwdHasSet = new ObservableField<>(true);
    public ObservableField<String> legalCurrencyPwd = new ObservableField<>(App.getInstance().getApplicationContext().getString(R.string.no_setting));
    public ObservableField<String> realnameCurrencyPwd = new ObservableField<>(App.getInstance().getApplicationContext().getString(R.string.unauthorized));
    public ObservableField<Boolean> legalCurrencyPwdHasSet = new ObservableField<>(false);
    public ObservableField<Boolean> realnameCurrencyPwdHasSet = new ObservableField<>(false);

    //    public int realname = 0;
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Integer> safeLevel = new SingleLiveEvent<>();
    }

    //修改手机号
    public BindingCommand phoneOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (App.getInstance().getCurrentUser().getMobilePhone() != null) return;
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_SAFECENTRE_PHONE)
                    .navigation();
        }
    });
    //绑定邮箱
    public BindingCommand emailOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (App.getInstance().getCurrentUser().getEmail() != null) return;
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_SAFECENTRE_EMAIL)
                    .navigation();
        }
    });
    //开启谷歌认证器
    public BindingCommand googleOpenOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_SAFECENTRE_GOOGLE_OPEN)
                    .navigation();
        }
    });
    //登录密码
    public BindingCommand loginPwdUpdateOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
//            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_SAFECENTRE_LOGIN_PWD)
//                    .navigation();
//            if (realname == 0 || realname == 2 || realname == 3) {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_FORGET_PWD)
                    .withString("type", "1")
                    .navigation();
//            }

        }
    });
    //法币资金密码
    public BindingCommand tradePwdOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_TRADE_PWD)
                    .withInt("isFirst", App.getInstance().getCurrentUser().getFundsVerified())
                    .navigation();
        }
    });
    //实名认证
    public BindingCommand realnameOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {

            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_AUTHENTICATION : ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        }
    });

    public void initItemStatue() {
        //刷新用户信息
        showDialog();
        MemberClient.getInstance().userInfo();
        phoneNumHasSet.set(App.getInstance().getCurrentUser().getMobilePhone() != null);
        phoneNum.set(App.getInstance().getCurrentUser().getMobilePhone() == null ?
                App.getInstance().getApplicationContext().getString(R.string.no_setting) :
                App.getInstance().getCurrentUser().getMobilePhone());
        emailPwdHasSet.set(App.getInstance().getCurrentUser().getEmail() != null);
        emailPwd.set(App.getInstance().getCurrentUser().getEmail() == null ?
                App.getInstance().getApplicationContext().getString(R.string.no_setting)
                : App.getInstance().getCurrentUser().getEmail());
        legalCurrencyPwdHasSet.set(App.getInstance().getCurrentUser().getFundsVerified() == 1);
        legalCurrencyPwd.set(legalCurrencyPwdHasSet.get() ?
                App.getInstance().getApplicationContext().getString(R.string.has_setting) :
                App.getInstance().getApplicationContext().getString(R.string.no_setting));

//        if (App.getInstance().getCurrentUser().getRealNameStatus() == 0) {
//            realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.unauthorized));
//
//        } else {//1审核 2通过 3失败
//            showDialog("请求中...");
//            SecurityClient.getInstance().getAuthInfo();
//        }
        switch (App.getInstance().getCurrentUser().getRealNameStatus()) {
            case 1:
//                            realname = 1;
                realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.approving));
                break;
            case 2:
//                            realname = 2;
                realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.approving_error));
                break;
            case 3:
//                            realname = 3;
                realnameCurrencyPwdHasSet.set(true);
                realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.authorized));
                break;
            default:
//                            realname = 0;
                realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.unauthorized));
                break;
        }
//        realnameCurrencyPwd.set(realnameCurrencyPwdHasSet.get() ?
//                App.getInstance().getApplicationContext().getString(R.string.authorized) :
//                App.getInstance().getApplicationContext().getString(R.string.unauthorized));

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //刷新用户信息
            case EvKey.userInfo:
                dismissDialog();
                if (eventBean.isStatue()) {
                    User user = (User) eventBean.getObject();
                    if (user != null) {
                        App.getInstance().setCurrentUser(user);
                    }
                    Toasty.showSuccess(App.getInstance().getApplicationContext().getString(R.string.successfully_modified));
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;

//            case EvKey.authInfo:
//                dismissDialog();
//                if (eventBean.isStatue()) {
//                    AuthInfoEntity authInfoEntity = (AuthInfoEntity) eventBean.getObject();
//                    realnameCurrencyPwdHasSet.set(authInfoEntity.getData().getAuditStatus() == 3);
//
//                    switch (authInfoEntity.getData().getAuditStatus()) {
//                        case 1:
////                            realname = 1;
//                            realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.approving));
//                            break;
//                        case 2:
////                            realname = 2;
//                            realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.approving_error));
//                            break;
//                        case 3:
////                            realname = 3;
//                            realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.authorized));
//                            break;
//                        default:
////                            realname = 0;
//                            realnameCurrencyPwd.set(App.getInstance().getApplicationContext().getString(R.string.unauthorized));
//                            break;
//                    }
//                }
//                break;
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
