package com.spark.chiefwallet.app.invite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.finance.property.coincharging.CoinChargingActivity;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.databinding.FragmentInviteBinding;
import com.spark.chiefwallet.ui.toast.Toasty;

import java.util.Hashtable;
import java.util.List;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseFragment;
import me.spark.mvvm.base.BaseHost;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.utils.ImageUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.PermissionConstants;
import me.spark.mvvm.utils.PermissionUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/18
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class InviteFragment extends BaseFragment<FragmentInviteBinding, InviteViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_invite;
    }

    private Bitmap saveBitmap;

    @Override
    public int initVariableId() {
        return BR.inviteViewModel;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        viewModel.isVisible2User = !hidden;
    }

    @Override
    public void initView() {
        viewModel.initContext(getActivity());
    }

    @Override
    public void initData() {
        viewModel.loadDate();
    }

    @OnClick({R.id.invite_friend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invite_friend:
                if (!App.getInstance().isAppLogin()) {
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                            .navigation();
                    return;
                }

                binding.websiteUrl.setText(BaseHost.HOST.substring(0, BaseHost.HOST.length() - 1));
                binding.inviteCode.setText(App.getInstance().getCurrentUser().getPromotionCode());
//                final Bitmap mBitmap = new QREncode.Builder(getActivity())
//                        .setColor(Color.BLACK)
//                        .setContents(Constant.inviteUrl + Constant.inviteUrlSub + App.getInstance().getCurrentUser().getPromotionCode())
//                        .setMargin(2)
//                        .build().encodeAsBitmap();
//                binding.qrCode.setImageBitmap(mBitmap);


                binding.qrCode.post(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isEmpty(App.getInstance().getCurrentUser().getPromotionCode()))
                            return;
                        saveBitmap = createQRCode(Constant.inviteUrl + Constant.inviteUrlSub + App.getInstance().getCurrentUser().getPromotionCode(), Math.min(binding.qrCode.getWidth(), binding.qrCode.getHeight()));
                        binding.qrCode.setImageBitmap(saveBitmap);
                    }
                });

                PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), ImageUtils.view2Bitmap(binding.posterRoot), null, null));

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        shareIntent.setType("image/*");
                        getActivity().startActivity(Intent.createChooser(shareIntent, getActivity().getString(R.string.share_to)));
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        LogUtils.e(permissionsDenied);
                        Toasty.showError(getActivity().getString(R.string.camera_permission));
                    }
                }).request();
                break;
        }
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
