package com.xmb.orientationx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.xmb.orientationx.constant.XTags;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.fragment.XMapFragment;

/**
 * Created by lym on 2018/02/24.
 */

public class XGuideActivity extends XBaseActivity {

    private BNRoutePlanNode mBNRoutePlanNode;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);

        View v = BNRouteGuideManager.getInstance().onCreate(this, new BNRouteGuideManager.OnNavigationListener() {
            @Override
            public void onNaviGuideEnd() {
                finish();
            }

            @Override
            public void notifyOtherAction(int i, int i1, int i2, Object o) {

            }
        });

        if (v != null) {
            Log.i(XTags.MAP, "onCreateBase");
            setContentView(v);
        }

        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable("plan");
            }
        }
    }

    @Override
    public void onDestroyBase() throws XBaseException {
        BNRouteGuideManager.getInstance().onDestroy();
        super.onDestroyBase();
    }

    @Override
    public void onStartBase() throws XBaseException {
        BNRouteGuideManager.getInstance().onStart();
        super.onStartBase();
    }

    @Override
    public void onStopBase() throws XBaseException {
        BNRouteGuideManager.getInstance().onStop();
        super.onStopBase();
    }

    @Override
    public void onPauseBase() throws XBaseException {
        BNRouteGuideManager.getInstance().onPause();
        super.onPauseBase();
    }

    @Override
    public void onResumeBase() throws XBaseException {
        BNRouteGuideManager.getInstance().onResume();
        super.onResumeBase();
    }
}
