package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.lxj.xpopup.core.DrawerPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.trade.currency.drawer.DrawerFragment;
import com.spark.chiefwallet.app.trade.currency.drawer.DrawerWsFragment;
import com.spark.chiefwallet.ui.adapter.SlideTabPagerAdapter;
import com.spark.chiefwallet.ui.progresslayout.ProgressLinearLayout;
import com.spark.chiefwallet.ui.tablayout.SlidingTabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spark.mvvm.base.BaseRequestCode;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.utils.EventBusUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/30
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class B2BDrawerPopup extends DrawerPopupView {
    @BindView(R.id.quotes_input)
    EditText mQuotesInput;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles;
    private SlideTabPagerAdapter mAdapter;

    private FragmentManager fragmentManager;

    @BindView(R.id.drawer_tab)
    SlidingTabLayout mDrawerTab;
    @BindView(R.id.drawer_tab_vp)
    ViewPager mDrawerTabVp;
    @BindView(R.id.drawer_root)
    ProgressLinearLayout mDrawerRoot;

    public B2BDrawerPopup(@NonNull Context context, FragmentManager fragmentManager) {
        super(context);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mDrawerRoot.showLoading();
        if (Constant.coinPairThumbBeanList.isEmpty()) return;
        mTitles = Constant.coinPairThumbBeanList.toArray(new String[Constant.coinPairThumbBeanList.size()]);
        for (String title : mTitles) {
            if (Constant.isHttpAndWs) {
                mFragments.add(DrawerWsFragment.newInstance(title));
            } else {
                mFragments.add(DrawerFragment.newInstance(title));
            }
        }
        mAdapter = new SlideTabPagerAdapter(fragmentManager, mFragments, mTitles);
        mDrawerTabVp.setAdapter(mAdapter);
        mDrawerTab.setViewPager(mDrawerTabVp);
        mDrawerRoot.showContent();
        initInputListener();
    }

    private void initInputListener() {
        mQuotesInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                EventBusUtils.postSuccessEvent(EvKey.drawerSearch, BaseRequestCode.OK, TextUtils.isEmpty(editable.toString()) ? "" : editable.toString().trim());
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_b2b_drawer;
    }

    @OnClick({})
    public void onClick(View view) {

    }
}
