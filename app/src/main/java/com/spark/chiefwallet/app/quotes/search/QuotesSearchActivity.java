package com.spark.chiefwallet.app.quotes.search;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.app.quotes.adapter.QuotesSearchAdapter;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.databinding.ActivityQuotesSearchBinding;
import com.spark.chiefwallet.util.StatueBarUtils;
import com.spark.modulespot.pojo.AllThumbResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import me.spark.mvvm.base.BaseActivity;
import me.spark.mvvm.base.Constant;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/30
 * 描    述：
 * 修订历史：
 * ================================================
 */
@Route(path = ARouterPath.ACTIVITY_QUOTES_SERACH)
public class QuotesSearchActivity extends BaseActivity<ActivityQuotesSearchBinding, QuotesSearchViewModel> {
    private List<AllThumbResult.DataBean> mDataBeans = new ArrayList<>();
    private QuotesSearchAdapter mQuotesSerachAdapter;
    private AllThumbResult mAllThumbResult;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_quotes_search;
    }

    @Override
    public int initVariableId() {
        return BR.quotesSearchViewModel;
    }


    @Override
    public void initView() {
        super.initView();
        StatueBarUtils.setStatusBarLightMode(this, true);
        StatueBarUtils.addMarginTopEqualStatusBarHeight(binding.fakeStatusBar);
        StatueBarUtils.setStatusBarColor(this, Color.WHITE);

        mAllThumbResult = App.gson.fromJson(Constant.searchQuotesJson, AllThumbResult.class);
        mQuotesSerachAdapter = new QuotesSearchAdapter(mDataBeans);
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        binding.rv.setAdapter(mQuotesSerachAdapter);

        mQuotesSerachAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_QUOTES_KLINE_CHIEF)
                        .withParcelable("quotesThumbClick", mDataBeans.get(position))
                        .navigation();
            }
        });

        mQuotesSerachAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.img_favor:
                        if (!App.getInstance().isAppLogin()) {
                            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                                    .navigation();
                        } else {
//                            showDialog(App.getInstance().getString(R.string.loading));
//                            if (mDataBeans.get(position).isFavor()) {
//                                SpotCoinClient.getInstance().favorDelete(allThumbResult.getSymbol());
//                            } else {
//                                SpotCoinClient.getInstance().favorAdd(allThumbResult.getSymbol());
//                            }
                        }
                        break;
                }
            }
        });

        initInputListener();
    }

    private void initInputListener() {
        binding.quotesInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search(editable);
            }
        });
    }

    private void search(Editable editable) {
        mDataBeans.clear();
        if (TextUtils.isEmpty(editable.toString())) {
            mQuotesSerachAdapter.notifyDataSetChanged();
            return;
        }
        for (AllThumbResult.DataBean dataBean : mAllThumbResult.getData()) {
            if (dataBean.getSymbol().contains(editable.toString().toUpperCase())) {
                mDataBeans.add(dataBean);
            }
        }
        mQuotesSerachAdapter.notifyDataSetChanged();
        if (mDataBeans.size() == 0) {
            mQuotesSerachAdapter.setEmptyView(R.layout.view_search_rv_empty, binding.rv);
        }
    }

    @OnClick({R.id.title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;

        }
    }
}
