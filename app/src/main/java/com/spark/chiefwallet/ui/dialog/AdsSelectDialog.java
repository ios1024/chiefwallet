package com.spark.chiefwallet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.spark.chiefwallet.R;

/**
 * 选择的弹窗
 */
public class AdsSelectDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private DisplayMetrics dm;
    private TextView tvMessOne;
    private TextView tvMessTwo;
    private TextView tvMessThree;
    private TextView tvMessFour;
    private TextView tvMessFive;
    private View loginTextFive;
    private View loginTextThree;
    private View views;
    private LoginSelectInterface mInterface;

    public void setInterface(LoginSelectInterface face) {
        this.mInterface = face;
    }

    public AdsSelectDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.context = context;
        dm = context.getResources().getDisplayMetrics();
        initView();
    }

    public void setTitle(String msg1, String msg2) {
        if (msg1 != null) {
            loginTextFive.setVisibility(View.GONE);
            loginTextThree.setVisibility(View.GONE);
            tvMessOne.setText(msg1);
            tvMessTwo.setText(msg2);
        }
    }

    public void setTitle(String msg1, String msg2, String msg3) {
        if (msg1 != null) {
            loginTextFive.setVisibility(View.GONE);
            loginTextThree.setVisibility(View.GONE);
            tvMessThree.setVisibility(View.VISIBLE);
            views.setVisibility(View.VISIBLE);
            tvMessOne.setText(msg1);
            tvMessTwo.setText(msg2);
            tvMessThree.setText(msg3);
        }
    }

    public void setTitle(String msg1, String msg2, String msg3, String msg4, String msg5) {
        if (msg1 != null) {
            tvMessThree.setVisibility(View.VISIBLE);
            tvMessFour.setVisibility(View.VISIBLE);
            tvMessFive.setVisibility(View.VISIBLE);
            views.setVisibility(View.VISIBLE);
            tvMessOne.setText(msg1);
            tvMessTwo.setText(msg2);
            tvMessThree.setText(msg3);
            tvMessFour.setText(msg4);
            tvMessFive.setText(msg5);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        params.width = dm.widthPixels;
        // params.height = dm.heightPixels;
        window.setAttributes(params);
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_ads_select, null);
        setContentView(view);
        tvMessOne = view.findViewById(R.id.loginTextOne);
        tvMessTwo = view.findViewById(R.id.loginTextTwo);
        tvMessThree = view.findViewById(R.id.loginTextThree);
        tvMessFour = view.findViewById(R.id.loginTextFour);
        tvMessFive = view.findViewById(R.id.loginTextFive);
        loginTextFive = view.findViewById(R.id.loginTextViewFour);
        loginTextThree = view.findViewById(R.id.loginTextViewThree);
        views = view.findViewById(R.id.loginTextView);
        tvMessTwo.setOnClickListener(this);
        tvMessOne.setOnClickListener(this);
        tvMessThree.setOnClickListener(this);
        tvMessFour.setOnClickListener(this);
        tvMessFive.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginTextOne: //
                if (mInterface != null) {
                    mInterface.onSelectType(1);
                    dismiss();
                }
                break;
            case R.id.loginTextTwo: //
                if (mInterface != null) {
                    mInterface.onSelectType(2);
                    dismiss();
                }
                break;
            case R.id.loginTextThree:
                if (mInterface != null) {
                    mInterface.onSelectType(3);
                    dismiss();
                }
                break;
            case R.id.loginTextFour:
                if (mInterface != null) {
                    mInterface.onSelectType(4);
                    dismiss();
                }
                break;
            case R.id.loginTextFive:
                if (mInterface != null) {
                    mInterface.onSelectType(5);
                    dismiss();
                }
                break;
        }
    }

    public interface LoginSelectInterface {
        void onSelectType(int type);
    }
}
