package com.spark.chiefwallet.app.quotes.search;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.base.ARouterPath;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.modulespot.SpotCoinClient;
import com.spark.modulespot.pojo.AllThumbResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.SPUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/4/30
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class QuotesSearchViewModel extends BaseViewModel {
    public ObservableField<String> quotesInput = new ObservableField<>();

    public QuotesSearchViewModel(@NonNull Application application) {
        super(application);
    }

    private AllThumbResult.DataBean allThumbResult;
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> isRefresh = new SingleLiveEvent<>();
    }

    public void isFavor(AllThumbResult.DataBean dataBean) {
        if (!App.getInstance().isAppLogin()) {
            ARouter.getInstance().build(ARouterPath.ACTIVITY_ME_LOGIN)
                    .navigation();
        } else {
            this.allThumbResult = dataBean;
            showDialog(App.getInstance().getString(R.string.loading));
            if (dataBean.isFavor()) {
                SpotCoinClient.getInstance().favorDelete(dataBean.getSymbol());
            } else {
                SpotCoinClient.getInstance().favorAdd(dataBean.getSymbol());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            //添加收藏
            case EvKey.favorAdd:
                dismissDialog();
                if (eventBean.isStatue()) {
                    List<String> tempList = SPUtils.getInstance().getFavorFindList();
                    if (!tempList.contains(allThumbResult.getSymbol())) {
                        tempList.add(allThumbResult.getSymbol());
                    }
                    SPUtils.getInstance().setFavorFindList(tempList);
                    uc.isRefresh.setValue(true);
                    Toasty.showSuccess(App.getInstance().getString(R.string.add_collection_success));
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            //取消收藏
            case EvKey.favorDelete:
                dismissDialog();
                if (eventBean.isStatue()) {
                    List<String> tempList = SPUtils.getInstance().getFavorFindList();
                    if (tempList.contains(allThumbResult.getSymbol())) {
                        tempList.remove(allThumbResult.getSymbol());
                    }
                    SPUtils.getInstance().setFavorFindList(tempList);
                    uc.isRefresh.setValue(false);
                    Toasty.showSuccess(App.getInstance().getString(R.string.cancel_collection_success));
                } else {
                    Toasty.showError(eventBean.getMessage());
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
