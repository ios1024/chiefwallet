package com.spark.chiefwallet.ui.popup.impl;

import com.spark.otcclient.pojo.PayListBean;
import com.spark.ucclient.pojo.CountryEntity2;

import java.util.ArrayList;
import java.util.List;

public interface ChoosePaymentMethodListener {

    void onClickItem(ArrayList<PayListBean.DataBean> selectList);
}
