package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lxj.xpopup.core.DrawerPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.trade.legal_currency.adapter.SelectPayWayAdapter;
import com.spark.chiefwallet.ui.popup.impl.ChoosePaymentMethodListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.pojo.PayListBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.spark.mvvm.http.impl.OnRequestListener;

public class ChoosePaymentMethodPopup extends DrawerPopupView {
    public ArrayList<PayListBean.DataBean> mSelectList;//选中的收款方式列表
    private HashMap<String, PayListBean.DataBean> hashMap = new HashMap<>();
    public int AD_TYPE = 0;//广告类型 0 - 发布购买  1- 发布出售

    private List<PayListBean.DataBean> mCoinAddressList = new ArrayList<>();
    private SelectPayWayAdapter mAdapter;
    private Context mcontext;
    private ChoosePaymentMethodListener mchoosePaymentMethodListener;
    @BindView(R.id.rv_coin_address)
    RecyclerView recyclerView;
    //    @BindView(R.id.swipeLayout)
//    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.tv_determine)
    TextView tvDetermine;
    @BindView(R.id.tv_reset)
    TextView tvReset;

    public ChoosePaymentMethodPopup(@NonNull Context context, ArrayList<PayListBean.DataBean> selectList, int adType, List<PayListBean.DataBean> coinAddressList, ChoosePaymentMethodListener choosePaymentMethodListener) {
        super(context);
        this.mcontext = context;
        this.mSelectList = selectList;
        this.AD_TYPE = adType;
        this.mCoinAddressList = coinAddressList;
        this.mchoosePaymentMethodListener = choosePaymentMethodListener;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();


    }

    public void initView() {
        super.init();
        mAdapter = new SelectPayWayAdapter(mCoinAddressList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mcontext));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnSelectListener(new SelectPayWayAdapter.OnSelectListener() {
            @Override
            public void onSelect(PayListBean.DataBean dataBean) {
                observedBottomText(dataBean);
            }
        });

        refresh();

        for (PayListBean.DataBean payWaySetting : mSelectList) {
            String payType = payWaySetting.getPayType();
            hashMap.put(payType, payWaySetting);
        }
        tvReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hashMap.clear();
                for (PayListBean.DataBean payWaySetting : mCoinAddressList) {
                    payWaySetting.setIsSelected(0);
                }
                mAdapter.update();
            }
        });
        tvDetermine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定
                ArrayList<PayListBean.DataBean> payWaySettingsSelected = new ArrayList<>();//选择的收款方式
                for (PayListBean.DataBean payWaySetting : mCoinAddressList) {
                    if (payWaySetting.getIsSelected() == 1) {
                        payWaySettingsSelected.add(payWaySetting);
                    }
                }

                mchoosePaymentMethodListener.onClickItem(payWaySettingsSelected);
                dismiss();
            }
        });

    }

    private void refresh() {

        for (PayListBean.DataBean payWaySetting : mCoinAddressList) {
            for (PayListBean.DataBean temp : mSelectList) {
                if (temp.getId() == payWaySetting.getId()) {
                    payWaySetting.setIsSelected(1);
                }
            }
        }
        mAdapter.update();
    }


    /**
     * 选中后更新视图
     *
     * @param payWaySetting
     */
    public void observedBottomText(PayListBean.DataBean payWaySetting) {
        String payType = payWaySetting.getPayType();
        if (hashMap.containsKey(payType)) {
            PayListBean.DataBean temp = hashMap.get(payType);
            if (temp.getId() != payWaySetting.getId()) {
                Toasty.showError(mcontext.getString(R.string.str_pay_way_only_one));
            } else {
                hashMap.remove(payType);
                for (PayListBean.DataBean dataBean : mCoinAddressList) {
                    if (dataBean.getId() == payWaySetting.getId()) {
                        dataBean.setIsSelected(0);
                    }
                }
                mAdapter.update();
            }
        } else {
            hashMap.put(payType, payWaySetting);
            for (PayListBean.DataBean dataBean : mCoinAddressList) {
                if (dataBean.getId() == payWaySetting.getId()) {
                    dataBean.setIsSelected(1);
                }
            }
            mAdapter.update();
        }
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.activity_choose_payment_method_popup;

    }
}
