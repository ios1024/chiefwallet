package com.spark.chiefwallet.app.me.user.register;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.interfaces.XPopupCallback;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.api.AppClient;
import com.spark.chiefwallet.api.pojo.ArticleListBean;
import com.spark.chiefwallet.api.pojo.PromotionCodeLimitResult;
import com.spark.chiefwallet.app.me.user.pwdforget.PwdForgetViewModel;
import com.spark.chiefwallet.app.me.user.register.verificationcode.VerifyCodeActivity;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.ChoiceOfNationalityPopup;
import com.spark.chiefwallet.ui.popup.impl.NationalChoiceListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.CheckErrorUtil;
import com.spark.chiefwallet.util.RegexUtils;
import com.spark.ucclient.CaptchaGetClient;
import com.spark.ucclient.RegisterClient;
import com.spark.ucclient.pojo.Captcha;
import com.spark.ucclient.pojo.CountryEntity;
import com.spark.ucclient.pojo.CountryEntity2;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;

import me.spark.mvvm.base.AppManager;
import me.spark.mvvm.base.BaseHost;
import me.spark.mvvm.base.BaseRequestCode;
import me.spark.mvvm.base.BaseResponseError;
import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.SPUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RegisterViewModel extends BaseViewModel {
    private String strAreaCode = "86";
    private GT3GeetestUtilsBind gt3GeetestUtils;
    private String cid;
    private String[] mCountryArray;
    private Context mContext;
    private List<CountryEntity> mCountryEntityList;
    private String countryEnName = "中国";                      //值传递 国籍 enName
    public int type = 0;                                //0 - 手机注册 1 - 邮箱注册
    public String bannerPicBean;
    private String aboutAppUrl, rivacyUrl, clauseUrl, helpCenterUrl;
    private String checkData = "";

    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public void initContext(Context context) {
        this.mContext = context;
    }

    public ObservableField<String> title = new ObservableField<>(App.getInstance().getString(R.string.register_phone));
    public ObservableField<String> subTitle = new ObservableField<>(App.getInstance().getString(R.string.register_description));
    public ObservableField<String> registerType = new ObservableField<>(App.getInstance().getString(R.string.phone_num));
    public ObservableField<String> registerHint = new ObservableField<>(App.getInstance().getString(R.string.phone_num_hint));
    //手机号码
    public ObservableField<String> phoneNum = new ObservableField<>("");
    public ObservableField<String> verifyCode = new ObservableField<>("");
    public ObservableField<String> newPwd = new ObservableField<>("");
    public ObservableField<String> newPwdAgain = new ObservableField<>("");
    public ObservableField<String> hitphoneNum = new ObservableField<>(App.getInstance().getResources().getString(R.string.phone_num_hint));
    public ObservableField<String> inviteCode = new ObservableField<>("");
    public ObservableField<String> countryName = new ObservableField<>("中国 +86");

    public ObservableField<String> tiaolie = new ObservableField<>("0");
    public ChoiceOfNationalityPopup choiceOfNationalityPopup;

    //国籍选择
    public BindingCommand chooseCountryOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            loadCountryInfo();
        }
    });

    //服务条款
    public BindingCommand termsofserviceOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_EMEX_WEBDETAILS)
                    .withString("title", App.getInstance().getString(R.string.register_clause))
                    .withString("link", StringUtils.isEmpty(clauseUrl) ? "" : clauseUrl)
                    .navigation();
        }
    }); //隐私政策
    public BindingCommand rivacyOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_EMEX_WEBDETAILS)
                    .withString("title", App.getInstance().getString(R.string.register_rivacy))
                    .withString("link", StringUtils.isEmpty(rivacyUrl) ? "" : rivacyUrl)
                    .navigation();
        }
    });
    //密码显示开关
    public BindingCommand pwdSwitchOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.pwdSwitchEvent.setValue(uc.pwdSwitchEvent.getValue() == null || !uc.pwdSwitchEvent.getValue());
        }
    }); //密码显示开关
    public BindingCommand newpwdSwitchOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.newpwdSwitchEvent.setValue(uc.newpwdSwitchEvent.getValue() == null || !uc.newpwdSwitchEvent.getValue());

        }
    });

    //y阅读条例
    public BindingCommand checkBoxOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (tiaolie.get().equals(0)) {
                tiaolie.set("1");
            } else
                tiaolie.set("0");

        }
    });
    //提交登录
    public BindingCommand submissionOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {

            if (type == 0) {
                if (StringUtils.isEmpty(phoneNum.get())) {
                    Toasty.showError(App.getInstance().getString(R.string.phone_num_hint));
                    return;
                }
//                if (!RegexUtils.isMobileExact(phoneNum.get())) {
//                    Toasty.showError(App.getInstance().getString(R.string.valid_phone));
//                    return;
//                }
            } else {
                if (StringUtils.isEmpty(phoneNum.get())) {
                    Toasty.showError(App.getInstance().getString(R.string.email_address_hint));
                    return;
                }
                if (!RegexUtils.isEmail(phoneNum.get())) {
                    Toasty.showError(App.getInstance().getString(R.string.valid_email));
                    return;
                }
            }


            if (StringUtils.isEmpty(countryName.get()) || StringUtils.isEmpty(countryName.get())) {
                Toasty.showError(mContext.getString(R.string.choose_country));
                return;
            }

            if (StringUtils.isEmpty(verifyCode.get())) {
                Toasty.showError(App.getInstance().getString(R.string.verify_code_hint));
                return;
            }

            if (StringUtils.isEmpty(newPwd.get())) {
                Toasty.showError(App.getInstance().getString(R.string.new_pwd_hint));
                return;
            }

            if (!RegexUtils.isPassword(newPwd.get())) {
                Toasty.showError(App.getInstance().getString(R.string.pwd_error));
                return;
            }

            if (StringUtils.isEmpty(newPwdAgain.get())) {
                Toasty.showError(App.getInstance().getString(R.string.new_pwd_ensure_hint));
                return;
            }

            if (!newPwd.get().equals(newPwdAgain.get())) {
                Toasty.showError(App.getInstance().getString(R.string.pwd_inconsistent));
                return;
            }
            if (tiaolie.get().equals("0")) {
                Toasty.showError(App.getInstance().getString(R.string.checklist));
                return;
            }

            showDialog(App.getInstance().getString(R.string.loading));
//            AppClient.getInstance().checkPromotionLimit();//
            registerRequest();

        }
    });

    public void getArticleList() {
        AppClient.getInstance().getArticleList();
    }

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> mGetCodeSuccessLiveEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> pwdSwitchEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> newpwdSwitchEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> smsCodePopopShow = new SingleLiveEvent<>();

    }

    /**
     * 获取短信验证码
     */
    public void getPhoneCode(int type) {
        showDialog(App.getInstance().getString(R.string.loading));
        if (type == 0) {
            CaptchaGetClient.getInstance().phoneCaptcha(strAreaCode + phoneNum.get());
        } else
            CaptchaGetClient.getInstance().emailCaptcha(phoneNum.get());
    }


