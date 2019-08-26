package com.spark.chiefwallet.app.trade.legal_currency.order.appeal;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityLcOrderAppeal2Binding;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.PhotoSelectUtils;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.otcclient.LcTradeClient;
import com.spark.otcclient.pojo.LcOrderResult;
import com.spark.otcclient.pojo.OrderAppealBean;

import java.io.File;
import java.util.List;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.PermissionConstants;
import me.spark.mvvm.utils.PermissionUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/18
 * 描    述：
 * 修订历史：
 * ================================================
 */

@Route(path = ARouterPath.ACTIVITY_TRADE_LC_ORCER_APPEAL)
public class LcOrderAppealActivity extends BaseActivity<ActivityLcOrderAppeal2Binding, LcOrderAppealViewModel> {
    @Autowired(name = "orderDetails")
    LcOrderResult.DataBean.RecordsBean orderDetailsBean;

    private TitleBean mTitleModel;
    private PhotoSelectUtils mPhotoSelectUtils;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lc_order_appeal2;
    }

    @Override
    public int initVariableId() {
        return BR.lcOrderAppealViewModel;
    }

    @Override
    public void initView() {
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.orderTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleNameLeft(App.getInstance().getString(R.string.str_appeal));
        binding.orderTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.orderTitle.titleRootLeft);

        initTextChangedListener();
    }

    @OnClick({R.id.btn_ensure
            , R.id.mCheckBox1
            , R.id.mCheckBox2
            , R.id.mCheckBox3
            , R.id.mCheckBox4
            , R.id.mCheckBox5
            , R.id.mCheckBox6
            , R.id.mCheckBox7
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure:
                if (!orderDetailsBean.getOrderType().equals("0")) {
                    if (StringUtils.isNotEmpty(binding.minLimit.getText().toString())) {
                        viewModel.appealContent.set(viewModel.appealContent.get() + " " + binding.minLimit.getText().toString());
                    }
                }
                OrderAppealBean orderAppealBean = new OrderAppealBean();
                orderAppealBean.setOrderId(orderDetailsBean.getOrderSn());
                orderAppealBean.setRemark(viewModel.appealContent.get());
                orderAppealBean.setPicturesOne(viewModel.imgOne.get());
                orderAppealBean.setPicturesTwo(viewModel.imgTwo.get());
                orderAppealBean.setPicturesThree(viewModel.imgThree.get());

                showDialog(App.getInstance().getString(R.string.loading));
                LcTradeClient.getInstance().orderAppeal(orderAppealBean);
                break;
            case R.id.mCheckBox1:
                if (binding.mCheckBox1.isChecked()) return;
                binding.mCheckBox1.setChecked(true);
                binding.mCheckBox2.setChecked(false);
                binding.mCheckBox3.setChecked(false);
                viewModel.appealContent.set(binding.mCheckBox1Text.getText().toString());
                break;
            case R.id.mCheckBox2:
                if (binding.mCheckBox2.isChecked()) return;
                binding.mCheckBox2.setChecked(true);
                binding.mCheckBox1.setChecked(false);
                binding.mCheckBox3.setChecked(false);
                viewModel.appealContent.set(binding.mCheckBox2Text.getText().toString());
                break;
            case R.id.mCheckBox3:
                if (binding.mCheckBox3.isChecked()) return;
                binding.mCheckBox3.setChecked(true);
                binding.mCheckBox2.setChecked(false);
                binding.mCheckBox1.setChecked(false);
                viewModel.appealContent.set(binding.mCheckBox3Text.getText().toString());
                break;
            case R.id.mCheckBox4:
                if (binding.mCheckBox4.isChecked()) return;
                binding.mCheckBox4.setChecked(true);
                binding.mCheckBox5.setChecked(false);
                binding.mCheckBox6.setChecked(false);
                binding.mCheckBox7.setChecked(false);
                viewModel.appealContent.set(binding.mCheckBox4Text.getText().toString());
                break;
            case R.id.mCheckBox5:
                if (binding.mCheckBox5.isChecked()) return;
                binding.mCheckBox5.setChecked(true);
                binding.mCheckBox4.setChecked(false);
                binding.mCheckBox6.setChecked(false);
                binding.mCheckBox7.setChecked(false);
                viewModel.appealContent.set(binding.mCheckBox5Text.getText().toString());
                break;
            case R.id.mCheckBox6:
                if (binding.mCheckBox6.isChecked()) return;
                binding.mCheckBox6.setChecked(true);
                binding.mCheckBox4.setChecked(false);
                binding.mCheckBox5.setChecked(false);
                binding.mCheckBox7.setChecked(false);
                viewModel.appealContent.set(binding.mCheckBox6Text.getText().toString());
                break;
            case R.id.mCheckBox7:
                if (binding.mCheckBox7.isChecked()) return;
                binding.mCheckBox7.setChecked(true);
                binding.mCheckBox4.setChecked(false);
                binding.mCheckBox5.setChecked(false);
                binding.mCheckBox6.setChecked(false);
                viewModel.appealContent.set(binding.mCheckBox7Text.getText().toString());
                break;
        }
    }

    /**
     * 选择图片
     *
     * @param type
     */
    private void selectPic(final int type, final View view) {
        mPhotoSelectUtils = new PhotoSelectUtils(this, new PhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                showDialog(App.getInstance().getString(R.string.str_uploading));
                LcTradeClient.getInstance().uploadIdCardPic(outputFile, type, view.getWidth(), view.getHeight());
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
        // 2、在Activity中的onActivityResult()方法里与LQRPhotoSelectUtils关联
        mPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }

    @Override
    public void initData() {
        viewModel.isShow.set(orderDetailsBean.getOrderType().equals("0"));
        if (orderDetailsBean.getOrderType().equals("0")) {
            binding.mCheckBox1.setChecked(true);
            viewModel.appealContent.set(binding.mCheckBox1Text.getText().toString());
        } else {
            binding.mCheckBox4.setChecked(true);
            viewModel.appealContent.set(binding.mCheckBox4Text.getText().toString());
            CharSequence text = new SpanUtils()
                    .append(App.getInstance().getString(R.string.str_appeal_input_1))
                    .append(" 300 ").setForegroundColor(ContextCompat.getColor(this, R.color.base))
                    .append(App.getInstance().getString(R.string.str_appeal_input_2))
                    .create();
            viewModel.inputRemain.set(text);
        }
    }

    private void initTextChangedListener() {
        //数量监听
        binding.minLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CharSequence text = new SpanUtils()
                        .append(App.getInstance().getString(R.string.str_appeal_input_1))
                        .append(" " + (300 - binding.minLimit.getText().toString().length())).setForegroundColor(ContextCompat.getColor(App.getInstance(), R.color.base))
                        .append(App.getInstance().getString(R.string.str_appeal_input_2))
                        .create();
                viewModel.inputRemain.set(text);
            }
        });

    }
}
