package com.spark.chiefwallet.app.me.system.setup;

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
import com.spark.chiefwallet.api.pojo.UpdateBean;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.AppUtils;
import com.spark.chiefwallet.util.CheckErrorUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.BaseRequestCode;
import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LanguageSPUtil;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.StringUtils;

public class SetUpViewModel extends BaseViewModel {
    public SetUpViewModel(@NonNull Application application) {
        super(application);
    }

    private Context mContext;
    public ObservableField<String> languageSelect = new ObservableField<>("");
    public ObservableField<String> updateVersion = new ObservableField<>(AppUtils.getContextVersionName());


    //语言
    public BindingCommand languageOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LANGUAGE)
                    .navigation();
        }
    });
    //计价方式
    public BindingCommand valueTypeOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {

        }
    });
    //版本更新
    public BindingCommand versionUpdateClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            try {
                UpdateBean mUpdateBean = BaseApplication.gson.fromJson(Constant.updateInfoJson, UpdateBean.class);
                if (StringUtils.formatVersionCode(mUpdateBean.getData().getVersion()) >
                        StringUtils.formatVersionCode(AppUtils.getContextVersionName())) {
                    AppUtils.updateApp(mUpdateBean);
                } else {
                    Toasty.showSuccess(App.getInstance().getString(R.string.already_the_latest_version));
                }
            } catch (Exception e) {
                LogUtils.e("checkVersion", e.toString());
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
        switch (eventBean.getOrigin()) {
            //退出登录
            case EvKey.logout:
                dismissDialog();
                if (eventBean.isStatue()) {
                    App.getInstance().deleteCurrentUser();
                    EventBusUtils.postSuccessEvent(EvKey.loginStatue, BaseRequestCode.OK, "");
                    Toasty.showSuccess(App.getInstance().getString(R.string.logout_success));
                    finish();
                } else {
                    CheckErrorUtil.checkError(eventBean);
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