//    //获取短信验证码
//    public BindingCommand getCodeOnClickCommand = new BindingCommand(new BindingAction() {
//        @Override
//        public void call() {
//            switch (type) {
//                //手机注册
//                case 0:
//                    if (!RegexUtils.isMobileExact(phoneNum.get())) {
//                        Toasty.showError(mContext.getString(R.string.valid_phone));
//                        return;
//                    }
//                    if (StringUtils.isEmpty(strAreaCode) || StringUtils.isEmpty(countryName.get())) {
//                        Toasty.showError(mContext.getString(R.string.choose_country));
//                        return;
//                    }
//                    showDialog(mContext.getString(R.string.loading));
//                    CaptchaGetClient.getInstance().phoneCaptcha(strAreaCode + phoneNum.get());
//                    break;
//                //邮箱注册
//                case 1:
//                    if (!RegexUtils.isEmail(phoneNum.get())) {
//                        Toasty.showError(mContext.getString(R.string.valid_email));
//                        return;
//                    }
//
//                    showDialog(mContext.getString(R.string.loading));
//                    CaptchaGetClient.getInstance().emailCaptcha(phoneNum.get());
//                    break;
//            }
//        }
//    });

    public void typeContext(int type) {
        this.type = type;
        if (type == 0) {
            hitphoneNum.set(App.getInstance().getResources().getString(R.string.phone_num_hint));
        } else {
            hitphoneNum.set(App.getInstance().getResources().getString(R.string.email_address_hint));

        }
    }

    //获取国籍列表
    private void loadCountryInfo() {
        //避免重复请求
        if (mCountryArray != null) {
            new XPopup.Builder(mContext)
                    .setPopupCallback(new XPopupCallback() {
                        @Override
                        public void onShow() {
                            uc.smsCodePopopShow.setValue(true);
                        }

                        @Override
                        public void onDismiss() {
                            uc.smsCodePopopShow.setValue(false);
                        }

                    })
                    .asCustom(choiceOfNationalityPopup).show();
//            new XPopup.Builder(mContext)
//                    .asBottomList(mContext.getString(R.string.choose_country), mCountryArray,
//                            new OnSelectListener() {
//                                @Override
//                                public void onSelect(int position, String text) {
//                                    countryEnName = mCountryEntityList.get(position).getEnName();
//                                    updateCountryInfo(mCountryEntityList.get(position).getZhName() + " +" + mCountryEntityList.get(position).getAreaCode(), mCountryEntityList.get(position).getAreaCode());
//                                }
//                            })
//                    .show();
        } else {
            showDialog(mContext.getString(R.string.loading));
            RegisterClient.getInstance().findSupportCountry();
        }
    }

    private void registerRequest() {
        switch (type) {
            case 0:
                if (StringUtils.isNotEmpty(cid, checkData)) {
                    RegisterClient.getInstance().registerByPhone(cid, checkData, "", newPwd.get(), countryEnName, inviteCode.get(), strAreaCode + phoneNum.get());
                } else {
                    RegisterClient.getInstance().registerByPhone("", verifyCode.get(), newPwd.get(), countryEnName, inviteCode.get(), strAreaCode + phoneNum.get());
                }
                break;
            case 1:
                if (StringUtils.isNotEmpty(cid, checkData)) {
                    RegisterClient.getInstance().registerByEmail(cid, checkData, "", newPwd.get(), countryEnName, inviteCode.get(), phoneNum.get());
                } else {
                    RegisterClient.getInstance().registerByEmail("", verifyCode.get(), newPwd.get(), countryEnName, inviteCode.get(), phoneNum.get());
                }
                break;
        }

    }

    private void registerSuccess() {
        Toasty.showSuccess(App.getInstance().getString(R.string.register_success));
        AppManager.getAppManager().finishActivity(RegisterActivity.class);
        AppManager.getAppManager().finishActivity(VerifyCodeActivity.class);
        ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                .navigation();
        finish();
    }

    private void dealError(EventBean eventBean) {
        if (gt3GeetestUtils != null) {
            gt3GeetestUtils.gt3TestClose();
            gt3GeetestUtils = null;
        }

        BaseResponseError responseError = (BaseResponseError) eventBean.getObject();
        if (responseError != null) {
            String msg = responseError.getMessage();
            if (responseError.getCode() == BaseRequestCode.ERROR_411 && StringUtils.isNotEmpty(msg) && msg.contains("captcha")) {
                //极验验证 411
                cid = responseError.getCid();
                gt3GeetestUtils = new GT3GeetestUtilsBind(mContext);
                CaptchaGetClient.getInstance().geeCaptcha();
            } else if (responseError.getCode() == BaseRequestCode.ERROR_412 && StringUtils.isNotEmpty(msg) && msg.contains("Captcha")) {
                //验证码错误
                Toasty.showError(App.getInstance().getString(R.string.sms_code_error));
                finish();
            } else {
                CheckErrorUtil.checkError(eventBean);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            case EvKey.registerByPhone:
                dismissDialog();
                if (eventBean.isStatue()) {
                    registerSuccess();
                } else {
                    dealError(eventBean);
                }
                break;
            //是否强制输入邀请码
            case EvKey.promotionCodeLimit:
                if (eventBean.isStatue()) {
                    PromotionCodeLimitResult promotionCodeLimitResult = (PromotionCodeLimitResult) eventBean.getObject();
                    if (promotionCodeLimitResult.getData().getValue().equals("true")) {
                        if (TextUtils.isEmpty(inviteCode.get())) {
                            dismissDialog();
                            Toasty.showError(App.getInstance().getString(R.string.invite_code_hint));
                            return;
                        }
                        AppClient.getInstance().checkPromotionCode(inviteCode.get().trim());
                    } else {
                        if (TextUtils.isEmpty(inviteCode.get())) {
                            registerRequest();
                        } else {
                            AppClient.getInstance().checkPromotionCode(inviteCode.get().trim());
                        }
                    }
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;

            //获取国籍列表
            case EvKey.findSupportCountry:
                dismissDialog();
                if (eventBean.isStatue()) {
                    final List<CountryEntity> objList = (List<CountryEntity>) eventBean.getObject();
                    if (!objList.isEmpty()) {
                        if (choiceOfNationalityPopup == null) {
                            choiceOfNationalityPopup = new ChoiceOfNationalityPopup(mContext, objList, new NationalChoiceListener() {

                                @Override
                                public void onClickItem(int position, List<CountryEntity2> countryEntities2) {

                                    updateCountryInfo(countryEntities2.get(position).getZhName() + " +" + countryEntities2.get(position).getAreaCode(), countryEntities2.get(position).getAreaCode());

                                }
                            });
                        }
                        new XPopup.Builder(mContext)
                                .setPopupCallback(new XPopupCallback() {
                                    @Override
                                    public void onShow() {
                                        uc.smsCodePopopShow.setValue(true);
                                    }

                                    @Override
                                    public void onDismiss() {
                                        uc.smsCodePopopShow.setValue(false);
                                    }

                                })
                                .asCustom(choiceOfNationalityPopup).show();
//                        mCountryEntityList = objList;
//                        mCountryArray = new String[objList.size()];
//                        for (int i = 0; i < objList.size(); i++) {
//                            mCountryArray[i] = objList.get(i).getZhName();
//                        }
//                        if (mCountryArray.length > 0) {
//                            new XPopup.Builder(mContext)
//                                    .asBottomList(mContext.getString(R.string.choose_country), mCountryArray,
//                                            new OnSelectListener() {
//                                                @Override
//                                                public void onSelect(int position, String text) {
//                                                    countryEnName = objList.get(position).getEnName();
//                                                    updateCountryInfo(objList.get(position).getZhName() + " +" + objList.get(position).getAreaCode(), mCountryEntityList.get(position).getAreaCode());
//                                                }
//                                            })
//                                    .show();
//                        }
                    } else {
                        Toasty.showInfo(mContext.getString(R.string.country_list_null));
                    }
                } else {
                    CheckErrorUtil.checkError(eventBean);
                }
                break;
            //手机验证
            case EvKey.phoneCaptcha:
                dismissDialog();
                if (eventBean.isStatue()) {
                    if (gt3GeetestUtils != null) {
                        gt3GeetestUtils.gt3TestFinish();
                        gt3GeetestUtils = null;
                    }
                    uc.mGetCodeSuccessLiveEvent.setValue(true);
                } else {
                    if (gt3GeetestUtils != null) {
                        gt3GeetestUtils.gt3TestClose();
                        gt3GeetestUtils = null;
                    }

                    BaseResponseError responseError = (BaseResponseError) eventBean.getObject();
                    if (responseError != null) {
                        String msg = responseError.getMessage();
                        if (responseError.getCode() == BaseRequestCode.ERROR_411 && StringUtils.isNotEmpty(msg) && msg.contains("captcha")) {
                            cid = responseError.getCid();
                            gt3GeetestUtils = new GT3GeetestUtilsBind(mContext);
                            CaptchaGetClient.getInstance().geeCaptcha();
                        } else if (responseError.getCode() == BaseRequestCode.ERROR_412 && StringUtils.isNotEmpty(msg) && msg.contains("Captcha")) {
                            //解决验证码失效问题
                            cid = responseError.getCid();
                            gt3GeetestUtils = new GT3GeetestUtilsBind(mContext);
                            CaptchaGetClient.getInstance().geeCaptcha();
                        } else {
                            CheckErrorUtil.checkError(eventBean);
                        }
                    }
                }
//                dismissDialog();
//                if (eventBean.isStatue()) {
//                    if (gt3GeetestUtils != null) {
//                        gt3GeetestUtils.gt3TestFinish();
//                        gt3GeetestUtils = null;
//                    }
//
//
//
////                    new Handler().postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_VERIFYCODE)
////                                    .withString("phoneNum", strAreaCode + phoneNum.get())
////                                    .withString("strCountry", countryEnName)
////                                    .withInt("type", type)
////                                    .navigation();
////                        }
////                    }, 1000);
//
//
//                } else {
//                    if (gt3GeetestUtils != null) {
//                        gt3GeetestUtils.gt3TestClose();
//                        gt3GeetestUtils = null;
//                    }
//
//                    BaseResponseError responseError = (BaseResponseError) eventBean.getObject();
//                    if (responseError != null) {
//                        String msg = responseError.getMessage();
//                        if (responseError.getCode() == BaseRequestCode.ERROR_411 && StringUtils.isNotEmpty(msg) && msg.contains("captcha")) {
//                            cid = responseError.getCid();
//                            gt3GeetestUtils = new GT3GeetestUtilsBind(mContext);
//                            CaptchaGetClient.getInstance().geeCaptcha();
//                        } else if (responseError.getCode() == BaseRequestCode.ERROR_412 && StringUtils.isNotEmpty(msg) && msg.contains("Captcha")) {
//                            //解决验证码失效问题
//                            cid = responseError.getCid();
//                            gt3GeetestUtils = new GT3GeetestUtilsBind(mContext);
//                            CaptchaGetClient.getInstance().geeCaptcha();
//                        } else {
//                            CheckErrorUtil.checkError(eventBean);
//                        }
//                    }
//                }
                break;
            //邮箱验证
            case EvKey.emailCaptcha:
                dismissDialog();
                if (eventBean.isStatue()) {
                    if (gt3GeetestUtils != null) {
                        gt3GeetestUtils.gt3TestFinish();
                        gt3GeetestUtils = null;
                    }
                    uc.mGetCodeSuccessLiveEvent.setValue(true);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_VERIFYCODE)
//                                    .withString("phoneNum", phoneNum.get())
//                                    .withString("strCountry", countryEnName)
//                                    .withInt("type", type)
//                                    .navigation();
//                        }
//                    }, 1000);

                } else {
                    if (gt3GeetestUtils != null) {
                        gt3GeetestUtils.gt3TestClose();
                        gt3GeetestUtils = null;
                    }

                    BaseResponseError responseError = (BaseResponseError) eventBean.getObject();
                    if (responseError != null) {
                        String msg = responseError.getMessage();
                        if (responseError.getCode() == BaseRequestCode.ERROR_411 && StringUtils.isNotEmpty(msg) && msg.contains("captcha")) {
                            cid = responseError.getCid();
                            gt3GeetestUtils = new GT3GeetestUtilsBind(mContext);
                            CaptchaGetClient.getInstance().geeCaptcha();
                        } else if (responseError.getCode() == BaseRequestCode.ERROR_412 && StringUtils.isNotEmpty(msg) && msg.contains("Captcha")) {
                            //解决验证码失效问题
                            cid = responseError.getCid();
                            gt3GeetestUtils = new GT3GeetestUtilsBind(mContext);
                            CaptchaGetClient.getInstance().geeCaptcha();
                        } else {
                            CheckErrorUtil.checkError(eventBean);
                        }
                    }
                }
                break;
            //极验验证
            case EvKey.geeCaptcha:
                if (eventBean.isStatue()) {
                    gt3GeetestUtils.gtSetApi1Json((JSONObject) eventBean.getObject());
                    gt3GeetestUtils.getGeetest(mContext, BaseHost.UC_HOST + "captcha/mm/gee", null, null, new GT3GeetestBindListener() {
                        @Override
                        public boolean gt3SetIsCustom() {
                            return true;
                        }

                        @Override
                        public void gt3GetDialogResult(boolean status, String result) {
                            if (status) {
                                Captcha captcha = new Gson().fromJson(result, Captcha.class);
                                checkData = "gee::" + captcha.getGeetest_challenge() + "$" + captcha.getGeetest_validate() + "$" + captcha.getGeetest_seccode();
                                switch (type) {
                                    case 0:
                                        CaptchaGetClient.getInstance().phoneCaptchaWithHeader(strAreaCode + phoneNum.get(), checkData, cid);
                                        break;
                                    case 1:
                                        CaptchaGetClient.getInstance().emailCaptchaWithHeader(phoneNum.get(), checkData, cid);
                                        break;
                                }
                            }
                        }
                    });
                    gt3GeetestUtils.setDialogTouch(true);
                } else {
                    CheckErrorUtil.checkError(eventBean);
                }
                break;
            case EvKey.registerSuccess:
                finish();
                break;
            case EvKey.articleList:
                if (eventBean.isStatue()) {
                    ArticleListBean mArticleListBean = (ArticleListBean) eventBean.getObject();
                    for (int i = 0; i < mArticleListBean.getData().size(); i++) {

                        if (mArticleListBean.getData().get(i).getName().contains("法律")
                                || mArticleListBean.getData().get(i).getName().contains("政策")) {
                            rivacyUrl = mArticleListBean.getData().get(i).getRedirectUrl();
                        }
                        if (mArticleListBean.getData().get(i).getName().contains("用户")) {
                            clauseUrl = mArticleListBean.getData().get(i).getRedirectUrl();
                        }

                    }
                } else {
                    Toasty.showError(App.getInstance().getString(R.string.network_abnormal));
                }
                break;
            default:
                break;
        }
    }


    /**
     * 更新国籍展示
     *
     * @param strCountry
     */
    public void updateCountryInfo(String strCountry, String code) {
        strAreaCode = code;
        countryName.set(strCountry);
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBusUtils.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBusUtils.unRegister(this);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        EventBusUtils.register(this);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        EventBusUtils.unRegister(this);
//    }
}
