package com.xmb.orientationx.activity;

import android.content.Intent;
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

public class XAccountActivity extends XBaseActivity {

    @BindView(R.id.id_activity_name)
    TextView mNameTextView;
    @BindView(R.id.id_back_to_map_btn)
    Button mBackToMapButton;
    @BindView(R.id.id_to_setting_btn)
    Button mToSetButton;
    @BindView(R.id.id_to_profile_favorite_btn)
    Button mToFavoriteButton;

    @BindString(R.string.app_profile)
    String profile;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_profile);
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
        ButterKnife.bind(this);
        initRxBindings();
        initViews();
    }

    private void initViews() {
        this.showTitle(true, profile);
    }

    private void initRxBindings() {
        RxView.clicks(mBackToMapButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        RxView.clicks(mToSetButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
            }
        });

        RxView.clicks(mToFavoriteButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XFavoriteActivity.class);
                startActivity(intent);
            }
        });
    }

}
