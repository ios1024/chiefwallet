package com.spark.chiefwallet.app.me.safe.business;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupCallback;
import com.spark.casclient.CasClient;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.user.login.LoginViewModel;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.dialog.AdsSelectDialog;
import com.spark.chiefwallet.ui.popup.AuthenticationmethodPopup;
import com.spark.chiefwallet.ui.popup.ChoiceOfNationalityPopup;
import com.spark.chiefwallet.ui.popup.SmsVerifyPopup;
import com.spark.chiefwallet.ui.popup.impl.OnEtContentListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.pojo.AdvertiseCoinListResult;
import com.spark.otcclient.pojo.AdvertiseCoinResult;
import com.spark.otcclient.pojo.AuthMerchantResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

public class CertificationDetailsViewModel extends BaseViewModel {

    private int determine = 0;
    public ObservableField<String> tvbushiness = new ObservableField<>("");
    public ObservableField<String> tvstatustips = new ObservableField<>("");
    public ObservableField<String> tvcontentofapplication = new ObservableField<>("");
    public ObservableField<String> tvapply = new ObservableField<>("");
    public ObservableField<Boolean> certificationtype1 = new ObservableField<>(false);//type1
    public ObservableField<Boolean> certificationtype2 = new ObservableField<>(false);//type2
    public ObservableField<Boolean> businesswaitfor = new ObservableField<>(false);//type2
    public ObservableField<Boolean> businesssuccess = new ObservableField<>(false);//type2
    public ObservableField<Boolean> businessfail = new ObservableField<>(false);//type2

    private AuthenticationmethodPopup authenticationmethodPopup;
    private Context mContext;
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //密码开关观察者
        public SingleLiveEvent<Boolean> authentCodePopopShow = new SingleLiveEvent<>();
        public SingleLiveEvent<AdvertiseCoinResult> mbusMoney = new SingleLiveEvent<>();
        public SingleLiveEvent<String> TitleName = new SingleLiveEvent<>();
    }

    public CertificationDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public void initContext(Context context) {
        this.mContext = context;
        initSmsVerifyPopup();
    }

    //选择认证方式
    public BindingCommand conmOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            new XPopup.Builder(mContext)
                    .setPopupCallback(new XPopupCallback() {
                        @Override
                        public void onShow() {
                            uc.authentCodePopopShow.setValue(true);
                        }

                        @Override
                        public void onDismiss() {
                            uc.authentCodePopopShow.setValue(false);
                        }

                    })
                    .asCustom(authenticationmethodPopup).show();
        }
    });

    public BindingCommand applyOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (determine == 2) {
                certificationtype1.set(true);
                certificationtype2.set(false);
            } else if (determine == 3 || determine == 1) {//退保
                ARouter.getInstance().build(ARouterPath.ACTIVITY_BUSINESS_SURREND)
                        .navigation();
            } else if (determine == 6) {
                businesswaitfor.set(false);
                businesssuccess.set(true);
                businessfail.set(false);
                tvstatustips.set(mContext.getString(R.string.tvstatustipssuccess));
                tvcontentofapplication.set(mContext.getString(R.string.tvcontentofapplicationsuccess));
                tvapply.set(mContext.getString(R.string.publish_advertisements2));
                determine = 1;//申请退保
            } else
                finish();


        }
    });

    private void initSmsVerifyPopup() {
        if (authenticationmethodPopup == null) {
            authenticationmethodPopup = new AuthenticationmethodPopup(mContext);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //获取保证金
            case EvKey.authmerchanttype:
                if (eventBean.isStatue()) {
                    AdvertiseCoinResult advertiseCoinListResult = (AdvertiseCoinResult) eventBean.getObject();
                    uc.mbusMoney.setValue(advertiseCoinListResult);
                } else {
                    Toasty.showError("获取保证金失败");
                    finish();
                }
                break;
            case EvKey.authMerchantFind:
                dismissDialog();
                if (eventBean.isStatue()) {
                    uc.TitleName.setValue(mContext.getString(R.string.application_of_certifie_business));
                    certificationtype1.set(true);
                    certificationtype2.set(false);
                    AuthMerchantResult authMerchantResult = (AuthMerchantResult) eventBean.getObject();
                    int certifiedBusinessStatus = authMerchantResult.getData().getCertifiedBusinessStatus();
                    // 认证状态 0：未认证 1：认证-待审核 2：认证-审核成功 3：认证-审核失败 5：退保-待审核 6：退保-审核失败 7:退保-审核成功
                    tvapply.set(mContext.getString(R.string.ensure));
                    if (certifiedBusinessStatus == 1 || certifiedBusinessStatus == 2 || certifiedBusinessStatus == 3 || certifiedBusinessStatus == 5 || certifiedBusinessStatus == 6 || certifiedBusinessStatus == 7) {
                        uc.TitleName.setValue(mContext.getString(R.string.str_apply_type));
                        certificationtype1.set(false);
                        certificationtype2.set(true);
                    }


                    if (certifiedBusinessStatus == 1) {
                        businesswaitfor.set(true);
                        businesssuccess.set(false);
                        businessfail.set(false);
                        tvstatustips.set(mContext.getString(R.string.tvstatustips));
                        tvcontentofapplication.set(mContext.getString(R.string.tvcontentofapplication));
                        determine = 0;//关闭
                    } else if (certifiedBusinessStatus == 2) {
                        businesswaitfor.set(false);
                        businesssuccess.set(true);
                        businessfail.set(false);
                        tvstatustips.set(mContext.getString(R.string.tvstatustipssuccess));
                        tvcontentofapplication.set(mContext.getString(R.string.tvcontentofapplicationsuccess));
                        tvapply.set(mContext.getString(R.string.publish_advertisements2));
                        determine = 1;//申请退保

                    } else if (certifiedBusinessStatus == 3) {
                        businesswaitfor.set(false);
                        businesssuccess.set(false);
                        businessfail.set(true);
                        tvstatustips.set(mContext.getString(R.string.audit_failure1));

                        tvcontentofapplication.set(authMerchantResult.getData().getReason());//失败原因
                        tvapply.set(mContext.getString(R.string.resubmit));
                        determine = 2;//重新提交

                    } else if (certifiedBusinessStatus == 5) {

                        businesswaitfor.set(true);
                        businesssuccess.set(false);
                        businessfail.set(false);
                        tvstatustips.set(mContext.getString(R.string.tvstatustips2));
                        tvcontentofapplication.set(mContext.getString(R.string.tvcontentofapplication2));
                        determine = 0;//关闭
                    } else if (certifiedBusinessStatus == 6) {

                        businesswaitfor.set(false);
                        businesssuccess.set(false);
                        businessfail.set(true);
                        tvstatustips.set(mContext.getString(R.string.audit_failure2));
                        tvcontentofapplication.set(authMerchantResult.getData().getReason());//失败原因
                        tvapply.set(mContext.getString(R.string.resubmit));
                        determine = 3;//商家退保重新申请
                    } else if (certifiedBusinessStatus == 7) {//退保审核通过
                        businesswaitfor.set(false);
                        businesssuccess.set(true);
                        businessfail.set(false);
                        tvstatustips.set(mContext.getString(R.string.tvstatustipssuccess2));
                        tvcontentofapplication.set("");
                        tvapply.set(mContext.getString(R.string.re_application));
                        determine = 2;

                    }
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegister(this);
    }

}
