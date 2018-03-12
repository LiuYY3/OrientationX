package com.xmb.orientationx.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;
import com.xmb.orientationx.exception.XBaseException;

/**
 * Created by lym on 2018/03/11.
 */

public class XWalkGuideActivity extends XBaseActivity {

    private WalkNavigateHelper mWalkHelper;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {

        super.onCreateBase(savedInstanceState);

        View view = WalkNavigateHelper.getInstance().onCreate(this);
        if (view != null) {
            setContentView(view);
        } else {
            Log.i("View", "onCreateBase: null");
        }

        mWalkHelper = WalkNavigateHelper.getInstance();
//        mWalkHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
//            @Override
//            public void onWalkNaviModeChange(int i, WalkNaviModeSwitchListener walkNaviModeSwitchListener) {
//                mWalkHelper.switchWalkNaviMode(XWalkGuideActivity.this, i, walkNaviModeSwitchListener);
//            }
//
//            @Override
//            public void onNaviExit() {
//
//            }
//        });

        mWalkHelper.startWalkNavi(this);

        mWalkHelper.setRouteGuidanceListener(this, new IWRouteGuidanceListener() {
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
        });
    }

    @Override
    public void onDestroyBase() throws XBaseException {
        super.onDestroyBase();
        mWalkHelper.quit();
    }

    @Override
    public void onPauseBase() throws XBaseException {
        super.onPauseBase();
        mWalkHelper.pause();
    }

    @Override
    public void onResumeBase() throws XBaseException {
        super.onResumeBase();
        mWalkHelper.resume();
    }
}
