package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.core.BottomPopupView;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.dialog.AdsSelectDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthenticationmethodPopup extends BottomPopupView {

    @BindView(R.id.ll_choice)
    LinearLayout llChoice;
    @BindView(R.id.tv_apply)
    TextView tvApply;
    @BindView(R.id.tv_business)
    TextView tvBusiness;

    private int applytype = 1;

    public AuthenticationmethodPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.activity_authenticationmethod_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();

    }

    private AdsSelectDialog mDialog2;

    private void showTypeDialog() {
        if (mDialog2 == null) {
            mDialog2 = new AdsSelectDialog(getContext());
            mDialog2.setTitle(App.getInstance().getString(R.string.str_apply_type_o), App.getInstance().getString(R.string.str_apply_type_t));
        }
        mDialog2.setInterface(new AdsSelectDialog.LoginSelectInterface() {
            @Override
            public void onSelectType(int type) {
                if (type == 1) {
                    applytype = 1;
                    tvBusiness.setText(App.getInstance().getString(R.string.str_apply_type_o));
                } else if (type == 2) {
                    applytype = 2;
                    tvBusiness.setText(App.getInstance().getString(R.string.str_apply_type_t));
                }
            }
        });
        mDialog2.show();
    }

    private void initView() {
        llChoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypeDialog();
            }
        });
        tvApply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (applytype == 1) {//申请成为个人商家
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_BUSINESS_INDIVIDUAL).withInt("Bond", 0)
                            .navigation();
                    dismiss();
                } else {//申请成为企业商家
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_BUSINESS_INDIVIDUAL).withInt("Bond", 1)
                            .navigation();
                    dismiss();
                }
            }
        });

    }

}
