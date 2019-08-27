package com.spark.chiefwallet.app.me.safe.business.individualbusinessmen;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.safe.business.CertificationDetailsActivity;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityIndividualBusinessmenBinding;
import com.spark.chiefwallet.ui.SmoothCheckBox;
import com.spark.chiefwallet.ui.dialog.AdsSelectDialog;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.PhotoSelectUtils;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.LcTradeClient;
import com.spark.otcclient.PayControlClient;
import com.spark.otcclient.pojo.AdvertiseCoinResult;

import java.io.File;
import java.util.List;

import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.PermissionConstants;
import me.spark.mvvm.utils.PermissionUtils;
import me.spark.mvvm.utils.SpanUtils;

@Route(path = ARouterPath.ACTIVITY_BUSINESS_INDIVIDUAL)
public class IndividualBusinessmenActivity extends BaseActivity<ActivityIndividualBusinessmenBinding, IndividualBusinessmenViewModel> {
    @Autowired(name = "Bond")
    int Bond;

    private TitleBean mTitleModel;
    private PhotoSelectUtils mPhotoSelectUtils;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_individual_businessmen;
    }

    @Override
    public int initVariableId() {
        return BR.individualBusinessmenViewModel;
    }

    @Override
    public void initView() {
        super.initView();
        viewModel.myContext(this);
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.certificationTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleName(getString(R.string.str_apply_type_o));
        binding.certificationTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.certificationTitle.titleRootLeft);

        viewModel.bondtype(Bond);
        if (Bond == 0) {
            binding.tvPart.setVisibility(View.GONE);
        } else {
            binding.tvPart.setVisibility(View.VISIBLE);
            LcTradeClient.getInstance().authMerchantType();
        }
        binding.wechatCb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    viewModel.tiaolie.set("1");
                } else {
                    viewModel.tiaolie.set("0");
                }
            }
        });
        binding.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseQrCod(view);

            }
        });
    }

    private void chooseQrCod(final View view) {
        mPhotoSelectUtils = new PhotoSelectUtils(this, new PhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                showDialog(App.getInstance().getString(R.string.str_uploading));
                PayControlClient.getInstance().uploadQRCode(outputFile, 120, 180);
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

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.mbusMoney.observe(this, new Observer<AdvertiseCoinResult>() {
            @Override
            public void onChanged(@Nullable AdvertiseCoinResult advertiseCoinResult) {
                binding.tvBond.setText(new SpanUtils().
                        append(getString(R.string.to_be_a_certified_business_needs_to_be_frozen)).
                        append("   " + advertiseCoinResult.getData().getAmount() + "  " + advertiseCoinResult.getData().getCoinName() + "   ").setForegroundColor(ContextCompat.getColor(IndividualBusinessmenActivity.this, R.color.base)).
                        append(getString(R.string.as_a_deposit)).create());
                binding.unifiedCheck.setText(new SpanUtils().
                        append(getString(R.string.str_frost_gold_one)).
                        append("   " + advertiseCoinResult.getData().getAmount() + "  " + advertiseCoinResult.getData().getCoinName() + "   ").setForegroundColor(ContextCompat.getColor(IndividualBusinessmenActivity.this, R.color.base)).
                        append(getString(R.string.str_frost_gold_two)).create());


            }
        });
    }
}
