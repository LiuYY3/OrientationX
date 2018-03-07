package com.xmb.orientationx.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.utils.StatusBarUtil;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by lym on 2018/03/07.
 */

public class XSettingActivity extends XBaseActivity {

    @BindView(R.id.id_activity_name)
    TextView mNameTextView;
    @BindView(R.id.id_setting_to_profile_btn)
    Button mSettingToProfileButton;

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
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Bold.ttf");
        mNameTextView.setTypeface(typeface);
        mNameTextView.setText(setting);
    }

    private void initRxBindings() {
        RxView.clicks(mSettingToProfileButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
                overridePendingTransition(R.anim.top_in, R.anim.top_out);
            }
        });
    }
}
