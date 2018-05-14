package com.xmb.orientationx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;

import com.xmb.orientationx.message.XClickMessageEvent;
import com.xmb.orientationx.utils.XAppDataUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by Mr xu on 2018/03/27.
 */
public class XSettingCoActivity extends XBaseActivity {

    @BindView(R.id.id_setting_to_co)
    Button mSettingtoCo;

    @BindString(R.string.app_setting)
    String setting;
    @BindView(R.id.id_activity_left_icon)
    ImageView mBackImagView;

    @BindView(R.id.id_co1)
    RadioButton mCo1;
    @BindView(R.id.id_co2)
    RadioButton mCo2;
    @BindView(R.id.radioGroupID)
    RadioGroup mCo;
    private int a = 0;
    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_settingco);
        ButterKnife.bind(this);
        initViews();
        initRxBindings();
        //获取实例
        //设置监
    }

    private void initViews() {
        this.showTitle(true, setting);
        this.showLeftIcon(true, R.mipmap.ic_action_arrow_left);

        Log.i("Test", "initViews: " + mCo1.getId());

//        mCo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCo1.isChecked()){
//                    XAppDataUtils.getInstance().setco1(1);
//                }
//                if (mCo2.isChecked()){
//                    XAppDataUtils.getInstance().setco1(2);
//                }else{mCo1.isChecked(); XAppDataUtils.getInstance().setco1(1);}
//            }
//        });
        if (XAppDataUtils.getInstance().getco1() == 2) {
            mCo.check(mCo2.getId());
        } else  {
            mCo.check(mCo1.getId());
        }
        mCo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                           @Override
                                           public void onCheckedChanged(RadioGroup group, int checkedId) {
                                               if(checkedId==mCo1.getId()){
                                                   XAppDataUtils.getInstance().setco1(1);
                                               }else{
                                                   XAppDataUtils.getInstance().setco1(2);
                                               }
                                           }
                                       });
    }
    private void initRxBindings() {

        RxView.clicks(mBackImagView).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
            }
        });
        RxView.clicks(mSettingtoCo).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XSettingCoActivity.this, XCommonActivity.class);
                startActivity(intent);
            }
        });
//        RxView.clicks(co1).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) throws Exception {
//
//                XAppDataUtils.getInstance().setco1(1);
//                Log.i("Test", "initViews: " + XAppDataUtils.getInstance().getco1());
//            }
//        });
//        RxView.clicks(co2).subscribe(new Consumer<Object>() {
//            @Override
//            public void accept(Object o) throws Exception {
//
//                XAppDataUtils.getInstance().setco1(2);
//            }
//        });
    }

}