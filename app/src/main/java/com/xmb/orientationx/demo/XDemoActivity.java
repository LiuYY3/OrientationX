package com.xmb.orientationx.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xmb.orientationx.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lym on 2018/03/11.
 */

public class XDemoActivity extends AppCompatActivity {

    @BindView(R.id.demo)
    TextView mDemoTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
        GeoCoder mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    mDemoTxt.setText("Error");
                }

                mDemoTxt.setText(geoCodeResult.getLocation().toString());
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        mSearch.geocode(new GeoCodeOption().city("南京").address("铁心桥4号6幢"));
    }

}
