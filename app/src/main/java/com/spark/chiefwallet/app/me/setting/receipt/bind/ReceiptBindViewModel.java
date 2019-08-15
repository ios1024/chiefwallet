package com.spark.chiefwallet.app.me.setting.receipt.bind;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.PayControlClient;
import com.spark.otcclient.pojo.PayListBean;
import com.spark.otcclient.pojo.PayTypeAddBean;
import com.spark.otcclient.pojo.UploadPicResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
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
public class ReceiptBindViewModel extends BaseViewModel {
    private Context mContext;
    private String type;
    private String[] bankList = App.getInstance().getApplicationContext().getResources().getStringArray(R.array.bank_list);
    private PayTypeAddBean payTypeAddBean = new PayTypeAddBean();
    private int updateTypeId;

    public ReceiptBindViewModel(@NonNull Application application) {
        super(application);
    }

    //标题
    public ObservableField<String> titleText = new ObservableField<>("");
    //姓名
    public ObservableField<String> nameText = new ObservableField<>("");
    //支付宝/微信 号
    public ObservableField<String> typeText = new ObservableField<>("");
    public ObservableField<String> typeHintText = new ObservableField<>("");
    //支付宝/微信 地址
    public ObservableField<String> typeAddressText = new ObservableField<>("");
    //银行名称
    public ObservableField<String> bankText = new ObservableField<>("");
    //支行名称
    public ObservableField<String> branchText = new ObservableField<>("");
    //银行卡号
    public ObservableField<String> bankAddressText = new ObservableField<>("");
    //资金密码
    public ObservableField<String> pwdText = new ObservableField<>("");
    public ObservableField<Boolean> typeVisiable = new ObservableField<>(true);
    public ObservableField<String> bottomText = new ObservableField<>("");
    //二维码URL
    public ObservableField<String> qrCodeUrl = new ObservableField<>("");
    public ObservableField<Boolean> qrCodeVisiable = new ObservableField<>(true);

