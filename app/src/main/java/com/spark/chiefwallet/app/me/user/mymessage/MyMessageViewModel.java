package com.spark.chiefwallet.app.me.user.mymessage;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.ModifyUserNamePopup;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.pojo.UploadPicResult;
import com.spark.ucclient.MemberClient;
import com.spark.ucclient.SecurityClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.pojo.User;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

public class MyMessageViewModel extends BaseViewModel {

    private Context mContext;
    private boolean isUpdateUserName = false;

    //昵称
    public ObservableField<String> nickname = new ObservableField<>(
            App.getInstance().getCurrentUser().getUsername());
    //头像
    public ObservableField<String> avatarUser = new ObservableField<>(App.getInstance().getCurrentUser().getAvatar());

    //修改昵称
    public BindingCommand nickNameModifyOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (App.getInstance().isAppLogin()) {
                new XPopup.Builder(mContext)
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


    public void initContext(Context context) {
        mContext = context;
    }

    public MyMessageViewModel(@NonNull Application application) {
        super(application);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
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
                    nickname.set(user.getUsername());
                    avatarUser.set(user.getAvatar());
                    if (user != null) {
                        App.getInstance().setCurrentUser(user);
                    }
                    nickname.set(user.getUsername());
                    Toasty.showSuccess(App.getInstance().getApplicationContext().getString(R.string.successfully_modified));
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //上传图片
            case EvKey.uploadQRCode:
                dismissDialog();
                if (eventBean.isStatue()) {
                    avatarUser.set(((UploadPicResult) eventBean.getObject()).getData());
                    SecurityClient.getInstance().chartBase64Pic(((UploadPicResult) eventBean.getObject()).getData());
                    Toasty.showSuccess(App.getInstance().getApplicationContext().getString(R.string.upload_success));
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //上传头像成功
            case EvKey.chartBase64Pic:
                dismissDialog();
                if (eventBean.isStatue()) {
                    MemberClient.getInstance().userInfo();
                    //刷新用户信息
                    MemberClient.getInstance().userInfo();
                    Toasty.showSuccess(eventBean.getMessage());
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
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
