package com.xmb.orientationx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;

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
    private RadioGroup radioGroup;
    private RadioButton co1;
    private RadioButton co2;
    private int a = 0;
    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_settingco);
        ButterKnife.bind(this);
        initViews();
        initRxBindings();
        super.onCreate(savedInstanceState);
        //加载布局文件
        setContentView(R.layout.activity_main);
        //获取实例
        radioGroup=(RadioGroup)findViewById(R.id.radioGroupID);
        co1=(RadioButton)findViewById(R.id.id_co1);
        co2=(RadioButton)findViewById(R.id.id_co2);
        //设置监听


    }

    private void initViews() {
        this.showTitle(true, setting);
        this.showLeftIcon(true, R.mipmap.ic_action_arrow_left);
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
        RxView.clicks(co1).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

                XAppDataUtils.getInstance().setco1(1);
            }
        });
        RxView.clicks(co2).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

                XAppDataUtils.getInstance().setco1(2);
            }
        });
    }

}