    public BindingCommand chooseBankList = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            new XPopup.Builder(mContext)
                    .maxHeight((int) (XPopupUtils.getWindowHeight(mContext) * .85f))
                    .asBottomList("请选择银行", bankList,
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    bankText.set(text);
                                }
                            })
                    .show();
        }
    });


    public BindingCommand submitCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            switch (bottomText.get()) {
                case "绑定":
                    bind();
                    break;
                case "重置":
                    update();
                    break;
            }
        }
    });


    public void initType(Context context, String type, PayListBean.DataBean typeBean) {
        this.mContext = context;
        this.type = type;

        if (typeBean == null) {
            switch (type) {
                case Constant.alipay:
                    titleText.set("绑定支付宝收款");
                    typeText.set("支付宝账号");
                    typeHintText.set("请输入支付宝账号");
                    break;
                case Constant.wechat:
                    titleText.set("绑定微信收款");
                    typeText.set("微信账号");
                    typeHintText.set("请输入微信账号");
                    break;
                case Constant.card:
                    titleText.set("绑定银行卡收款");
                    typeVisiable.set(false);
                    qrCodeVisiable.set(false);
                    break;
                case Constant.PAYPAL:
                    titleText.set("绑定PayPal收款");
                    typeText.set("PayPal账号");
                    typeHintText.set("请输入PayPal账号");
                    qrCodeVisiable.set(false);
                    break;
                case Constant.other:
                    titleText.set("绑定其他收款");
                    typeText.set("其他账号");
                    typeHintText.set("请输入其他账号");
                    qrCodeVisiable.set(false);
                    break;
            }
            bottomText.set("绑定");
        } else {
            if (StringUtils.isEmpty(typeBean.getRealName())) {
                nameText.set(App.getInstance().getCurrentUser().getRealName());
            } else {
                nameText.set(typeBean.getRealName());
            }
            switch (typeBean.getPayType()) {
                case Constant.alipay:
                    titleText.set("编辑支付宝收款");
                    typeText.set("支付宝账号");
                    typeHintText.set("请输入支付宝账号");
                    typeAddressText.set(typeBean.getPayAddress());
                    qrCodeUrl.set(typeBean.getQrCodeUrl());
                    break;
                case Constant.wechat:
                    titleText.set("编辑微信收款");
                    typeText.set("微信账号");
                    typeHintText.set("请输入微信账号");
                    typeAddressText.set(typeBean.getPayAddress());
                    qrCodeUrl.set(typeBean.getQrCodeUrl());
                    break;
                case Constant.card:
                    titleText.set("编辑绑定银行卡收款");
                    typeVisiable.set(false);
                    qrCodeVisiable.set(false);
                    bankText.set(typeBean.getBank());
                    branchText.set(typeBean.getBranch());
                    bankAddressText.set(typeBean.getPayAddress());
                    break;
                case Constant.PAYPAL:
                    titleText.set("编辑PayPal收款");
                    typeText.set("PayPal账号");
                    typeHintText.set("请输入PayPal账号");
                    typeAddressText.set(typeBean.getPayAddress());
                    qrCodeVisiable.set(false);
                    break;
                case Constant.other:
                    titleText.set("编辑其他收款");
                    typeText.set("其他账号");
                    typeHintText.set("请输入其他账号");
                    typeAddressText.set(typeBean.getPayAddress());
                    qrCodeVisiable.set(false);
                    break;
            }
            updateTypeId = typeBean.getId();
            bottomText.set("重置");
        }
    }

    /**
     * 绑定
     */
    private void bind() {
        if (verify()) {
            showDialog("正在绑定");
            PayControlClient.getInstance().payTypeAdd(payTypeAddBean);
        }
    }

    /**
     * 重置
     */
    private void update() {
        if (verify()) {
            showDialog("正在重置");
            PayControlClient.getInstance().payTypeUpdate(updateTypeId, payTypeAddBean);
        }
    }

    /**
     * 表单校验
     */
    private boolean verify() {
        boolean isVerifySuccess = false;
        if (StringUtils.isEmpty(nameText.get())) {
            Toasty.showError("请输入姓名！");
            return isVerifySuccess;
        }
        payTypeAddBean.setRealName(nameText.get().trim());
        switch (type) {
            case Constant.alipay:
                if (StringUtils.isEmpty(typeAddressText.get())) {
                    Toasty.showError("请输入支付宝账号！");
                } else {
                    if (StringUtils.isEmpty(qrCodeUrl.get())) {
                        Toasty.showError("请上传收款二维码！");
                    } else if (StringUtils.isEmpty(pwdText.get())) {
                        Toasty.showError("请输入资金密码！");
                    } else {
                        payTypeAddBean.setPayAddress(typeAddressText.get().trim());
                        payTypeAddBean.setQrCodeUrl(qrCodeUrl.get());
                        payTypeAddBean.setTradePwd(pwdText.get().trim());
                        payTypeAddBean.setPayType(type);
                        isVerifySuccess = true;
                    }
                }
                break;
            case Constant.wechat:
                if (StringUtils.isEmpty(typeAddressText.get())) {
                    Toasty.showError("请输入微信账号！");
                } else if (StringUtils.isEmpty(qrCodeUrl.get())) {
                    Toasty.showError("请上传收款二维码！");
                } else if (StringUtils.isEmpty(pwdText.get())) {
                    Toasty.showError("请输入资金密码！");
                } else {
                    payTypeAddBean.setPayAddress(typeAddressText.get().trim());
                    payTypeAddBean.setQrCodeUrl(qrCodeUrl.get());
                    payTypeAddBean.setTradePwd(pwdText.get().trim());
                    payTypeAddBean.setPayType(type);
                    isVerifySuccess = true;
                }
                break;
            case Constant.card:
                if (StringUtils.isEmpty(bankText.get())) {
                    Toasty.showError("请选择开户行！");
                } else if (StringUtils.isEmpty(branchText.get())) {
                    Toasty.showError("请输入开户行支行！");
                } else if (StringUtils.isEmpty(bankAddressText.get())) {
                    Toasty.showError("请输入用户卡号！");
                } else if (StringUtils.isEmpty(pwdText.get())) {
                    Toasty.showError("请输入资金密码！");
                } else {
                    payTypeAddBean.setBank(bankText.get());
                    payTypeAddBean.setBranch(branchText.get());
                    payTypeAddBean.setPayAddress(bankAddressText.get().trim());
                    payTypeAddBean.setTradePwd(pwdText.get().trim());
                    payTypeAddBean.setPayType(type);
                    isVerifySuccess = true;
                }
                break;
            case Constant.PAYPAL:
                if (StringUtils.isEmpty(typeAddressText.get())) {
                    Toasty.showError("请输入PayPal账号！");
                } else {
                    if (StringUtils.isEmpty(pwdText.get())) {
                        Toasty.showError("请输入资金密码！");
                    } else {
                        payTypeAddBean.setPayAddress(typeAddressText.get().trim());
                        payTypeAddBean.setTradePwd(pwdText.get().trim());
                        payTypeAddBean.setPayType(type);
                        isVerifySuccess = true;
                    }
                }
                break;
            case Constant.other:
                if (StringUtils.isEmpty(typeAddressText.get())) {
                    Toasty.showError("请输入其他账号！");
                } else {
                    if (StringUtils.isEmpty(pwdText.get())) {
                        Toasty.showError("请输入资金密码！");
                    } else {
                        payTypeAddBean.setPayAddress(typeAddressText.get().trim());
                        payTypeAddBean.setTradePwd(pwdText.get().trim());
                        payTypeAddBean.setPayType(type);
                        isVerifySuccess = true;
                    }
                }
                break;
        }
        return isVerifySuccess;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //上传图片
            case EvKey.uploadQRCode:
                dismissDialog();
                if (eventBean.isStatue()) {
                    qrCodeUrl.set(((UploadPicResult) eventBean.getObject()).getData());
                    Toasty.showSuccess("上传成功！");
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //添加支付方式
            case EvKey.payTypeAdd:
                dismissDialog();
                if (eventBean.isStatue()) {
                    Toasty.showSuccess(bottomText.get() + "成功！");
                    finish();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //修改支付方式
            case EvKey.payTypeUpdate:
                dismissDialog();
                if (eventBean.isStatue()) {
                    Toasty.showSuccess(bottomText.get() + "成功！");
                    finish();
                } else {
                    Toasty.showError(eventBean.getMessage());
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
