package com.xmb.orientationx.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.utils.StatusBarUtil;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by Mr xu on 2018/03/07.
 */

public class XSettingActivity extends XBaseActivity {

    @BindView(R.id.id_activity_name)
    TextView mNameTextView;
    @BindView(R.id.id_setting_to_profile_btn)
    Button mSettingToProfileButton;
    @BindView(R.id.id_setting_ib1)
    ImageButton mSettingib1;

    @BindString(R.string.app_setting)
    String setting;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
        ButterKnife.bind(this);
        initRxBindings();
        initViews();
    }

    private void initViews() {
        this.showTitle(true, setting);
    }

    private void initRxBindings() {
        RxView.clicks(mSettingToProfileButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
            }
        });
        RxView.clicks(mSettingib1).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XSettingActivity.this, XSettingCoActivity.class);
                startActivity(intent);
            }
        });
    }

}
