package com.xmb.orientationx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.message.XClickMessageEvent;
import com.xmb.orientationx.utils.XAppDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by lym on 2018/03/12.
 */

public class XPlanGuideActivity extends XBaseActivity {

    @BindView(R.id.id_bike_guide_btn)
    Button mBikeGuideButton;
    @BindView(R.id.id_car_guide_btn)
    Button mVehicleGuideButton;
    @BindView(R.id.id_start_pt_txt)
    EditText mStartEditText;
    @BindView(R.id.id_end_pt_txt)
    EditText mEndEditText;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_guide_picker);
        ButterKnife.bind(this);
        initViews();
        initRxBindings();
    }

    private void initViews() {
        mStartEditText.setText(XAppDataUtils.getInstance().getsAdr());
        mEndEditText.setText(XAppDataUtils.getInstance().geteAdr());
        mStartEditText.setEnabled(false);
//        mEndEditText.setEnabled(false);
    }

    private void initRxBindings() {
        RxView.clicks(mBikeGuideButton).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                return !TextUtils.isEmpty(mStartEditText.getText()) && !TextUtils.isEmpty(mEndEditText.getText());
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    startMapActivity();
                    XClickMessageEvent.getInstance().setClick(2);
                }
            }
        });

        RxView.clicks(mVehicleGuideButton).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                return !TextUtils.isEmpty(mStartEditText.getText()) && !TextUtils.isEmpty(mEndEditText.getText());
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    startMapActivity();
                    XClickMessageEvent.getInstance().setClick(0);
                }
            }
        });
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, XMapActivity.class);
        startActivity(intent);
    }

}
