package com.spark.chiefwallet.app.me.user.login;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.interfaces.XPopupCallback;
import com.spark.casclient.CasClient;
import com.spark.casclient.base.CasHost;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.popup.ChoiceOfNationalityPopup;
import com.spark.chiefwallet.ui.popup.SmsVerifyPopup;
import com.spark.chiefwallet.ui.popup.impl.NationalChoiceListener;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.CheckErrorUtil;
import com.spark.chiefwallet.util.RegexUtils;
import com.spark.modulespot.SpotCoinClient;
import com.spark.modulespot.pojo.FavorFindResult;
import com.spark.ucclient.MemberClient;
import com.spark.ucclient.RegisterClient;
import com.spark.ucclient.pojo.Captcha;
import com.spark.ucclient.pojo.CountryEntity;
import com.spark.ucclient.pojo.CountryEntity2;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseHost;
import me.spark.mvvm.base.BaseRequestCode;
import me.spark.mvvm.base.BaseResponseError;
import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.pojo.User;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LanguageSPUtil;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.SPUtils;
import me.spark.mvvm.utils.StringUtils;
import okhttp3.Cookie;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LoginViewModel extends BaseViewModel {
    private String strAreaCode = "86";
    private boolean isCasLogin = false;
    private String cid;
    private GT3GeetestUtilsBind gt3GeetestUtils;
    private Context mContext;
    private SmsVerifyPopup mSmsVerifyPopup;
    private int loginType;                  //0 - 手机登录 1 - 邮箱登录
    private String countryEnName = "China";                      //值传递 国籍 enName

    private List<CountryEntity> mCountryEntityList;

    public void initContext(Context context) {
        this.mContext = context;
        initSmsVerifyPopup();
    }

    public void typeContext(int type) {
        this.loginType = type;
    }

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    private String[] mCountryArray;

    //账号
    public ObservableField<String> userName = new ObservableField<>("");
    //密码
    public ObservableField<String> userPassWord = new ObservableField<>("");
    public ObservableField<String> countryName = new ObservableField<>("China +86");
    public UIChangeObservable uc = new UIChangeObservable();

    public ChoiceOfNationalityPopup choiceOfNationalityPopup;

    public class UIChangeObservable {
        //密码开关观察者
        public SingleLiveEvent<Boolean> pwdSwitchEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> smsCodePopopShow = new SingleLiveEvent<>();
    }

    //密码显示开关
    public BindingCommand pwdSwitchOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.pwdSwitchEvent.setValue(uc.pwdSwitchEvent.getValue() == null || !uc.pwdSwitchEvent.getValue());
        }
    });

    //登录
    public BindingCommand loginOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            login();
        }
    });
    //忘记密码
    public BindingCommand pwdForGetOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_FORGET_PWD)
                    .withString("type", "0")
                    .navigation();
        }
    });
    //注册
    public BindingCommand registerOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_REGISTER)
                    .navigation();
        }
    });
    //国籍选择
    public BindingCommand chooseCountryOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            loadCountryInfo();
        }
    });


    public void login() {
        if (TextUtils.isEmpty(userName.get())) {
            Toasty.showError(mContext.getString(R.string.count_not_null));
            return;
        }
        if (TextUtils.isEmpty(userPassWord.get())) {
            Toasty.showError(mContext.getString(R.string.pwd_not_null));
            return;
        }

        App.getInstance().deleteCurrentUser();
        showDialog(mContext.getString(R.string.loading));
        if (RegexUtils.isEmail(userName.get().trim())) {
            loginType = 1;
            CasClient.getInstance().login(userName.get(), userPassWord.get(), "true");
        } else {
            loginType = 0;
            CasClient.getInstance().login(strAreaCode + userName.get(), userPassWord.get(), "true");
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
//
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //获取国籍列表
            case EvKey.findSupportCountry:
                dismissDialog();
                if (eventBean.isStatue()) {
                    final List<CountryEntity> objList = (List<CountryEntity>) eventBean.getObject();
                    if (!objList.isEmpty()) {
//                        mCountryEntityList = objList;
//                        mCountryArray = new String[objList.size()];
//                        for (int i = 0; i < objList.size(); i++) {
//                            mCountryArray[i] = objList.get(i).getZhName();
//                        }
//                        if (mCountryArray.length > 0) {
                        if (choiceOfNationalityPopup == null) {
                            choiceOfNationalityPopup = new ChoiceOfNationalityPopup(mContext, objList, new NationalChoiceListener() {

                                @Override
                                public void onClickItem(int position, List<CountryEntity2> countryEntity2) {
                                    switch (LanguageSPUtil.getInstance(App.getInstance()).getSelectLanguage()) {
                                        case 1://中文
                                            updateCountryInfo(countryEntity2.get(position).getZhName() + " +" + countryEntity2.get(position).getAreaCode(), countryEntity2.get(position).getAreaCode());
                                            break;
                                        case 0://英文
                                            updateCountryInfo(countryEntity2.get(position).getEnName() + " +" + countryEntity2.get(position).getAreaCode(), countryEntity2.get(position).getAreaCode());
                                            break;
                                        default:
                                            updateCountryInfo(countryEntity2.get(position).getZhName() + " +" + countryEntity2.get(position).getAreaCode(), countryEntity2.get(position).getAreaCode());
                                            break;
                                    }

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
            case EvKey.login:
                if (eventBean.isStatue()) {
                    isCasLogin = true;
                    if (eventBean.getMessage().equals("1")) {
                        CasClient.getInstance().sendVertifyCode(loginType);
                    } else {
                        CasClient.getInstance().getServiceTicket(SPUtils.getInstance().getTgc(), BaseHost.TYPE_UC);
                    }
                } else {
                    dealError(eventBean);
                }
                break;
            case EvKey.getServiceTicket:
                if (!eventBean.isStatue()) {
                    dealError(eventBean);
                }
                break;
            case EvKey.loginApp:
                if (eventBean.isStatue()) {
//                    saveOtcSid();
                    isCasLogin = false;
                    //获取用户信息
                    MemberClient.getInstance().userInfo();
                } else {
                    dealError(eventBean);
                }
                break;
            case EvKey.geeCaptcha:
                dismissDialog();
                if (eventBean.isStatue()) {
                    gt3GeetestUtils.gtSetApi1Json((JSONObject) eventBean.getObject());
                    gt3GeetestUtils.getGeetest(mContext, BaseHost.LOGIN_HOST + CasHost.vertifyCodeUrl, null, null, new GT3GeetestBindListener() {
                        @Override
                        public boolean gt3SetIsCustom() {
                            return true;
                        }

                        @Override
                        public void gt3GetDialogResult(boolean status, String result) {
                            if (status) {
                                Captcha captcha = new Gson().fromJson(result, Captcha.class);
                                String checkData = "gee::" + captcha.getGeetest_challenge() + "$" + captcha.getGeetest_validate() + "$" + captcha.getGeetest_seccode();
                                CasClient.getInstance().checkGeeCaptcha(checkData, cid);
                            }
                        }
                    });
                    gt3GeetestUtils.setDialogTouch(true);
                } else {
                    CheckErrorUtil.checkError(eventBean);
                }
                break;
            case EvKey.checkCaptcha:
                if (eventBean.isStatue()) {
                    if (gt3GeetestUtils != null) {
                        gt3GeetestUtils.gt3TestFinish();
                        gt3GeetestUtils = null;
                    }

                    showDialog(mContext.getString(R.string.loading));
                    if (isCasLogin) {
                        CasClient.getInstance().getServiceTicket(SPUtils.getInstance().getTgc(), BaseHost.TYPE_UC);
                    } else {
                        login();
                    }
                } else {
                    CheckErrorUtil.checkError(eventBean);
                }
                break;
            case EvKey.sendVertifyCode:
                dismissDialog();
                if (eventBean.isStatue()) {
                    if (gt3GeetestUtils != null) {
                        gt3GeetestUtils.gt3TestFinish();
                        gt3GeetestUtils = null;
                    }
                    showPhoneVertifyDialog();
                } else {
                    if (eventBean.getCode() == BaseRequestCode.ERROR_500) {
                        Toasty.showError(mContext.getString(R.string.sms_get_error));
                    } else {
                        CheckErrorUtil.checkError(eventBean);
                    }
                }
                break;
            case EvKey.phoneCodeCheck:
                if (eventBean.isStatue()) {
                    if (isCasLogin) {
                        CasClient.getInstance().getServiceTicket(SPUtils.getInstance().getTgc(), BaseHost.TYPE_UC);
                    } else {
                        login();
                    }
                } else {
                    dismissDialog();
                    CheckErrorUtil.checkError(eventBean);
                }
                break;
            case EvKey.userInfo:
                if (eventBean.isStatue()) {
                    User user = (User) eventBean.getObject();
                    user.setLogintype(loginType);
                    if (user != null) {
                        App.getInstance().setCurrentUser(user);
                    }
                    SpotCoinClient.getInstance().getFavorFind();
                } else {
                    dealError(eventBean);
                }
                break;
            //获取全部收藏币种
            case EvKey.favorFind:
                dismissDialog();
                if (eventBean.isStatue()) {
                    FavorFindResult favorFindResult = (FavorFindResult) eventBean.getObject();
                    List<String> favorFindList = new ArrayList<>();
                    for (FavorFindResult.DataBean dataBean : favorFindResult.getData()) {
                        favorFindList.add(dataBean.getSymbol());
                    }
                    SPUtils.getInstance().setFavorFindList(favorFindList);
                }

                for (Cookie cookie : App.getInstance().getCookieManger().getCookieStore().getCookies()) {
                    LogUtils.e("cookie", cookie.name() + "-- " + cookie.value());
                    switch (cookie.name()) {
                        case "acsid":
                            SPUtils.getInstance().setAcCookie(cookie.value());
                            break;
                        case "ucsid":
                            SPUtils.getInstance().setUcCookie(cookie.value());
                            break;
                        case "cfdsid":
                            SPUtils.getInstance().setCfdCookie(cookie.value());
                            break;
                        case "otcsid":
                            SPUtils.getInstance().setOtcCookie(cookie.value());
                            break;
                        case "spotsid":
                            SPUtils.getInstance().setSpotCookie(cookie.value());
                            break;
                    }
                }
                EventBusUtils.postSuccessEvent(EvKey.loginStatue, BaseRequestCode.OK, "");
                Toasty.showSuccess(mContext.getString(R.string.login_success));
                finish();
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

    private void dealError(EventBean eventBean) {
        int code = eventBean.getCode();
        if (code == BaseRequestCode.ERROR_401 && !isCasLogin) {
            dismissDialog();
            Toasty.showError(mContext.getString(R.string.usernameorpwderror));
        } else if (code == BaseRequestCode.ERROR_411) {
            BaseResponseError responseError = (BaseResponseError) eventBean.getObject();
            if (responseError != null) {
                String data = responseError.getData();
                cid = responseError.getCid();
                if (StringUtils.isNotEmpty(data) && "phone".equals(data)) {
                    dismissDialog();
                    CasClient.getInstance().sendVertifyCode(loginType);
                } else if (StringUtils.isNotEmpty(data) && "gee".equals(data)) {
                    LogUtils.e("sss", "geeeeee");
                    gt3GeetestUtils = new GT3GeetestUtilsBind(mContext);
                    CasClient.getInstance().geeCaptcha();
                } else if (StringUtils.isNotEmpty(responseError.getMessage())) {
                    dismissDialog();
                    Toasty.showError(responseError.getMessage());
                }
            }
        } else {
            CheckErrorUtil.checkError(eventBean);
        }
    }

    private void initSmsVerifyPopup() {
        if (mSmsVerifyPopup == null) {
            mSmsVerifyPopup = new SmsVerifyPopup(mContext, loginType, true, strAreaCode + userName.get(), new OnEtContentListener() {
                @Override
                public void onCEtContentInput(String content) {
                    showDialog(mContext.getString(R.string.loading));
                    CasClient.getInstance().phoneCodeCheck(loginType, content);
                }
            });
        }
    }

    /**
     * 展示发送短信验证码弹框
     */
    private void showPhoneVertifyDialog() {
        if (mSmsVerifyPopup.isShow()) {
            return;
        }
        mSmsVerifyPopup.updatePhone(loginType == 0 ? strAreaCode + userName.get() : userName.get());
        mSmsVerifyPopup.updateLoginType(loginType);
        new XPopup.Builder(mContext)
                .dismissOnBackPressed(false) // 按返回键是否关闭弹窗，默认为true
                .dismissOnTouchOutside(false) // 点击外部是否关闭弹窗，默认为true
                .enableDrag(false)//不能下滑关闭
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
                .asCustom(mSmsVerifyPopup).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.isLoginVisiable = true;
        EventBusUtils.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Constant.isLoginVisiable = false;
        EventBusUtils.unRegister(this);
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Constant.isLoginVisiable = true;
//        EventBusUtils.register(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Constant.isLoginVisiable = false;
//        EventBusUtils.unRegister(this);
//    }


}
