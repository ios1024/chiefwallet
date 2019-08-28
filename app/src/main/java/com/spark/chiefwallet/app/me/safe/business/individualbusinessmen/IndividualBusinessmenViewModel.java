package com.spark.chiefwallet.app.me.safe.business.individualbusinessmen;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.me.safe.business.CertificationDetailsViewModel;
import com.spark.chiefwallet.ui.dialog.AdsSelectDialog;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.AdvertiseSelfClient;
import com.spark.otcclient.LcTradeClient;
import com.spark.otcclient.pojo.AdvertiseCoinResult;
import com.spark.otcclient.pojo.UploadPicResult;
import com.spark.ucclient.SecurityClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;

public class IndividualBusinessmenViewModel extends BaseViewModel {
    public IndividualBusinessmenViewModel(@NonNull Application application) {
        super(application);
    }

    private int mbondtype;
    private String coin;
    private double cmount;
    private Context mContext;
    public ObservableField<String> selecttv = new ObservableField<>();
    public ObservableField<String> nametv = new ObservableField<>();
    public ObservableField<String> phonetv = new ObservableField<>();
    public ObservableField<String> mailboxtv = new ObservableField<>();
    public ObservableField<String> wechattv = new ObservableField<>();
    public ObservableField<String> whatsapptv = new ObservableField<>();
    public ObservableField<String> emergencytv = new ObservableField<>();
    public ObservableField<String> emergencyphonetv = new ObservableField<>();
    public ObservableField<String> emergaddresstv = new ObservableField<>();
    public ObservableField<String> qrCodeUrl = new ObservableField<>("");
    public ObservableField<Boolean> hiddenpart = new ObservableField<>(true);
    public ObservableField<String> tiaolie = new ObservableField<>("0");

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //密码开关观察者
        public SingleLiveEvent<AdvertiseCoinResult> mbusMoney = new SingleLiveEvent<>();
    }

    //选择关系
    public BindingCommand concernclink = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            showSelecttType();

        }
    });

    public void bondtype(int mybondtype) {
        this.mbondtype = mybondtype;
    }

    //提交申请
    public BindingCommand applicationsubmissionClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (TextUtils.isEmpty(nametv.get())) {
                Toasty.showError(mContext.getString(R.string.name_hint));
                return;
            }
            if (TextUtils.isEmpty(phonetv.get())) {
                Toasty.showError(mContext.getString(R.string.phone_num_hint2));
                return;
            }
            if (TextUtils.isEmpty(mailboxtv.get())) {
                Toasty.showError(mContext.getString(R.string.email_num_hint2));
                return;
            }
            if (TextUtils.isEmpty(emergencytv.get())) {
                Toasty.showError(mContext.getString(R.string.emergency_contact2));
                return;
            }
            if (TextUtils.isEmpty(emergencyphonetv.get())) {
                Toasty.showError(mContext.getString(R.string.emergency_contact_phone2));
                return;
            }
            if (TextUtils.isEmpty(selecttv.get())) {
                Toasty.showError(mContext.getString(R.string.str_emerg_my_concern2));
                return;
            }
            if (TextUtils.isEmpty(emergaddresstv.get())) {
                Toasty.showError(mContext.getString(R.string.str_emerg_address2));
                return;
            }
            if (mbondtype == 1) {
                if (TextUtils.isEmpty(qrCodeUrl.get())) {
                    Toasty.showError(mContext.getString(R.string.business_license));
                    return;
                }
                if (tiaolie.get().equals("0")) {
                    Toasty.showError(mContext.getString(R.string.please_check_freeze));
                    return;
                }
            }

            //上传基本信息
            showDialog("正在提交");
            if (mbondtype == 0) {
                LcTradeClient.getInstance().getBusBasicApply(1, nametv.get(), phonetv.get(), mailboxtv.get(), emergencytv.get(), emergencyphonetv.get(), selecttv.get(), emergaddresstv.get(), "", "", "", "");
            } else
                LcTradeClient.getInstance().getBusBasicApply(2, nametv.get(), phonetv.get(), mailboxtv.get(), emergencytv.get(), emergencyphonetv.get(), selecttv.get(), emergaddresstv.get(), String.valueOf(cmount), coin, qrCodeUrl.get(), "BTC");

        }
    });

    public void myContext(Context context) {
        this.mContext = context;
    }

    private AdsSelectDialog mSelectDialog;

    private void showSelecttType() {
        if (mSelectDialog == null) {
            mSelectDialog = new AdsSelectDialog(mContext);
            mSelectDialog.setTitle(mContext.getString(R.string.str_emerg_one), mContext.getString(R.string.str_emerg_two), mContext.getString(R.string.str_emerg_three));
        }
        mSelectDialog.setInterface(new AdsSelectDialog.LoginSelectInterface() {
            @Override
            public void onSelectType(int type) {
                if (type == 1) {
                    selecttv.set(mContext.getString(R.string.str_emerg_one));
                } else if (type == 2) {
                    selecttv.set(mContext.getString(R.string.str_emerg_two));
                } else if (type == 3) {
                    selecttv.set(mContext.getString(R.string.str_emerg_three));
                }
            }
        });
        mSelectDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //上传图片
            case EvKey.uploadQRCode:
                dismissDialog();
                if (eventBean.isStatue()) {
                    qrCodeUrl.set(((UploadPicResult) eventBean.getObject()).getData());
                    hiddenpart.set(false);
                    Toasty.showSuccess(App.getInstance().getApplicationContext().getString(R.string.upload_success));
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;

            case EvKey.businesstype:
                if (eventBean.isStatue()) {
                    Toasty.showSuccess(eventBean.getMessage());
                    showDialog("请求中");
                    LcTradeClient.getInstance().getBusinessFind();
                    finish();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //获取保证金
            case EvKey.authmerchanttype:
                dismissDialog();
                if (eventBean.isStatue()) {
                    AdvertiseCoinResult advertiseCoinListResult = (AdvertiseCoinResult) eventBean.getObject();
                    coin = advertiseCoinListResult.getData().getCoinName();
                    cmount = advertiseCoinListResult.getData().getAmount();
                    uc.mbusMoney.setValue(advertiseCoinListResult);
                } else {
                    Toasty.showError("获取保证金失败");
                    finish();
                }
                break;
            case EvKey.logout_success_401:
                if (eventBean.isStatue()) {
                    dismissDialog();
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
