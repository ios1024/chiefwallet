package com.spark.chiefwallet.app.me.user.mymessage;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityMyMessageBinding;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.PhotoSelectUtils;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.PayControlClient;

import java.io.File;
import java.util.List;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.PermissionConstants;
import me.spark.mvvm.utils.PermissionUtils;

@Route(path = ARouterPath.ACTIVITY_ME_MYMESSAGE)
public class MyMessageActivity extends BaseActivity<ActivityMyMessageBinding, MyMessageViewModel> {

    private TitleBean mTitleModel;
    private PhotoSelectUtils mPhotoSelectUtils;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_my_message;
    }

    @Override
    public int initVariableId() {
        return BR.myMessageViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        viewModel.initContext(this);
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.openOrdersTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        binding.openOrdersTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.openOrdersTitle.titleRootLeft);

        //TitleSet
    }

    @OnClick({R.id.chartOnClickCommand})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.chartOnClickCommand:
                if (App.getInstance().getCurrentUser().getRealNameStatus() == 3) {
                    chooseQrCod(view);
                } else
                    Toasty.showError(getString(R.string.real_name_certification));
                break;
            default:
                break;
        }

    }

    private void chooseQrCod(final View view) {
        mPhotoSelectUtils = new PhotoSelectUtils(this, new PhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                showDialog(App.getInstance().getString(R.string.str_uploading));
                PayControlClient.getInstance().uploadQRCode(outputFile, 50, 50);
            }
        }, true);

        new XPopup.Builder(this)
                .asBottomList(App.getInstance().getString(R.string.please_choose), new String[]{App.getInstance().getString(R.string.take_photo), App.getInstance().getString(R.string.album)},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                choosePicOrigin(position);
                            }
                        })
                .show();
    }


    /**
     * 选择图片来源
     *
     * @param position 0-拍照  1-图库
     */
    private void choosePicOrigin(final int position) {
        switch (position) {
            case 0:
                PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        mPhotoSelectUtils.takePhoto();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        LogUtils.e(permissionsDenied);
                        Toasty.showError(getString(R.string.camera_permission));
                    }
                }).request();
                break;
            case 1:
                PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        mPhotoSelectUtils.selectPhoto();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        Toasty.showError(getString(R.string.storage_permission));
                    }
                }).request();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }

}
