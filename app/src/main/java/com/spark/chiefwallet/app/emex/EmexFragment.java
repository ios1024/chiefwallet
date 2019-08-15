package com.spark.chiefwallet.app.emex;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gongwen.marqueen.MarqueeFactory;
import com.gongwen.marqueen.util.OnItemClickListener;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.BR;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.api.pojo.AnnounceBean;
import com.spark.chiefwallet.api.pojo.BannerBean;
import com.spark.chiefwallet.api.pojo.BannerPicBean;
import com.spark.chiefwallet.app.quotes.adapter.QuotesFavorAdapter;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.bean.AnnounceItemBean;
import com.spark.chiefwallet.bean.TitleBean;
import com.spark.chiefwallet.databinding.FragmentEmexBinding;
import com.spark.chiefwallet.ui.AnnounceView;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.chiefwallet.util.GlideImageLoader;
import com.spark.modulespot.B2BWsClient;
import com.spark.modulespot.pojo.AllThumbResult;
import com.spark.wsclient.pojo.B2BThumbBean;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import me.spark.mvvm.base.BaseFragment;
import me.spark.mvvm.base.Constant;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.DateUtils;
import me.spark.mvvm.utils.LanguageSPUtil;
import me.spark.mvvm.utils.LogUtils;
import me.spark.mvvm.utils.SPUtils;


