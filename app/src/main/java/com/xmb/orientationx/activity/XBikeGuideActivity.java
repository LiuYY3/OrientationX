package com.xmb.orientationx.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBRouteGuidanceListener;
import com.baidu.mapapi.bikenavi.model.BikeRouteDetailInfo;
import com.baidu.mapapi.bikenavi.model.RouteGuideKind;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.xmb.orientationx.exception.XBaseException;

/**
 * Created by lym on 2018/03/11.
 */

public class XBikeGuideActivity extends XBaseActivity {

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);

        View v = BikeNavigateHelper.getInstance().onCreate(this);
        if (v != null) {
            setContentView(v);
        }

        BikeNavigateHelper.getInstance().startBikeNavi(this);

        BikeNavigateHelper.getInstance().setRouteGuidanceListener(this, new IBRouteGuidanceListener() {
            @Override
            public void onRouteGuideIconUpdate(Drawable drawable) {

            }

            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {

            }

            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {

            }

            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {

            }

            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {

            }

            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {

            }

            @Override
            public void onReRouteComplete() {

            }

            @Override
            public void onArriveDest() {

            }

            @Override
            public void onVibrate() {

            }

            @Override
            public void onGetRouteDetailInfo(BikeRouteDetailInfo bikeRouteDetailInfo) {

            }
        });
    }

    @Override
    public void onDestroyBase() throws XBaseException {
        super.onDestroyBase();
        BikeNavigateHelper.getInstance().quit();
    }

    @Override
    public void onPauseBase() throws XBaseException {
        super.onPauseBase();
        BikeNavigateHelper.getInstance().pause();
    }

    @Override
    public void onResumeBase() throws XBaseException {
        super.onResumeBase();
        BikeNavigateHelper.getInstance().resume();
    }
}
