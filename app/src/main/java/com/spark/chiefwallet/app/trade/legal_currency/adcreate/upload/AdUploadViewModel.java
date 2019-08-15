package com.spark.chiefwallet.app.trade.legal_currency.adcreate.upload;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.AdvertiseScanClient;
import com.spark.otcclient.AdvertiseSelfClient;
import com.spark.otcclient.pojo.AdCreateBean;
import com.spark.otcclient.pojo.AdSelfDownFindResult;
import com.spark.otcclient.pojo.AdvertiseCoinListResult;
import com.spark.otcclient.pojo.FindPriceResult;
import com.spark.otcclient.pojo.PayListBean;
import com.spark.otcclient.pojo.TradeAreaListResult;
import com.spark.otcclient.pojo.TradeCoinListResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/27
 * 描    述：发布或编辑广告
 * 修订历史：
 * ================================================
 */
public class AdUploadViewModel extends BaseViewModel {
    public AdUploadViewModel(@NonNull Application application) {
        super(application);
    }

    private Context mContext;
    private int adType;//广告类型 0-买入 1-卖出
    private String[] coinAddressArray;
    private TradeAreaListResult tradeAreaListResult;
    private List<Integer> payTypeList = new ArrayList<>();

    public ObservableField<String> coinName = new ObservableField<>("");
    public ObservableField<String> coinAddress = new ObservableField<>("");
    public ObservableField<String> coinType = new ObservableField<>("");
    public ObservableField<Boolean> isFixedPrice = new ObservableField<>(false);
    public ObservableField<String> fixedPrice = new ObservableField<>("");
    public ObservableField<String> fixedPriceTips = new ObservableField<>("浮动比例是指以当前市场价格多少百分比进行交易");
    public ObservableField<String> premiumPrice = new ObservableField<>("");
    public ObservableField<String> premiumPriceTips = new ObservableField<>("启用后，您的币价不会随市场波动，价格不变");
    public ObservableField<String> tradePrice = new ObservableField<>("");
    public ObservableField<String> tradeNumber = new ObservableField<>("");
    public ObservableField<String> payMode = new ObservableField<>("");
    public ObservableField<String> tradeDeadline = new ObservableField<>("");
    public ObservableField<String> minAmount = new ObservableField<>("");
    public ObservableField<String> maxAmount = new ObservableField<>("");
    public ObservableField<String> remarks = new ObservableField<>("");
    public ObservableField<Boolean> isAutoResponse = new ObservableField<>(false);
    public ObservableField<String> autoResponse = new ObservableField<>("");
    public ObservableField<String> tradeNumberHint = new ObservableField<>("请输入买入数量");
    public ObservableField<String> adTypeTag = new ObservableField<>("买入数量");
    public ObservableField<String> submitBtn = new ObservableField<>("发布");


