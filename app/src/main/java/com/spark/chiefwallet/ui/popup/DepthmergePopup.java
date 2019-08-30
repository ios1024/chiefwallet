package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.lxj.xpopup.core.AttachPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.popup.impl.OnTypeChooseListener;

import butterknife.BindView;
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
public class DepthmergePopup extends AttachPopupView {
    @BindView(R.id.merge_2)
    TextView mMerge2;
    @BindView(R.id.merge_4)
    TextView mMerge4;
    @BindView(R.id.merge_6)
    TextView mMerge6;
    private OnTypeChooseListener mOnTypeChooseListener;
    private Context mContext;
    private int mBaseScale;

    /**
     * @param context
     * @param mOnTypeChooseListener
     */
    public DepthmergePopup(@NonNull Context context, int baseScale, OnTypeChooseListener mOnTypeChooseListener) {
        super(context);
        this.mContext = context;
        this.mBaseScale = baseScale;
        this.mOnTypeChooseListener = mOnTypeChooseListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_depth_merge;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        switch (mBaseScale) {
            case 2:
                mMerge2.setVisibility(VISIBLE);
                break;
            case 4:
                mMerge2.setVisibility(VISIBLE);
                mMerge4.setVisibility(VISIBLE);
                break;
            case 6:
                mMerge2.setVisibility(VISIBLE);
                mMerge4.setVisibility(VISIBLE);
                mMerge6.setVisibility(VISIBLE);
                break;
            default:
                mMerge2.setVisibility(VISIBLE);
                mMerge4.setVisibility(VISIBLE);
                break;

        }
    }

    @OnClick({R.id.merge_2,
            R.id.merge_4,
            R.id.merge_6})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.merge_2:
                mOnTypeChooseListener.onChooseType(2, "0.01");
                dismiss();
                break;
            case R.id.merge_4:
                mOnTypeChooseListener.onChooseType(4, "0.0001");
                dismiss();
                break;
            case R.id.merge_6:
                mOnTypeChooseListener.onChooseType(6, "0.000001");
                dismiss();
                break;
        }
    }


}
