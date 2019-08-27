package com.spark.chiefwallet.app.me.finance.property.coincharging;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.spark.acclient.pojo.CoinAddressBean;
import com.spark.acclient.pojo.CoinSupportBean;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.ActivityCoinChargingBinding;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.AppUtils;
import com.spark.chiefwallet.util.StatueBarUtils;

import java.util.Hashtable;
import java.util.List;

import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.ImageUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.PermissionConstants;
import me.spark.mvvm.utils.PermissionUtils;
import me.spark.mvvm.utils.StringUtils;

@Route(path = ARouterPath.ACTIVITY_ME_COINCHARGING)
public class CoinChargingActivity extends BaseActivity<ActivityCoinChargingBinding, CoinChargingViewModel> {
    @Autowired(name = "Coin")
    String Coin;

    private TitleBean mTitleModel;
    private Bitmap saveBitmap;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_coin_charging;
    }

    @Override
    public int initVariableId() {
        return BR.coinChargingViewModel;
    }

    @Override
    public void initView() {
        super.initView();

        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.coinChargingTitle.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        mTitleModel = new TitleBean();
        mTitleModel.setShowRightTV(true);
        mTitleModel.setTitleName(Coin + getResources().getString(R.string.coin_in));
        binding.coinChargingTitle.titleLeftImg.setImageDrawable(getResources().getDrawable(R.drawable.svg_back));
        binding.coinChargingTitle.setViewTitle(mTitleModel);
        setTitleListener(binding.coinChargingTitle.titleRootLeft);

        binding.coinRechargeCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isLllegal) {
//                    Toasty.showError(mContext.getString(R.string.no_notes));
//                    return;
//                }
                AppUtils.copy2Clipboard(CoinChargingActivity.this, binding.coinRechargeAddress.getText().toString());
            }
        });

        binding.tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                Unicorn.openServiceActivity(CoinChargingActivity.this, title, source);
            }
        });

    }

    @Override
    public void initData() {
        super.initData();

        viewModel.getCoinInInfo(this, Coin);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.uc.coinAddressBean.observe(this, new Observer<CoinAddressBean>() {
            @Override
            public void onChanged(@Nullable final CoinAddressBean coinAddressBean) {
                binding.tvCoin.setText(coinAddressBean.getData().getCoinId());
                binding.coinRechargeQrCode.post(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isEmpty(coinAddressBean.getData().getAddress())) return;
                        saveBitmap = createQRCode(coinAddressBean.getData().getAddress(), Math.min(binding.coinRechargeQrCode.getWidth(), binding.coinRechargeQrCode.getHeight()));
                        binding.coinRechargeQrCode.setImageBitmap(saveBitmap);
                        binding.coinRechargeQrCode.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                                    @Override
                                    public void onGranted(List<String> permissionsGranted) {
                                        boolean isSaveSUccess = ImageUtils.saveImageToGallery(CoinChargingActivity.this, saveBitmap);
                                        if (isSaveSUccess) {
                                            Toasty.showSuccess(CoinChargingActivity.this.getString(R.string.save_success));
                                        } else {
                                            Toasty.showError(CoinChargingActivity.this.getString(R.string.save_failed));
                                        }
                                    }

                                    @Override
                                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                                        LogUtils.e(permissionsDenied);
                                        Toasty.showError(CoinChargingActivity.this.getString(R.string.camera_permission));
                                    }
                                }).request();
                                return false;
                            }
                        });
                    }
                });

//                final Bitmap bitmap = new QREncode.Builder(CoinChargingActivity.this)
//                        .setColor(Color.BLACK)
//                        .setContents(coinAddressBean.getData().getAddress())
//                        .setMargin(2)
//                        .build().encodeAsBitmap();
//                binding.coinRechargeQrCode.setImageBitmap(bitmap);

                binding.coinRechargeAddress.setText(coinAddressBean.getData().getAddress());
                binding.tip.setText(getResources().getString(R.string.coin_recharge_tips2));
            }
        });

    }

    public static Bitmap createQRCode(String text, int size) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 2);   //设置白边大小 取值为 0- 4 越大白边越大
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

}
