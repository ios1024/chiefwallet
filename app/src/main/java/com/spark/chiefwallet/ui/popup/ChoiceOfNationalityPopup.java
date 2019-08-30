package com.spark.chiefwallet.ui.popup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lxj.xpopup.core.BottomPopupView;
import com.spark.chiefwallet.R;
import com.spark.chiefwallet.ui.popup.adapter.ChoiceOfNationalityAdapter;
import com.spark.chiefwallet.ui.popup.impl.NationalChoiceListener;
import com.spark.ucclient.pojo.CountryEntity;
import com.spark.ucclient.pojo.CountryEntity2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChoiceOfNationalityPopup extends BottomPopupView {
    @BindView(R.id.rv_list)
    RecyclerView relist;
    @BindView(R.id.et_tex)
    SearchView editText;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    private NationalChoiceListener OnCoinChooseListener;
    public List<CountryEntity> countryEntities;
    CountryEntity2 countryEntity2;
    public List<CountryEntity2> countryEntities2 = new ArrayList<>();


    private ChoiceOfNationalityAdapter choiceOfNationalityAdapter;
    private Context mContext;

    public ChoiceOfNationalityPopup(@NonNull Context context, List<CountryEntity> mcountryEntities, NationalChoiceListener mOnCoinChooseListener) {
        super(context);
        this.mContext = context;
        this.countryEntities = mcountryEntities;
        this.OnCoinChooseListener = mOnCoinChooseListener;

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.activity_choice_of_nationality_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
        initView();

        editText.setIconifiedByDefault(false);
        editText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                countryEntities2.clear();
                if (!TextUtils.isEmpty(s.trim())) {

                    for (int i = 0; i < countryEntities.size(); i++) {
                        countryEntity2 = new CountryEntity2();
                        if (countryEntities.get(i).getEnName().toLowerCase().startsWith(s.trim().toLowerCase()) || countryEntities.get(i).getZhName().toLowerCase().startsWith(s.trim().toLowerCase())) {
                            countryEntity2.setZhName(countryEntities.get(i).getZhName());
                            countryEntity2.setEnName(countryEntities.get(i).getEnName());
                            countryEntity2.setAreaCode(countryEntities.get(i).getAreaCode());
                            countryEntities2.add(countryEntity2);
                        }
                    }
                    choiceOfNationalityAdapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < countryEntities.size(); i++) {
                        countryEntity2 = new CountryEntity2();
                        countryEntity2.setZhName(countryEntities.get(i).getZhName());
                        countryEntity2.setEnName(countryEntities.get(i).getEnName());
                        countryEntity2.setAreaCode(countryEntities.get(i).getAreaCode());
                        countryEntities2.add(countryEntity2);
                    }
                    choiceOfNationalityAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

    }

    private void initView() {
        countryEntities2.clear();

        for (int i = 0; i < countryEntities.size(); i++) {
            countryEntity2 = new CountryEntity2();
            countryEntity2.setZhName(countryEntities.get(i).getZhName());
            countryEntity2.setEnName(countryEntities.get(i).getEnName());
            countryEntity2.setAreaCode(countryEntities.get(i).getAreaCode());
            countryEntities2.add(countryEntity2);
        }
        choiceOfNationalityAdapter = new ChoiceOfNationalityAdapter(countryEntities2);
        relist.setLayoutManager(new LinearLayoutManager(mContext));
        relist.setAdapter(choiceOfNationalityAdapter);
        choiceOfNationalityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OnCoinChooseListener.onClickItem(position, countryEntities2);
                dismiss();
            }
        });
        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });

    }


}
