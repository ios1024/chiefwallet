package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.lxj.xpopup.core.AttachPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.popup.impl.OnTypeChooseListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GearPositionPopup extends AttachPopupView {
    private OnTypeChooseListener mOnTypeChooseListener;
    private Context mContext;

    /**
     * @param context
     * @param mOnTypeChooseListener
     */
    public GearPositionPopup(@NonNull Context context, OnTypeChooseListener mOnTypeChooseListener) {
        super(context);
        this.mContext = context;
        this.mOnTypeChooseListener = mOnTypeChooseListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_gear_position;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
    }

    @OnClick({R.id.gear_5,
            R.id.gear_10,
            R.id.gear_20,
            R.id.gear_50})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gear_5:
                mOnTypeChooseListener.onChooseType(5, "");
                dismiss();
                break;
            case R.id.gear_10:
                mOnTypeChooseListener.onChooseType(10, "");
                dismiss();
                break;
            case R.id.gear_20:
                mOnTypeChooseListener.onChooseType(20, "");
                dismiss();
                break;
            case R.id.gear_50:
                mOnTypeChooseListener.onChooseType(50, "");
                dismiss();
                break;
        }
    }


}
