package com.xmb.orientationx.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.utils.PermissionUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * XSplashActivity.
 * subclass of {@link XBaseActivity}
 * @author 徐梦笔
 */
public class XSplashActivity extends XBaseActivity implements Runnable {

    private TextView mAppNameTextView;
    private TextView mAppAuthorTextView;

    @Override
    public void onCreateBase(final Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorTransparent, getTheme()));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        initViews();
    }

    @Override
    public void run() {
        Intent intent = new Intent(XSplashActivity.this, XMapActivity.class);
        XSplashActivity.this.startActivity(intent);
        XSplashActivity.this.finish();
    }

    private void initViews() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Bold.ttf");
        mAppAuthorTextView = (TextView) this.findViewById(R.id.id_app_author_txt);
        mAppNameTextView = (TextView) this.findViewById(R.id.id_app_name_txt);
        mAppNameTextView.setTypeface(typeface);
        mAppAuthorTextView.setTypeface(typeface);
        initPermissions();
    }

    private void initPermissions() {
        PermissionUtil.checkPermissions(this, new Action1<Boolean>() {
            @Override
            public void call(final Boolean aBoolean) {
                ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
                threadPool.schedule(XSplashActivity.this, 10, TimeUnit.SECONDS);
            }
        });
    }

}
