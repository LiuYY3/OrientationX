package com.xmb.orientationx.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.utils.XAppDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lym on 2018/03/27.
 */
public class XDistanceActivity extends XBaseActivity {

    @BindView(R.id.id_distance_txt)
    TextView mDistanceTextView;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_cal_distance);
        ButterKnife.bind(this);
        mDistanceTextView.setText(String.valueOf(XAppDataUtils.getInstance().getDistance()));
    }
}
