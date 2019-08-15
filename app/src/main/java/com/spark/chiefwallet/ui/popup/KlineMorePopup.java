package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.lxj.xpopup.core.AttachPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.popup.impl.OnKlineMoreListener;

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
public class KlineMorePopup extends AttachPopupView {
    private OnKlineMoreListener mOnKlineMoreListener;

    public KlineMorePopup(@NonNull Context context, OnKlineMoreListener mOnKlineMoreListener) {
        super(context);
        this.mOnKlineMoreListener = mOnKlineMoreListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_kline_more;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
    }


    @OnClick({R.id.popup_kline_more_15min,
            R.id.popup_kline_more_30min,
            R.id.popup_kline_more_1week,
            R.id.popup_kline_more_1month})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popup_kline_more_15min:
                mOnKlineMoreListener.onSelectMore("15分钟", 5);
                break;
            case R.id.popup_kline_more_30min:
                mOnKlineMoreListener.onSelectMore("30分钟", 6);
                break;
            case R.id.popup_kline_more_1week:
                mOnKlineMoreListener.onSelectMore("1周", 7);
                break;
            case R.id.popup_kline_more_1month:
                mOnKlineMoreListener.onSelectMore("1月", 8);
                break;
        }
    }

}
