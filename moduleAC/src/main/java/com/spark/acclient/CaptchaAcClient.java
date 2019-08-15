package com.spark.acclient;

import com.spark.acclient.base.AcHost;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.BaseHost;
import me.spark.mvvm.base.BaseRequestCode;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.http.BaseHttpClient;
import me.spark.mvvm.http.GeneralResult;
import me.spark.mvvm.utils.EventBusUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/10
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CaptchaAcClient extends BaseHttpClient {
    private static CaptchaAcClient sCaptchaAcClient;

    private CaptchaAcClient() {
    }

    public static CaptchaAcClient getInstance() {
        if (sCaptchaAcClient == null) {
            synchronized (CaptchaAcClient.class) {
                if (sCaptchaAcClient == null) {
                    sCaptchaAcClient = new CaptchaAcClient();
                }
            }
        }
        return sCaptchaAcClient;
    }


    /**
     * 获取短信验证码
     *
     * @param phone
     */
    public void phoneCaptcha(String phone) {
        EasyHttp.get(AcHost.phoneCaptchaUrl)
                .baseUrl(BaseHost.AC_HOST)
                .params("phone", phone)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        try {
                            GeneralResult generalResult = BaseApplication.gson.fromJson(s, GeneralResult.class);
                            if (generalResult.getCode() == BaseRequestCode.OK) {
                                EventBusUtils.postSuccessEvent(EvKey.acPhoneCaptcha, generalResult.getCode(), generalResult.getMessage());
                            } else {
                                if (generalResult.getCode() == BaseRequestCode.ERROR_401) {
                                    uodateLogin(generalResult);
                                } else {
                                    EventBusUtils.postErrorEvent(EvKey.acPhoneCaptcha, generalResult.getCode(), generalResult.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            postException(EvKey.acPhoneCaptcha, e);
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        postError(EvKey.acPhoneCaptcha, e);
                    }
                });
    }
}
