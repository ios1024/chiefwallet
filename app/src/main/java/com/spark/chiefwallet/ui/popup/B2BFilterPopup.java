package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lxj.xpopup.impl.PartShadowPopupView;
import com.spark.chiefwallet.R;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class B2BFilterPopup extends PartShadowPopupView {
    public B2BFilterPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_b2b_filter;
    }
}
