package com.xmb.orientationx.activity;

import android.os.Bundle;
import android.widget.ImageView;
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
 * Created by lym on 2018/03/27.
 */
public class XDistanceActivity extends XBaseActivity {

    @BindView(R.id.id_distance_txt)
    TextView mDistanceTextView;
    @BindView(R.id.id_linear_distance_txt)
    TextView mLinearDistanceTextView;
    @BindString(R.string.app_edit)
    String edit;
    @BindString(R.string.app_distance)
    String distance;
    @BindView(R.id.id_activity_left_icon)
    ImageView mBackImagView;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_cal_distance);
        ButterKnife.bind(this);
        initViews();
        initRxBindings();
        mDistanceTextView.setText(String.valueOf(XAppDataUtils.getInstance().getDistance()));
        mLinearDistanceTextView.setText(String.valueOf(XAppDataUtils.getInstance().getLinearDistance()));
    }
    private void initViews() {
        this.showTitle(true, distance);
        this.showLeftIcon(true, R.mipmap.ic_action_arrow_left);
    }
    private void initRxBindings() {

        RxView.clicks(mBackImagView).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
            }
        });
    }
}
