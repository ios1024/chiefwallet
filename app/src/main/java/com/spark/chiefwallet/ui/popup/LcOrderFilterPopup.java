package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lxj.xpopup.impl.PartShadowPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.flowlayout.FlowLayout;
import com.spark.chiefwallet.ui.flowlayout.TagAdapter;
import com.spark.chiefwallet.ui.flowlayout.TagFlowLayout;
import com.spark.chiefwallet.ui.popup.impl.OnLcFilterListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.spark.mvvm.base.Constant;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/20
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LcOrderFilterPopup extends PartShadowPopupView {
    @BindView(R.id.fl_ad_type)
    TagFlowLayout mFlAdType;
    @BindView(R.id.fl_status)
    TagFlowLayout mFlStatus;
    @BindView(R.id.fl_coin_name)
    TagFlowLayout mFlCoinName;
    @BindView(R.id.btn_cancel)
    TextView mBtnCancel;
    @BindView(R.id.btn_ensure)
    TextView mBtnEnsure;

    //交易类型
    private String[] mAdtype = new String[]{"全部", "出售", "购买"};
    //订单状态
    private String[] mStatus = new String[]{"全部", "未付款", "已付款", "已完成", "已取消", "申诉中"};
    //
    private String[] mCoinName = Constant.lcCoinNameArray;

    private TagAdapter mTagAdapterAdType, mTagAdapterStatus, mTagAdapterCoinName;
    private Context mContext;
    private OnLcFilterListener mOnLcFilterListener;
    private String selectAdtype = mAdtype[0], selectStatus = mStatus[0], selectCoinName;

    public LcOrderFilterPopup(@NonNull Context context, OnLcFilterListener onLcFilterListener) {
        super(context);
        this.mContext = context;
        this.mOnLcFilterListener = onLcFilterListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_lc_order_filter;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mCoinName = new String[Constant.lcCoinNameArray.length + 1];
        mCoinName[0] = "全部";
        for (int i = 0; i < Constant.lcCoinNameArray.length; i++) {
            mCoinName[i + 1] = Constant.lcCoinNameArray[i];
        }
        selectCoinName = mCoinName[0];

        mTagAdapterAdType = new TagAdapter<String>(mAdtype) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                final LayoutInflater mInflater = LayoutInflater.from(mContext);
                TextView tv = (TextView) mInflater.inflate(R.layout.flowlayout_tv,
                        mFlAdType, false);
                tv.setText(s);
                return tv;
            }
        };
        mFlAdType.setAdapter(mTagAdapterAdType);
        mFlAdType.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                selectAdtype = mAdtype[position];
                return false;
            }
        });
        mTagAdapterAdType.setSelectedList(0);


        mTagAdapterStatus = new TagAdapter<String>(mStatus) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                final LayoutInflater mInflater = LayoutInflater.from(mContext);
                TextView tv = (TextView) mInflater.inflate(R.layout.flowlayout_tv,
                        mFlStatus, false);
                tv.setText(s);
                return tv;
            }
        };
        mFlStatus.setAdapter(mTagAdapterStatus);
        mFlStatus.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                selectStatus = mStatus[position];
                return false;
            }
        });
        mTagAdapterStatus.setSelectedList(0);

        mTagAdapterCoinName = new TagAdapter<String>(mCoinName) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                final LayoutInflater mInflater = LayoutInflater.from(mContext);
                TextView tv = (TextView) mInflater.inflate(R.layout.flowlayout_tv,
                        mFlCoinName, false);
                tv.setText(s);
                return tv;
            }
        };
        mFlCoinName.setAdapter(mTagAdapterCoinName);
        mFlCoinName.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                selectCoinName = mCoinName[position];
                return false;
            }
        });
        mTagAdapterCoinName.setSelectedList(0);
    }

    @OnClick({R.id.btn_cancel,
            R.id.btn_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ensure:
                String adType = "", status = "", coinName = "";
                switch (selectAdtype) {
                    case "全部":
                        adType = "";
                        break;
                    case "出售":
                        adType = "1";
                        break;
                    case "购买":
                        adType = "0";
                        break;
                }
                switch (selectStatus) {
                    case "全部":
                        status = "-1";
                        break;
                    case "未付款":
                        status = "1";
                        break;
                    case "已付款":
                        status = "2";
                        break;
                    case "已完成":
                        status = "3";
                        break;
                    case "已取消":
                        status = "0";
                        break;
                    case "申诉中":
                        status = "4";
                        break;
                }
                coinName = selectCoinName.equals("全部") ? "" : selectCoinName;
                mOnLcFilterListener.onLcSelect(adType, status, coinName);
                dismiss();
                break;
        }
    }
}
