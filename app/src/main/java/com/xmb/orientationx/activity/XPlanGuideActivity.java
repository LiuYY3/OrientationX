package com.xmb.orientationx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.interfaces.XBusInfoListener;
import com.xmb.orientationx.message.XBusInfoMessageEvent;
import com.xmb.orientationx.message.XClickMessageEvent;
import com.xmb.orientationx.utils.XAppDataUtils;

import butterknife.BindString;
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
    @BindView(R.id.id_bus_info_btn)
    Button mBusButton;
    @BindView(R.id.id_start_pt_txt)
    EditText mStartEditText;
    @BindView(R.id.id_end_pt_txt)
    EditText mEndEditText;
    @BindView(R.id.id_bus_info_txt)
    TextView mBusTextView;

    @BindString(R.string.app_guide)
    String guide;
    @BindView(R.id.id_activity_left_icon)
    ImageView mBackImagView;

    private String mInfo = "";

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_guide_picker);
        ButterKnife.bind(this);
        initViews();
        initRxBindings();
    }

    private void initViews() {
        this.showTitle(true, guide);
        this.showLeftIcon(true, R.mipmap.ic_action_arrow_left);
        XBusInfoMessageEvent.getInstance().setBusListener(new XBusInfoListener() {
            @Override
            public void onBusInfo(String info) {
                Log.i("BusInfo", "onBusInfo: " + info);
                mBusTextView.setText(info);
            }
        });
        mStartEditText.setText(XAppDataUtils.getInstance().getsAdr());
        mEndEditText.setText(XAppDataUtils.getInstance().geteAdr());
        mStartEditText.setEnabled(false);
        mEndEditText.setEnabled(false);
    }

    private void initRxBindings() {
        RxView.clicks(mBackImagView).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
            }
        });
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

        RxView.clicks(mBusButton).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                return !TextUtils.isEmpty(mStartEditText.getText()) && !TextUtils.isEmpty(mEndEditText.getText());
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    XClickMessageEvent.getInstance().setClick(1);
                }
            }
        });
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, XMapActivity.class);
        startActivity(intent);
    }

}
