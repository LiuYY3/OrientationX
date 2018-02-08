package com.xmb.orientationx.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.utils.StatusBarUtil;
import com.xmb.orientationx.utils.XPermissionUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * XSplashActivity.
 * subclass of {@link XBaseActivity}
 * @author 徐梦笔
 */
public class XSplashActivity extends XBaseActivity implements Runnable {

    @BindView(R.id.id_app_name_txt)
    TextView mAppNameTextView;
    @BindView(R.id.id_app_author_txt)
    TextView mAppAuthorTextView;

    @Override
    public void onCreateBase(final Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
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
        mAppNameTextView.setTypeface(typeface);
        mAppAuthorTextView.setTypeface(typeface);
        initPermissions();
    }

    private void initPermissions() {
        XPermissionUtils.checkPermissions(this, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
                threadPool.schedule(XSplashActivity.this, 3, TimeUnit.SECONDS);
            }
        });
    }

}
