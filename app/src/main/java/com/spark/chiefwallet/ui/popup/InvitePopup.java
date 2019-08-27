package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lxj.xpopup.core.BottomPopupView;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.AppUtils;

import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
 * 创建日期：2019/5/21
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class InvitePopup extends BottomPopupView {

    @BindView(R.id.qr_code)
    ImageView mQrCode;
    @BindView(R.id.cancel)
    ImageView mCancel;
    @BindView(R.id.save_img)
    TextView mSaveImg;
    @BindView(R.id.copy)
    TextView mCopy;
    @BindView(R.id.poster_root)
    LinearLayout mPosterRoot;
    @BindView(R.id.invite_code)
    TextView mInviteCode;
    @BindView(R.id.website_url)
    TextView mWebsiteUrl;
    private Context mContext;
    private String inviteUrl;
    private Bitmap saveBitmap;

    public InvitePopup(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_invite;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mWebsiteUrl.setText(BaseHost.HOST.substring(0, BaseHost.HOST.length() - 1));
        mInviteCode.setText(App.getInstance().getCurrentUser().getPromotionCode());
        inviteUrl = Constant.inviteUrl + Constant.inviteUrlSub + App.getInstance().getCurrentUser().getPromotionCode();
//
//        mBitmap = new QREncode.Builder(mContext)
//                .setColor(Color.BLACK)
//                .setContents(inviteUrl)
//                .setMargin(2)
//                .build().encodeAsBitmap();
//        mQrCode.setImageBitmap(mBitmap);

        mQrCode.post(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isEmpty(App.getInstance().getCurrentUser().getPromotionCode()))
                    return;
                saveBitmap = createQRCode(Constant.inviteUrl + Constant.inviteUrlSub + App.getInstance().getCurrentUser().getPromotionCode(), Math.min(mQrCode.getWidth(), mQrCode.getHeight()));
                mQrCode.setImageBitmap(saveBitmap);
            }
        });
    }

    @OnClick({R.id.cancel,
            R.id.save_img,
            R.id.copy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.save_img:
                final Bitmap saveImg = ImageUtils.view2Bitmap(mPosterRoot);
                PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        boolean isSaveSUccess = ImageUtils.saveImageToGallery(mContext, saveImg);
                        if (isSaveSUccess) {
                            Toasty.showSuccess(mContext.getString(R.string.save_success));
                        } else {
                            Toasty.showError(mContext.getString(R.string.save_failed));
                        }
                        dismiss();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        LogUtils.e(permissionsDenied);
                        Toasty.showError(mContext.getString(R.string.camera_permission));
                        dismiss();
                    }
                }).request();
                break;
            case R.id.copy:
                AppUtils.copy2Clipboard(mContext, inviteUrl);
                dismiss();
                break;
            default:
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
