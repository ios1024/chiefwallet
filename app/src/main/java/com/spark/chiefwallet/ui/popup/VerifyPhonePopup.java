package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxj.xpopup.impl.FullScreenPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.VerificationCodeInput;
import com.spark.chiefwallet.ui.impl.OnVerifyPhoneListener;

import cn.iwgang.countdownview.CountdownView;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/17
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class VerifyPhonePopup extends FullScreenPopupView implements View.OnClickListener {
    private ImageView mSmsClose;
    private TextView mSmsPhone;
    private VerificationCodeInput mPhoneInput;
    private CountdownView mSmsTimer;
    private TextView mSmsSendAgain;
    private String phoneNum;
    private OnVerifyPhoneListener mOnVerifyPhoneListener;

    public VerifyPhonePopup(@NonNull Context context, String phoneNum, OnVerifyPhoneListener onVerifyPhoneListener) {
        super(context);
        this.phoneNum = phoneNum;
        this.mOnVerifyPhoneListener = onVerifyPhoneListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.verify_phone_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mSmsClose = findViewById(R.id.sms_close);
        mSmsPhone = findViewById(R.id.sms_phone);
        mPhoneInput = findViewById(R.id.phone_input);
        mSmsTimer = findViewById(R.id.sms_timer);
        mSmsSendAgain = findViewById(R.id.sms_send_again);
        mSmsClose.setOnClickListener(this);
        mSmsSendAgain.setOnClickListener(this);
        mSmsPhone.setText("请输入您注册手机" + phoneNum + "收到的验证码");
        mPhoneInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String smsCode) {
                mOnVerifyPhoneListener.onReceiveCode(smsCode);
            }
        });

        mSmsTimer.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                mSmsSendAgain.setTextColor(Color.BLACK);
                mSmsSendAgain.setEnabled(true);
            }
        });
        mSmsTimer.start(60 * 1000);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        mSmsTimer.stop();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sms_send_again) {
            mOnVerifyPhoneListener.onReSendSMS(this);
        } else if (i == R.id.sms_close) {
            dismiss();
        }
    }
}
