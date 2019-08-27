package com.spark.chiefwallet.app.trade.legal_currency.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spark.chiefwallet.R;
import com.spark.chiefwallet.util.DateUtils;
import com.spark.otcclient.pojo.LcOrderResult;
import com.spark.wsclient.pojo.ChatBean;

import java.util.Date;
import java.util.List;

import me.spark.mvvm.utils.StringUtils;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/6/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatBean> datas;
    private String myId;
    private Context context;
    private LcOrderResult.DataBean.RecordsBean orderDetailsBean;

    public ChatAdapter(Context context, List<ChatBean> datas, String myId, LcOrderResult.DataBean.RecordsBean orderDetailsBean) {
        this.datas = datas;
        this.myId = myId;
        this.context = context;
        this.orderDetailsBean = orderDetailsBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (myId.equals(datas.get(position).getUidFrom())) {
            holder.llLeft.setVisibility(View.GONE);
            holder.llRight.setVisibility(View.VISIBLE);
            holder.tvMessageRight.setText(datas.get(position).getContent());
            if (StringUtils.isNotEmpty(datas.get(position).getSendTime())) {
                if (datas.get(position).getSendTime().length() != 13) {
                    Date date = new Date(datas.get(position).getSendTime());
                    holder.tvTimeRight.setText(DateUtils.getFormatTime(null, date));
                } else {
                    Date date = new Date(Long.parseLong(datas.get(position).getSendTime()));
                    holder.tvTimeRight.setText(DateUtils.getFormatTime(null, date));
                }
            }
        } else {
            holder.llLeft.setVisibility(View.VISIBLE);
            holder.llRight.setVisibility(View.GONE);
            holder.tvMessageLeft.setText(datas.get(position).getContent());
            if (StringUtils.isNotEmpty(datas.get(position).getSendTime())) {
                if (datas.get(position).getSendTime().length() != 13) {
                    Date date = new Date(datas.get(position).getSendTime());
                    holder.tvTimeLeft.setText(DateUtils.getFormatTime(null, date));
                } else {
                    Date date = new Date(Long.parseLong(datas.get(position).getSendTime()));
                    holder.tvTimeLeft.setText(DateUtils.getFormatTime(null, date));
                }
            }
            holder.ivHeaderLeft.setText(orderDetailsBean.getTradeToUsername().substring(0, 1));
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ivHeaderLeft;
        public TextView tvMessageLeft;
        public TextView tvTimeLeft;
        public TextView ivHeaderRight;
        public TextView tvMessageRight;
        public TextView tvTimeRight;
        public RelativeLayout llLeft;
        public RelativeLayout llRight;

        public ViewHolder(View itemView) {
            super(itemView);
            ivHeaderLeft = itemView.findViewById(R.id.ivHeaderLeft);
            tvMessageLeft = itemView.findViewById(R.id.tvMessageLeft);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            ivHeaderRight = itemView.findViewById(R.id.ivHeaderRight);
            tvMessageRight = itemView.findViewById(R.id.tvMessageRight);
            tvTimeRight = itemView.findViewById(R.id.tvTimeRight);
            llLeft = itemView.findViewById(R.id.llLeft);
            llRight = itemView.findViewById(R.id.llRight);
        }
    }
}