/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/18
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class EmexFragment extends BaseFragment<FragmentEmexBinding, EmexViewModel> {
    private TitleBean mTitleModel;
    //    private List<BannerBean.DataBean.CarouselBean> mCarouselBeans = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private List<AllThumbResult.DataBean> mThumbList = new ArrayList<>();
    private QuotesFavorAdapter mQuotesFavorAdapter;
    private BannerPicBean bannerPicBean;
    private List<AnnounceItemBean> mAnnounceItemBeans = new ArrayList<>();
    private AllThumbResult mAllThumbResult;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_emex;
    }

    @Override
    public int initVariableId() {
        return BR.emexViewModel;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        viewModel.isVisible2User = !hidden;
    }

    @Override
    public void initView() {
        super.initView();
        //TitleSet
        mTitleModel = new TitleBean();
        mTitleModel.setTitleName(getResources().getString(R.string.Emex));
        mTitleModel.setShowLeftImg(false);
//        mTitleModel.setShowRightImg(true);
//        binding.emexTitle.titleRightImg.setImageDrawable(getResources().getDrawable(R.drawable.svg_chat));
        binding.emexTitle.setViewTitle(mTitleModel);

        binding.emexBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_EMEX_WEBDETAILS)
                        .withString("title", App.getInstance().getString(R.string.details))
                        .withString("link", bannerPicBean.getAppCarousel().get(position).getLink())
                        .navigation();
            }
        });

        mQuotesFavorAdapter = new QuotesFavorAdapter(mThumbList);
        binding.favorRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.favorRv.setAdapter(mQuotesFavorAdapter);
        mQuotesFavorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_QUOTES_KLINE_HTTP)
                        .withParcelable("quotesThumbClick", mThumbList.get(position))
                        .navigation();
            }
        });

        binding.swipeLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.base));
        binding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        binding.imgIsHideAccount.setImageDrawable(SPUtils.getInstance().isHideAccount() ?
                getResources().getDrawable(R.drawable.svg_hide) :
                getResources().getDrawable(R.drawable.svg_show));
    }

    @Override
    public void initData() {
        binding.swipeLayout.setRefreshing(true);
        viewModel.loadBanner(getContext(), new OnRequestListener<BannerBean>() {
            @Override
            public void onSuccess(BannerBean bannerBean) {
                binding.swipeLayout.setRefreshing(false);
                if (null == bannerBean) return;

                LogUtils.e("getAppCarousel", "{\"appCarousel\":" + bannerBean.getData().getAppCarousel() + "}");
                bannerPicBean = App.gson.fromJson("{\"appCarousel\":" + bannerBean.getData().getAppCarousel() + "}", BannerPicBean.class);
                imageUrls.clear();
                int languageType = LanguageSPUtil.getInstance(getContext()).getSelectLanguage();
                for (int i = 0; i < bannerPicBean.getAppCarousel().size(); i++) {
                    if (languageType == 0) {
                        for (BannerPicBean.AppCarouselBean.PictureBean pictureBean : bannerPicBean.getAppCarousel().get(i).getPicture()) {
                            if (pictureBean.getLanguage().equals(Constant.LanguageZH)) {
                                imageUrls.add(pictureBean.getUrl());
                            }
                        }
                    } else if (languageType == 1) {
                        for (BannerPicBean.AppCarouselBean.PictureBean pictureBean : bannerPicBean.getAppCarousel().get(i).getPicture()) {
                            if (pictureBean.getLanguage().equals(Constant.LanguageEN)) {
                                imageUrls.add(pictureBean.getUrl());
                            }
                        }
                    }
                }
                binding.emexBanner
                        .setImageLoader(new GlideImageLoader())
                        .setImages(imageUrls)
                        .start();
            }

            @Override
            public void onFail(String message) {
                binding.swipeLayout.setRefreshing(false);
                Toasty.showError(message);
            }
        });

        viewModel.loadAnnounce(getContext(), new OnRequestListener<AnnounceBean>() {
            @Override
            public void onSuccess(final AnnounceBean announceBean) {
                mAnnounceItemBeans.clear();
                for (AnnounceBean.DataBean.RecordsBean record : announceBean.getData().getRecords()) {
                    AnnounceItemBean announceItemBean = new AnnounceItemBean();
                    announceItemBean.setTitle(record.getHeader());
                    announceItemBean.setTime(DateUtils.formatDate("MM-dd", record.getUpdateTime()));
                    mAnnounceItemBeans.add(announceItemBean);
                }
                MarqueeFactory<LinearLayout, AnnounceItemBean> marqueeFactory = new AnnounceView(getContext());
                marqueeFactory.setData(mAnnounceItemBeans);

                binding.announce.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClickListener(View mView, Object mData, int mPosition) {
                        ARouter.getInstance().build(ARouterPath.ACTIVITY_EMEX_WEBDETAILS)
                                .withString("title", App.getInstance().getString(R.string.str_notice_detail))
                                .withString("link", announceBean.getData().getRecords().get(mPosition).getRedirectUrl())
                                .navigation();
                    }
                });
                binding.announce.setMarqueeFactory(marqueeFactory);
                binding.announce.startFlipping();
            }

            @Override
            public void onFail(String message) {
                Toasty.showError(message);
            }
        });

        if (Constant.isHttpAndWs && App.getInstance().isAppLogin()) {
            if (!TextUtils.isEmpty(Constant.b2bKlinePushJson)) {
                mAllThumbResult = App.gson.fromJson(Constant.b2bKlinePushJson, AllThumbResult.class);
                refreshFavor();
            } else {
                B2BWsClient.getInstance().getB2BKlinePush();
            }
        }
    }

    @Override
    public void initViewObservable() {
        //收藏缩略图
        viewModel.uc.mAllThumbResultSingleLiveEvent.observe(this, new Observer<List<AllThumbResult.DataBean>>() {
            @Override
            public void onChanged(@Nullable List<AllThumbResult.DataBean> dataBeans) {
                if (viewModel.isOnPause) return;
                mThumbList.clear();
                mThumbList.addAll(dataBeans);
                mQuotesFavorAdapter.notifyDataSetChanged();
            }
        });

        viewModel.uc.isHideAccountSwitchEvent.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                binding.imgIsHideAccount.setImageDrawable(aBoolean ?
                        getResources().getDrawable(R.drawable.svg_hide) :
                        getResources().getDrawable(R.drawable.svg_show));
                SPUtils.getInstance().setIsHideAccount(aBoolean);
                viewModel.initText();
            }
        });

        viewModel.uc.isRefresh.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    initData();
                }
            }
        });

        viewModel.uc.httpBean.observe(this, new Observer<AllThumbResult>() {
            @Override
            public void onChanged(@Nullable AllThumbResult allThumbResult) {
                mAllThumbResult = allThumbResult;
                refreshFavor();
            }
        });

        viewModel.uc.wsBean.observe(this, new Observer<B2BThumbBean>() {
            @Override
            public void onChanged(@Nullable B2BThumbBean b2BThumbBean) {
                if (null == mAllThumbResult) return;
                mThumbList.clear();
                if (App.getInstance().isAppLogin()) {
                    for (String favor : SPUtils.getInstance().getFavorFindList()) {
                        for (AllThumbResult.DataBean dataBean : mAllThumbResult.getData()) {
                            if (dataBean.getSymbol().equals(favor)) {
                                for (B2BThumbBean.DateBean thumbBean : b2BThumbBean.getDate()) {
                                    if (thumbBean.getSymbol().equals(favor)) {
                                        dataBean.setVolume(thumbBean.getVolume());
                                        dataBean.setHigh(thumbBean.getHigh());
                                        dataBean.setChg(thumbBean.getChg());
                                        dataBean.setCnyLegalAsset(thumbBean.getCnyLegalAsset());
                                        dataBean.setLow(thumbBean.getLow());
                                        dataBean.setClose(thumbBean.getClose());
                                        dataBean.setTurnover(thumbBean.getTurnover());
                                        mThumbList.add(dataBean);
                                    }
                                }
                            }
                        }
                    }
                }
                mQuotesFavorAdapter.notifyDataSetChanged();
            }
        });

        viewModel.uc.loginStatue.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    if (!TextUtils.isEmpty(Constant.b2bKlinePushJson)) {
                        mAllThumbResult = App.gson.fromJson(Constant.b2bKlinePushJson, AllThumbResult.class);
                        refreshFavor();
                    } else {
                        B2BWsClient.getInstance().getB2BKlinePush();
                    }
                }
            }
        });
    }

    private void refreshFavor() {
        if (null == mAllThumbResult) return;
        mThumbList.clear();
        if (App.getInstance().isAppLogin()) {
            for (String favor : SPUtils.getInstance().getFavorFindList()) {
                for (AllThumbResult.DataBean dataBean : mAllThumbResult.getData()) {
                    if (dataBean.getSymbol().equals(favor)) {
                        mThumbList.add(dataBean);
                    }
                }
            }
            mQuotesFavorAdapter.notifyDataSetChanged();
            viewModel.isFirstHttpLoadSuccess = true;
            B2BWsClient.getInstance().monitorB2BKlinePush();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.emexBanner.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.emexBanner.stopAutoPlay();
    }
}
