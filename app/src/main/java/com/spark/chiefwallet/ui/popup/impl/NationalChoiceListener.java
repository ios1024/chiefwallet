package com.spark.chiefwallet.ui.popup.impl;

import com.spark.ucclient.pojo.CountryEntity2;

import java.util.List;

public interface NationalChoiceListener {
    void onClickItem(int position, List<CountryEntity2> countryEntity2);
}
