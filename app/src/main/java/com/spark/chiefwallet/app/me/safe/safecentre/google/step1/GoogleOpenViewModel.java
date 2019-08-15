package com.spark.chiefwallet.app.me.safe.safecentre.google.step1;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.spark.chiefwallet.base.ARouterPath;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/29
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GoogleOpenViewModel extends BaseViewModel {
    public GoogleOpenViewModel(@NonNull Application application) {
        super(application);
    }

    //开启谷歌认证器
    public BindingCommand googleBindOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_SAFECENTRE_GOOGLE_BIND)
                    .navigation();
        }
    });
}
