package com.spark.chiefwallet.app.me.safe.authentication.passport;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.api.AppClient;
import com.spark.chiefwallet.api.pojo.ArticleListBean;

import com.spark.ucclient.MemberClient;
import com.spark.ucclient.SecurityClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/16
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PassPortViewModel extends BaseViewModel {
    public PassPortViewModel(@NonNull Application application) {
        super(application);
    }
    private String aboutAppUrl, rivacyUrl, clauseUrl, helpCenterUrl;

    //账号
    public ObservableField<String> userName = new ObservableField<>("");
    //护照号
    public ObservableField<String> userPassPortNum = new ObservableField<>("");
    //正面 -手持图片 url
    public ObservableField<String> imgAuthUp = new ObservableField<>("");
    public ObservableField<String> imgAuthHandheld = new ObservableField<>("");
    public ObservableField<String> tiaolie = new ObservableField<>("0");

    public BindingCommand passPortUploadOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uploadIdCard();
        }
    });
    //服务条款
    public BindingCommand termsofserviceOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_EMEX_WEBDETAILS)
                    .withString("title", App.getInstance().getString(R.string.register_clause))
                    .withString("link", StringUtils.isEmpty(clauseUrl) ? "" : clauseUrl)
                    .navigation();
        }
    });
    //隐私政策
    public BindingCommand rivacyOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_EMEX_WEBDETAILS)
                    .withString("title", App.getInstance().getString(R.string.register_rivacy))
                    .withString("link", StringUtils.isEmpty(rivacyUrl) ? "" : rivacyUrl)
                    .navigation();
        }
    });
    public void getArticleList() {
        AppClient.getInstance().getArticleList();
    }

    //上传
    private void uploadIdCard() {
        if (StringUtils.isEmpty(userName.get())) {
            Toasty.showError(App.getInstance().getApplicationContext().getString(R.string.name_hint));
            return;
        }
        if (StringUtils.isEmpty(userPassPortNum.get())) {
            Toasty.showError(App.getInstance().getApplicationContext().getString(R.string.passport_num_hint));
            return;
        }
        if (tiaolie.get().equals("0")) {
            Toasty.showError(App.getInstance().getString(R.string.checklist));
            return;
        }

        showDialog(App.getInstance().getString(R.string.loading));
        SecurityClient.getInstance().uploadAuthInfo(
                1, userPassPortNum.get(), imgAuthUp.get(), imgAuthHandheld.get(), "", userName.get());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        if (eventBean.getType() != 1) return;
        switch (eventBean.getOrigin()) {
            //上传图片
            case EvKey.uploadPortPic:
                dismissDialog();
                if (eventBean.isStatue()) {
                    switch (eventBean.getCode()) {
                        //正面
                        case 0:
                            imgAuthUp.set(eventBean.getMessage());
                            break;
                        //手持
                        case 2:
                            imgAuthHandheld.set(eventBean.getMessage());
                            break;
                    }
                    Toasty.showSuccess(App.getInstance().getString(R.string.image_uploaded_successfully));
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //上传护照
            case EvKey.uploadAuthPort:
                dismissDialog();
                if (eventBean.isStatue()) {
                    //刷新用户信息
                    MemberClient.getInstance().userInfo();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            case EvKey.logout_success_401:
                if (eventBean.isStatue()) {
                    dismissDialog();
                }
                break;
            case EvKey.articleList:
                if (eventBean.isStatue()) {
                    ArticleListBean mArticleListBean = (ArticleListBean) eventBean.getObject();

                    for (int i = 0; i < mArticleListBean.getData().size(); i++) {

                        if (mArticleListBean.getData().get(i).getName().contains("法律")
                                || mArticleListBean.getData().get(i).getName().contains("政策")) {
                            rivacyUrl = mArticleListBean.getData().get(i).getRedirectUrl();
                        }
                        if (mArticleListBean.getData().get(i).getName().contains("用户")) {
                            clauseUrl = mArticleListBean.getData().get(i).getRedirectUrl();
                        }

                    }
                } else {
                    Toasty.showError(App.getInstance().getString(R.string.network_abnormal));
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
