package com.spark.chiefwallet.app.trade.legal_currency.chat;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.AdvertiseScanClient;
import com.spark.otcclient.ChatClient;
import com.spark.otcclient.pojo.ChatListResult;
import com.spark.otcclient.pojo.FindMerchantDetailsResult;
import com.spark.otcclient.pojo.LcOrderResult;
import com.spark.wsclient.base.WsCMD;
import com.spark.wsclient.pojo.ChatBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import me.spark.mvvm.base.BaseApplication;
import me.spark.mvvm.base.BaseViewModel;
import me.spark.mvvm.base.EvKey;
import me.spark.mvvm.binding.command.BindingAction;
import me.spark.mvvm.binding.command.BindingCommand;
import me.spark.mvvm.bus.event.SingleLiveEvent;
import me.spark.mvvm.http.impl.OnRequestListener;
import me.spark.mvvm.utils.DfUtils;
import me.spark.mvvm.utils.EventBean;
import me.spark.mvvm.utils.EventBusUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.StringUtils;
import me.spark.mvvm.utils.WebSocketResponse;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ChatViewModel extends BaseViewModel {
    private OnRequestListener mOnRequestListener;

    public ChatViewModel(@NonNull Application application) {
        super(application);
    }

    public ObservableField<String> chatPost = new ObservableField<>("");
    public ObservableField<CharSequence> money = new ObservableField<>();
    public ObservableField<CharSequence> status = new ObservableField<>();

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<String> chatContent = new SingleLiveEvent<>();
        public SingleLiveEvent<ChatBean> chatReceiveObservable = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> mIsRefresh = new SingleLiveEvent<>();
        public SingleLiveEvent<String> mIsRefreshRate = new SingleLiveEvent<>();
    }

    public void initViewDate(int pageIndex, String orderId, OnRequestListener onRequestListener) {
        this.mOnRequestListener = onRequestListener;
        ChatClient.getInstance().getHistoyList(pageIndex, orderId);
    }

    public void initViewData(LcOrderResult.DataBean.RecordsBean mRecordsBean) {
        AdvertiseScanClient.getInstance().findMerchantDetails(mRecordsBean.getMemberId());
        CharSequence text = new SpanUtils()
                .append(App.getInstance().getString(R.string.str_total_money_all))
                .append("￥ " + DfUtils.formatNum(String.valueOf(mRecordsBean.getMoney()))).setForegroundColor(ContextCompat.getColor(App.getInstance(), R.color.base)).setBold()
                .create();
        money.set(text);
        CharSequence state = new SpanUtils()
                .append(App.getInstance().getString(R.string.str_order_status))
                .append(mRecordsBean.initStatueType()).setForegroundColor(ContextCompat.getColor(App.getInstance(), R.color.base)).setBold()
                .create();
        status.set(state);
    }

    //选择币种
    public BindingCommand postCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (StringUtils.isEmpty(chatPost.get().trim())) return;
            uc.chatContent.setValue(chatPost.get().trim());
            chatPost.set("");
        }
    });

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebSocketResponse webSocketResponse) {
        switch (webSocketResponse.getCmd()) {
            //接受聊天信息
            case WsCMD.PUSH_CHAT:
                ChatBean chatReceive = App.gson.fromJson(webSocketResponse.getResponse(), ChatBean.class);
                chatReceive.setType(ChatBean.Type.LEFT);
                uc.chatReceiveObservable.setValue(chatReceive);
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        switch (eventBean.getOrigin()) {
            case EvKey.chatList:
                if (eventBean.isStatue()) {
                    if (mOnRequestListener != null) {
                        ChatListResult chatListResult = (ChatListResult) eventBean.getObject();
                        List<ChatBean> list = new Gson().fromJson(BaseApplication.gson.toJson(chatListResult.getData().getRecords()), new TypeToken<List<ChatBean>>() {
                        }.getType());
                        mOnRequestListener.onSuccess(list);
                    }
                } else {
                    if (mOnRequestListener != null)
                        mOnRequestListener.onFail(eventBean.getMessage());
                }
                break;
            case EvKey.findMerchantDetails:
                if (eventBean.isStatue()) {
                    FindMerchantDetailsResult findMerchantDetailsResult = (FindMerchantDetailsResult) eventBean.getObject();
                    uc.mIsRefreshRate.setValue(findMerchantDetailsResult.getData().formatRangeTimeOrder());
                } else {
                    Toasty.showError(eventBean.getMessage());
                }
                break;
            case EvKey.logout_success_401:
                if (eventBean.isStatue()) {
                    uc.mIsRefresh.setValue(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegister(this);
    }
}
