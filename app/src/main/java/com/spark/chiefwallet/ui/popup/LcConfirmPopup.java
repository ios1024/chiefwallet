package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.lxj.xpopup.core.CenterPopupView;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.SmoothCheckBox;
import com.spark.chiefwallet.ui.popup.impl.OnB2BBuyListener;
import com.spark.chiefwallet.ui.toast.Toasty;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/5
 * 描    述：取消订单、释放订单时确认弹框
 * 修订历史：
 * ================================================
 */
public class LcConfirmPopup extends CenterPopupView {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.mCheckBox)
    SmoothCheckBox mCheckBox;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.ensure)
    TextView ensure;

    String title, content, tip;

    OnB2BBuyListener onB2BBuyListener;

    public LcConfirmPopup(@NonNull Context context, String title, String content, String tip, OnB2BBuyListener onB2BBuyListener) {
        super(context);
        this.title = title;
        this.content = content;
        this.tip = tip;
        this.onB2BBuyListener = onB2BBuyListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_lc_cancel_order;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitle.setText(title);
        tvContent.setText(content);
        tvTip.setText(tip);
    }

    @OnClick({R.id.ensure,
            R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ensure:
                if (mCheckBox.isChecked()) {
                    dismiss();
                    onB2BBuyListener.onClickConfirm();
                } else {
                    Toasty.showError(App.getInstance().getString(R.string.str_please_check));
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
