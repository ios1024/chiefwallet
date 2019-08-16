package com.spark.chiefwallet.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SlideTabPagerNormalAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private FragmentManager fragmentManager;

    public SlideTabPagerNormalAdapter(FragmentManager fm,
                                      List<Fragment> mFragmentList) {
        super(fm);
        this.fragmentManager = fm;
        this.mFragmentList = mFragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

}
