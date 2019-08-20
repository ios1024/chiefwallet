package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxj.xpopup.core.BottomPopupView;
import com.spark.acclient.pojo.SpotWalletResult;
import com.spark.chiefwallet.App;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.PointLengthFilter;
import com.spark.chiefwallet.ui.popup.impl.OnOrderCreateListener;
import com.spark.chiefwallet.ui.toast.Toasty;
import com.spark.otcclient.pojo.FindPageResult.DataBean.RecordsBean;
import com.spark.otcclient.pojo.OrderCreateBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spark.mvvm.utils.MathUtils;
import me.spark.mvvm.utils.SpanUtils;
import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LcTradePopup extends BottomPopupView {
    @BindView(R.id.coin_name)
    TextView mCoinName;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.number)
    EditText mNumber;
    @BindView(R.id.limit)
    TextView mLimit;
    @BindView(R.id.trade_volume)
    TextView mTradeVolume;
    @BindView(R.id.trade_num)
    TextView mTradeNum;
    @BindView(R.id.order)
    TextView mOrder;
    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.type_unit)
    TextView mTypeUnit;
    @BindView(R.id.ll_limit)
    LinearLayout mLlLimit;
    @BindView(R.id.tvTradeType)
    TextView tvTradeType;
    @BindView(R.id.trade_use)
    TextView mTradeUse;
    @BindView(R.id.tv_popup_amount)
    TextView mTvPopupAmount;
    @BindView(R.id.tv_all)
    TextView mTvAll;
    @BindView(R.id.mobile_tv)
    TextView mobileTv;
    @BindView(R.id.mobile_view)
    TextView mobileView;
    @BindView(R.id.ll_mobile)
    LinearLayout llMobile;
    @BindView(R.id.mailbox_tv)
    TextView mailboxTv;
    @BindView(R.id.mailbox_view)
    TextView mailboxView;
    @BindView(R.id.ll_mailbox)
    LinearLayout llMailbox;

    private Context mContext;
    private RecordsBean mRecordsBean;
    private PointLengthFilter mPointLengthFilter;
    private int buyType = 0;// 0 - 按价格  1 - 按数量
    private int mTradeType;// 1 - 买  0 - 卖
    private OnOrderCreateListener mOnOrderCreateListener;
    private double money, number;
    private List<SpotWalletResult.DataBean> walletList;
    private SpotWalletResult.DataBean mDataBean;

    public LcTradePopup(@NonNull Context context, List<SpotWalletResult.DataBean> walletList, int tradeType, RecordsBean recordsBean, OnOrderCreateListener onOrderCreateListener) {
        super(context);
        this.mContext = context;
        this.walletList = walletList;
        this.mTradeType = tradeType;
        this.mRecordsBean = recordsBean;
        this.mOnOrderCreateListener = onOrderCreateListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_lc_trade;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTradeType.setText(mTradeType == 1 ? App.getInstance().getString(R.string.str_trade_type_buy_money) : App.getInstance().getString(R.string.str_trade_type_sell));
        mCoinName.setText((mTradeType == 1 ? App.getInstance().getString(R.string.buy2) : App.getInstance().getString(R.string.sell2)) + mRecordsBean.getCoinName());
        mPrice.setText(new SpanUtils().append(String.valueOf(mRecordsBean.getPrice())).append(" CNY").create());
        mTypeUnit.setText("CNY");
        mLimit.setText(MathUtils.getRundNumber(mRecordsBean.getMinLimit(), 2, null) + " - " + MathUtils.getRundNumber(mRecordsBean.getMaxLimit(), 2, null) + " CNY");
        mTradeVolume.setText("-- CNY");
        mTradeNum.setText("" + "-- " + mRecordsBean.getCoinName());

        mPointLengthFilter = new PointLengthFilter(2);
        mNumber.setFilters(new InputFilter[]{mPointLengthFilter});

        mNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isEmpty(s.toString())) return;
                switch (buyType) {
                    //按价格
                    case 0:
                        money = Double.valueOf(MathUtils.getRundNumber(Double.valueOf(s.toString()), 2, null));
                        number = Double.valueOf(MathUtils.getRundNumber(Double.valueOf(s.toString()) / mRecordsBean.getPrice(), 8, null));
                        break;
                    //按数量
                    case 1:
                        money = Double.valueOf(MathUtils.getRundNumber(Double.valueOf(s.toString()) * mRecordsBean.getPrice(), 2, null));
                        number = Double.valueOf(MathUtils.getRundNumber(Double.valueOf(s.toString()), 8, null));
                        break;
                }
                mTradeVolume.setText(MathUtils.getRundNumber(money, 2, null) + " CNY");
                mTradeNum.setText(MathUtils.getRundNumber(number, 8, null) + mRecordsBean.getCoinName());
            }
        });

        getUseMoney();

        mTvPopupAmount.setText(mRecordsBean.formatNum());
        mNumber.setHint(mTradeType == 1 ? App.getInstance().getString(R.string.str_enter_buy_money) : App.getInstance().getString(R.string.str_enter_sell_money));
        mTvAll.setText(mTradeType == 1 ? App.getInstance().getString(R.string.str_buy_all) : App.getInstance().getString(R.string.str_sell_all));
        mobileTv.setText(mTradeType == 1 ? App.getInstance().getString(R.string.str_money_buy) : App.getInstance().getString(R.string.str_money_sell));
        mailboxTv.setText(mTradeType == 1 ? App.getInstance().getString(R.string.str_amount_buy) : App.getInstance().getString(R.string.str_amount_sell));
    }

    private void getUseMoney() {
        for (SpotWalletResult.DataBean dataBean : walletList) {
            if (mRecordsBean.getCoinName().equals(dataBean.getCoinId())) {
                mDataBean = dataBean;
                mTradeUse.setText(App.getInstance().getString(R.string.str_coin_can_banlance) + MathUtils.getRundNumber(dataBean.getBalance(), 8, null) + mRecordsBean.getCoinName());
            }
        }
    }

    @OnClick({R.id.ll_mobile,
            R.id.ll_mailbox,
            R.id.tv_all,
            R.id.order,
            R.id.cancel})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.ll_mobile://按价格
                buyType = 0;

                mTypeUnit.setText("CNY");
                mPointLengthFilter = new PointLengthFilter(2);
                mNumber.setFilters(new InputFilter[]{mPointLengthFilter});

                mNumber.setText("");
                mTradeVolume.setText("-- CNY");
                mTradeNum.setText("-- " + mRecordsBean.getCoinName());

                mobileTv.setTextColor(getResources().getColor(R.color.base));
                mobileView.setVisibility(VISIBLE);
                mailboxTv.setTextColor(getResources().getColor(R.color.welcomelogin));
                mailboxView.setVisibility(INVISIBLE);

                tvTradeType.setText(mTradeType == 1 ? App.getInstance().getString(R.string.str_trade_type_buy_money) : App.getInstance().getString(R.string.str_trade_type_sell));
                mNumber.setHint(mTradeType == 1 ? App.getInstance().getString(R.string.str_enter_buy_money) : App.getInstance().getString(R.string.str_enter_sell_money));
                break;
            case R.id.ll_mailbox:
                buyType = 1;

                mTypeUnit.setText(mRecordsBean.getCoinName());
                mPointLengthFilter = new PointLengthFilter(8);
                mNumber.setFilters(new InputFilter[]{mPointLengthFilter});

                mNumber.setText("");
                mTradeVolume.setText("-- CNY");
                mTradeNum.setText("-- " + mRecordsBean.getCoinName());

                mobileTv.setTextColor(getResources().getColor(R.color.welcomelogin));
                mobileView.setVisibility(INVISIBLE);
                mailboxTv.setTextColor(getResources().getColor(R.color.base));
                mailboxView.setVisibility(VISIBLE);

                tvTradeType.setText(mTradeType == 1 ? (App.getInstance().getString(R.string.str_buy_number) + "(" + mRecordsBean.getCoinName() + ")") : (App.getInstance().getString(R.string.str_trade_type_sell_amount) + "(" + mRecordsBean.getCoinName() + ")"));
                mNumber.setHint(mTradeType == 1 ? App.getInstance().getString(R.string.str_enter_buy_amount) : App.getInstance().getString(R.string.str_enter_sell_amount));
                break;
            case R.id.tv_all://全部买入/全部出售
                if (mTradeType == 1) {// mTradeType;// 1 - 买  0 - 卖
                    switch (buyType) {// buyType 0 - 按价格  1 - 按数量
                        //按价格
                        case 0:
                            mNumber.setText(MathUtils.getRundNumber(Double.valueOf(MathUtils.getRundNumber(Double.valueOf(mRecordsBean.getRemainAmount()) * mRecordsBean.getPrice(), 8, null)), 2, null));
                            break;
                        //按数量
                        case 1:
                            mNumber.setText(MathUtils.getRundNumber(mRecordsBean.getRemainAmount(), 8, null));
                            break;
                    }
                } else {
                    if (mDataBean.getBalance() >= mRecordsBean.getRemainAmount()) {
                        switch (buyType) {// buyType 0 - 按价格  1 - 按数量
                            //按价格
                            case 0:
                                mNumber.setText(MathUtils.getRundNumber(Double.valueOf(MathUtils.getRundNumber(Double.valueOf(mRecordsBean.getRemainAmount()) * mRecordsBean.getPrice(), 8, null)), 2, null));
                                break;
                            //按数量
                            case 1:
                                mNumber.setText(MathUtils.getRundNumber(mRecordsBean.getRemainAmount(), 8, null));
                                break;
                        }
                    } else {
                        switch (buyType) {// buyType 0 - 按价格  1 - 按数量
                            //按价格
                            case 0:
                                mNumber.setText(MathUtils.getRundNumber(Double.valueOf(MathUtils.getRundNumber(Double.valueOf(mDataBean.getBalance()) * mRecordsBean.getPrice(), 8, null)), 2, null));
                                break;
                            //按数量
                            case 1:
                                mNumber.setText(MathUtils.getRundNumber(mDataBean.getBalance(), 8, null));
                                break;
                        }
                    }
                }
                break;
            case R.id.order:
                if (TextUtils.isEmpty(mNumber.getText().toString().trim())) {
                    Toasty.showError(buyType == 0 ? App.getInstance().getString(R.string.str_enter_money) : App.getInstance().getString(R.string.str_enter_number));
                    return;
                }

                double max = mRecordsBean.getMaxLimit();
                double min = mRecordsBean.getMinLimit();

                if (money > max || money < min) {
                    if (mTradeType == 0) {
                        Toasty.showError(App.getInstance().getString(R.string.sell2) + " >= " + min + App.getInstance().getString(R.string.str_and) + " <= " + max);
                        return;
                    } else {
                        Toasty.showError(App.getInstance().getString(R.string.buy2) + " >= " + min + App.getInstance().getString(R.string.str_and) + " <= " + max);
                        return;
                    }
                }

                OrderCreateBean orderCreateBean = new OrderCreateBean();
                orderCreateBean.setAdvertiseId(mRecordsBean.getId());
                orderCreateBean.setMoney(money);
                orderCreateBean.setNumber(number);
                orderCreateBean.setRemark(mRecordsBean.getRemark());
                orderCreateBean.setOrderType(mTradeType == 0 ? "1" : "0");
                orderCreateBean.setPrice(mRecordsBean.getPrice());
                if (mTradeType == 1) {
                    orderCreateBean.setTradePwd("");
                }
                /* 1按数量  2按金额购买 */
                orderCreateBean.setTradeType(buyType);

                mOnOrderCreateListener.onOrderCreate(orderCreateBean);
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