    public double ratePrice;
    public String coinAddressStr;
    private AdvertiseCoinListResult advertiseCoinListResult;
    private AdvertiseCoinListResult.DataBean coinInfo;
    public ArrayList<PayListBean.DataBean> mSelectList = new ArrayList<>();//选中的收款方式列表
    private String payIds = "";
    private AdSelfDownFindResult.DataBean.RecordsBean ads;

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> isRefresh = new SingleLiveEvent<>();
    }

    public void initContext(Context context, int adType, AdSelfDownFindResult.DataBean.RecordsBean ads) {
        this.mContext = context;
        this.adType = adType;
        if (adType == 0) {
            adTypeTag.set("买入数量");
            tradeNumberHint.set("请输入买入数量");
        } else {
            adTypeTag.set("卖出数量");
            tradeNumberHint.set("请输入卖出数量");
        }
        this.ads = ads;
        if (ads != null) {
            submitBtn.set("修改");
            payIds = ads.getPayIds();
        } else {
            submitBtn.set("发布");
        }
    }

    //广告交易币种信息
    public void getTradeCoinList() {
        //查询所有的币种
        if (Constant.lcCoinNameArray == null) {
            AdvertiseScanClient.getInstance().getTradeCoinList();
        }
        //广告交易币种信息
        AdvertiseSelfClient.getInstance().getTradeCoinList(adType);
        if (ads != null) {
            AdvertiseScanClient.getInstance().priceFind(ads.getCoinName(), "CNY", adType);
        }
    }

    //选择币种
    public BindingCommand chooseCoinCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            new XPopup.Builder(mContext)
                    .asBottomList("请选择币种", Constant.lcCoinNameArray,
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    if (advertiseCoinListResult != null) {
                                        for (AdvertiseCoinListResult.DataBean dataBean : advertiseCoinListResult.getData()) {
                                            if (text.equals(dataBean.getCoinName())) {
                                                coinInfo = dataBean;
                                            }
                                        }
                                    }
                                    if (coinInfo != null) {
                                        tradeNumberHint.set("大于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMinLimit() + "") + " 且 " + "小于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMaxLimit() + ""));
                                    }
                                    coinName.set(text);
                                    AdvertiseScanClient.getInstance().priceFind(text, "CNY", adType);
                                }
                            })
                    .show();
        }
    });
    //所在地
    public BindingCommand chooseCoinAddressCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (coinAddressArray != null) {
                new XPopup.Builder(mContext)
                        .asBottomList("请选择所在地", coinAddressArray,
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        coinAddress.set(text);
                                        coinAddressStr = tradeAreaListResult.getData().get(position).getEnName();
                                        coinType.set(tradeAreaListResult.getData().get(position).getLocalCurrency());
                                    }
                                })
                        .show();
            } else {
                showDialog("正在请求...");
                AdvertiseScanClient.getInstance().getTradeAreaList(adType);
            }
        }
    });
    //货币类型
    public BindingCommand chooseCoinTypeCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
        }
    });

    //发布
    public BindingCommand submitCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            submit();
        }
    });

    private void submit() {
        if (StringUtils.isEmpty(coinName.get())) {
            Toasty.showError("请选择币种！");
            return;
        }
        if (StringUtils.isEmpty(coinAddress.get())) {
            Toasty.showError("请选择所在地！");
            return;
        }

        if (isFixedPrice.get()) {
            if (StringUtils.isEmpty(fixedPrice.get())) {
                Toasty.showError("请设置固定价格！");
                return;
            }
        } else {
            if (StringUtils.isEmpty(premiumPrice.get())) {
                Toasty.showError("请输入浮动比例！");
                return;
            }
        }
        if (StringUtils.isEmpty(tradeNumber.get())) {
            Toasty.showError("请输入买入数量！");
            return;
        }
        if (StringUtils.isEmpty(payMode.get())) {
            Toasty.showError("请选择收款方式！");
            return;
        }
        if (StringUtils.isEmpty(tradeDeadline.get())) {
            Toasty.showError("请输入付款期限（15~30）分钟！");
            return;
        }
        if (Double.valueOf(tradeDeadline.get()) > 30 || Double.valueOf(tradeDeadline.get()) < 15) {
            Toasty.showError("请输入付款期限（15~30）分钟！");
            return;
        }
        if (StringUtils.isEmpty(minAmount.get())) {
            Toasty.showError("请输入每笔交易最小限额！");
            return;
        }
        if (StringUtils.isEmpty(maxAmount.get())) {
            Toasty.showError("请输入每笔交易最大限额！");
            return;
        }

        if (coinInfo != null) {
            String count = tradeNumber.get();
            //String max = MathUtils.subZeroAndDot(coinInfo.getAdvMaxLimit() + "");
            String min = MathUtils.subZeroAndDot(coinInfo.getAdvMinLimit() + "");

            if (!StringUtils.isEmpty(count) && !StringUtils.isEmpty(min) && Double.valueOf(min) != 0) {
                if (Double.valueOf(count) < Double.valueOf(min)) {
                    if (adType == 0) {
                        Toasty.showError("买入数量" + "必须" + "大于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMinLimit() + "") + " 且 " + "小于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMaxLimit() + ""));
                    } else {
                        Toasty.showError("卖出数量" + "必须" + "大于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMinLimit() + "") + " 且 " + "小于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMaxLimit() + ""));
                    }
                    return;
                }
            }
        }

        AdCreateBean adCreateBean = new AdCreateBean();
        adCreateBean.setAdvertiseType(adType);
        adCreateBean.setAutoReply(!isAutoResponse.get() ? 0 : 1);
        adCreateBean.setAutoword(autoResponse.get());
        adCreateBean.setCoinName(coinName.get());
        adCreateBean.setCountry(coinAddressStr);
        adCreateBean.setMaxLimit(Double.valueOf(maxAmount.get()));
        adCreateBean.setMinLimit(Double.valueOf(minAmount.get()));
        adCreateBean.setNumber(Double.valueOf(tradeNumber.get()));
        adCreateBean.setPayMode(getPayByCode(payMode.get()));
        if (!isFixedPrice.get()) {
            adCreateBean.setPremiseRate(Double.valueOf(premiumPrice.get()) * 0.01);
        }
        adCreateBean.setPrice(Double.valueOf(tradePrice.get()));
        adCreateBean.setPriceType(isFixedPrice.get() ? 0 : 1);
        adCreateBean.setRemark(remarks.get());
        adCreateBean.setTimeLimit(Integer.valueOf(tradeDeadline.get()));
        adCreateBean.setPayIds(payIds);

        /* 广告商家类型 0 普通 1 商家 */
        adCreateBean.setTradeType(0);

        showDialog("正在发布...");
        if (ads == null) {
            AdvertiseScanClient.getInstance().createAd(adCreateBean, adType);
        } else {
            AdvertiseSelfClient.getInstance().updateAd(adCreateBean, adType, ads.getId());
        }
    }

    public void updatePayType(int type, String content) {
        if (!payTypeList.contains(type)) {
            payTypeList.add(type);
            payMode.set(payMode.get() + " " + content);
        }
    }

    public void updatePayType() {
        String content = "";
        payIds = "";
        for (PayListBean.DataBean payWay : mSelectList) {
            payIds = payIds + "," + payWay.getId();
            content = content + "," + getSetPayByCode(payWay.getPayType());
        }
        if (StringUtils.isEmpty(content)) {
            payMode.set("");
        } else {
            payMode.set(content.length() > 1 ? content.substring(content.indexOf(",") + 1) : content);
            payIds = payIds.length() > 1 ? payIds.substring(payIds.indexOf(",") + 1) : payIds;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //查询所有的币种
            case EvKey.tradeCoinList:
                if (eventBean.isStatue()) {
                    TradeCoinListResult tradeCoinListBean = (TradeCoinListResult) eventBean.getObject();
                    String[] mTitles = new String[tradeCoinListBean.getData().size()];
                    for (int i = 0; i < tradeCoinListBean.getData().size(); i++) {
                        mTitles[i] = tradeCoinListBean.getData().get(i).getCoinName();
                    }
                    Constant.lcCoinNameArray = mTitles;
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //查询所有的交易区
            case EvKey.tradeAreaList:
                if (eventBean.getType() != adType) return;
                dismissDialog();
                if (eventBean.isStatue()) {
                    tradeAreaListResult = (TradeAreaListResult) eventBean.getObject();
                    coinAddressArray = new String[tradeAreaListResult.getData().size()];
                    for (int i = 0; i < tradeAreaListResult.getData().size(); i++) {
                        coinAddressArray[i] = tradeAreaListResult.getData().get(i).getZhName();
                    }
                    new XPopup.Builder(mContext)
                            .asBottomList("请选择所在地", coinAddressArray,
                                    new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {
                                            coinAddress.set(text);
                                            coinAddressStr = tradeAreaListResult.getData().get(position).getEnName();
                                            coinType.set(tradeAreaListResult.getData().get(position).getLocalCurrency());
                                        }
                                    })
                            .show();
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //价格获取
            case EvKey.pirceFind:
                if (eventBean.getType() != adType) return;
                if (eventBean.isStatue()) {
                    FindPriceResult findPriceResult = (FindPriceResult) eventBean.getObject();
                    updatePrice(findPriceResult);
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //创建广告
            case EvKey.adCreate:
                if (eventBean.getType() != adType) return;
                dismissDialog();
                if (eventBean.isStatue()) {
                    /*Toasty.showSuccess("创建成功！");
                    finish();*/
                    new XPopup.Builder(mContext)
                            .dismissOnBackPressed(false)
                            .dismissOnTouchOutside(false)
                            .asConfirm("温馨提示", "操作成功，是否去上架？",
                                    "取消", "确定",
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            ARouter.getInstance().build(App.getInstance().isAppLogin() ? ARouterPath.ACTIVITY_ME_AD : ARouterPath.ACTIVITY_ME_LOGIN)
                                                    .withBoolean("adUp", true)
                                                    .navigation();
                                            finish();
                                        }
                                    }, new OnCancelListener() {
                                        @Override
                                        public void onCancel() {
                                            finish();
                                        }
                                    }, false)

                            .show();
                } else {
                    Toasty.showError(eventBean.getCode() + eventBean.getMessage());
                }
                break;
            //广告交易币种信息
            case EvKey.advertiseCoinList:
                if (eventBean.getType() != adType) return;
                dismissDialog();
                if (eventBean.isStatue()) {
                    advertiseCoinListResult = (AdvertiseCoinListResult) eventBean.getObject();
                    if (coinInfo == null && ads != null) {
                        for (AdvertiseCoinListResult.DataBean authMerchantApplyMarginType : advertiseCoinListResult.getData()) {
                            if (authMerchantApplyMarginType.getCoinName().equals(ads.getCoinName())) {
                                coinInfo = authMerchantApplyMarginType;
                            }
                        }
                    }
                } else {
                    Toasty.showError(eventBean.getCode() + eventBean.getMessage());
                }
                break;
            //接收选择的付款方式
            case EvKey.payWaySettingsSelected:
                if (eventBean.getType() != adType) return;
                if (eventBean.isStatue()) {
                    mSelectList = (ArrayList<PayListBean.DataBean>) eventBean.getObject();
                    updatePayType();
                } else {
                    Toasty.showError(eventBean.getCode() + eventBean.getMessage());
                }
                break;
            //查询广告详情
            case EvKey.advertiseDetail:
                if (eventBean.getType() != adType) return;
                if (eventBean.isStatue()) {
                    if (eventBean.getObject() == null) return;
                    ads = (AdSelfDownFindResult.DataBean.RecordsBean) eventBean.getObject();
                    if (ads != null) initAdsData(ads);
                } else {
                    Toasty.showError(eventBean.getCode() + eventBean.getMessage());
                }
                break;
            case EvKey.logout_success_401:
                if (eventBean.isStatue()) {
                    uc.isRefresh.setValue(true);
                }
                break;
            default:
                break;
        }

    }

    private void updatePrice(FindPriceResult findPriceResult) {
        //查询广告详情
        if (ads != null) {
            AdvertiseSelfClient.getInstance().selfAdvertiseUpFind(ads.getId());
        }
        ratePrice = findPriceResult.getData();
        fixedPriceTips.set("当前市场参考价格：" + ratePrice + " CNY" + "\n" + "浮动比例是指以当前市场价格多少百分比进行交易");
        premiumPriceTips.set("当前市场参考价格：" + ratePrice + " CNY" + "\n" + "启用后，您的币价不会随市场波动，价格不变");
        if (isFixedPrice.get()) {
            tradePrice.set(fixedPrice.get());
        } else {
            if (StringUtils.isEmpty(premiumPrice.get().trim())) return;
            tradePrice.set(DfUtils.numberFormat(ratePrice * (Double.valueOf(premiumPrice.get()) / 100), 2));
        }
    }

    /**
     * 买入数量或卖出数量
     */
    public void countChange() {
        if (coinInfo != null) {
            String count = tradeNumber.get();
            String max = MathUtils.subZeroAndDot(coinInfo.getAdvMaxLimit() + "");
            String min = MathUtils.subZeroAndDot(coinInfo.getAdvMinLimit() + "");

            if (!StringUtils.isEmpty(count) && !StringUtils.isEmpty(max) && !StringUtils.isEmpty(min) && Double.valueOf(max) != 0 && Double.valueOf(min) != 0) {
                if (Double.valueOf(count) > Double.valueOf(max)) {
                    if (adType == 0) {
                        Toasty.showError("买入数量" + "必须" + "大于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMinLimit() + "") + " 且 " + "小于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMaxLimit() + ""));
                    } else {
                        Toasty.showError("卖出数量" + "必须" + "大于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMinLimit() + "") + " 且 " + "小于等于" + MathUtils.subZeroAndDot(coinInfo.getAdvMaxLimit() + ""));
                    }
                    if (Double.valueOf(count) > Double.valueOf(max)) {
                        tradeNumber.set(max);
                    }
                }
            }
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

    /**
     * 将选择的支付方式中文转换成英文
     *
     * @param payway
     * @return
     */
    private String getPayByCode(String payway) {
        if (StringUtils.isNotEmpty(payway)) {
            StringBuffer stringBuffer = new StringBuffer();
            if (payway.contains(Constant.Pay_alipay)) {
                stringBuffer = stringBuffer.append(Constant.alipay).append(",");
            }
            if (payway.contains(Constant.Pay_wechat)) {
                stringBuffer = stringBuffer.append(Constant.wechat).append(",");
            }
            if (payway.contains(Constant.Pay_card)) {
                stringBuffer = stringBuffer.append(Constant.card).append(",");
            }
            if (payway.contains(Constant.Pay_PAYPAL)) {
                stringBuffer = stringBuffer.append(Constant.PAYPAL).append(",");
            }
            if (payway.contains(Constant.Pay_other)) {
                stringBuffer = stringBuffer.append(Constant.other).append(",");
            }
            return StringUtils.getRealString(stringBuffer.toString());
        }
        return payway;
    }

    /**
     * 将选择的支付方式英文转换成中文
     *
     * @param payway
     * @return
     */
    private String getSetPayByCode(String payway) {
        if (StringUtils.isNotEmpty(payway)) {
            StringBuffer stringBuffer = new StringBuffer();
            if (payway.contains(Constant.alipay)) {
                stringBuffer = stringBuffer.append(Constant.Pay_alipay).append(",");
            }
            if (payway.contains(Constant.wechat)) {
                stringBuffer = stringBuffer.append(Constant.Pay_wechat).append(",");
            }
            if (payway.contains(Constant.card)) {
                stringBuffer = stringBuffer.append(Constant.Pay_card).append(",");
            }
            if (payway.toLowerCase().contains(Constant.PAYPAL)) {
                stringBuffer = stringBuffer.append(Constant.Pay_PAYPAL).append(",");
            }
            if (payway.contains(Constant.other)) {
                stringBuffer = stringBuffer.append(Constant.Pay_other).append(",");
            }
            return StringUtils.getRealString(stringBuffer.toString());
        }
        return payway;
    }

    /**
     * 根据返回数据显示
     *
     * @param ads
     */
    private void initAdsData(AdSelfDownFindResult.DataBean.RecordsBean ads) {
        LogUtils.e("ads==" + ads);
        coinName.set(ads.getCoinName());
        coinAddressStr = ads.getCountry();
        coinAddress.set(coinAddressStr);
        coinType.set(ads.getLocalCurrency());
        tradePrice.set(ads.getPrice() + "");
        minAmount.set(ads.getMinLimit() + "");
        maxAmount.set(ads.getMaxLimit() + "");
        tradeNumber.set(ads.getNumber() + "");
        payMode.set(getSetPayByCode(ads.getPayMode()));
        tradeDeadline.set(ads.getTimeLimit() + "");
        autoResponse.set(StringUtils.isEmpty(ads.getAutoword()) ? "" : ads.getAutoword());
        remarks.set(StringUtils.isEmpty(ads.getRemark()) ? "" : ads.getRemark());
        if (ads.getPriceType() == 0) {
            premiumPrice.set("100");
        } else {
            premiumPrice.set(MathUtils.subZeroAndDot(ads.getPremiseRate() * 100 + ""));
        }
        payIds = ads.getPayIds();
    }
}
