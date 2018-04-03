package com.xmb.orientationx.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.interfaces.XSwitchListener;
import com.xmb.orientationx.message.XSwitchMessageEvent;
import com.xmb.orientationx.utils.StatusBarUtil;
import com.xmb.orientationx.utils.XAppDataUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by 徐梦笔 on 2018/03/07.
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
    @BindView(R.id.id_to_profile_navigation_btn)
    Button mToPlanGuideButton;
    @BindView(R.id.id_cal_distance_btn)
    Button mToDistanceButton;
    @BindView(R.id.id_to_profile_style_btn)
    Button mToSwitchMapButton;
    @BindView(R.id.id_to_profile_multi_location_btn)
    Button mToMultiButton;

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
        switch (XAppDataUtils.getInstance().getStyle()) {
            case "a":
                mToSwitchMapButton.setText("热力");
                break;
            case "b":
                mToSwitchMapButton.setText("卫星");
                break;
            default:
                mToSwitchMapButton.setText("平面");
                break;
        }
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

        RxView.clicks(mToPlanGuideButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XPlanGuideActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mToDistanceButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XDistanceActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mToSwitchMapButton).map(new Function<Object, String>() {
            @Override
            public String apply(Object o) throws Exception {
                if (mToSwitchMapButton.getText().equals("平面")){
                    return "a";
                }else if(mToSwitchMapButton.getText().equals("热力")){
                    return "b";
                }else{
                    return "c";
                }

            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String b) throws Exception {
                XSwitchMessageEvent.getInstance().setSwitch(b);
                XAppDataUtils.getInstance().setStyle(b);
                if( b.equals("a")){
                    mToSwitchMapButton.setText("热力");
                }else if(b.equals("b")){
                    mToSwitchMapButton.setText("卫星");

                }else{
                    mToSwitchMapButton.setText("平面");

                }
            }
        });

        RxView.clicks(mToMultiButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XMultiLocationActivity.class);
                startActivity(intent);
            }
        });
    }

}
