package com.xmb.orientationx.activity;

import android.graphics.Typeface;
import android.os.Bundle;

import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;

/**
 * XSplashActivity.
 * subclass of {@link XBaseActivity}
 * @author 徐梦笔
 */
public class XSplashActivity extends XBaseActivity {
    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
    }

    private void initViews() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/CygnetRound.ttf");
    }
}
