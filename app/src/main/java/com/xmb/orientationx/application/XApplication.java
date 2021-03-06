package com.xmb.orientationx.application;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.xmb.orientationx.broadcast.XLocationClient;

/**
 * XApplication.
 * extends from {@link Application}
 */
public class XApplication extends Application {

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.GCJ02);
        Logger.addLogAdapter(new AndroidLogAdapter());
        XLocationClient.getInstance().init(this);
        XLocationClient.getInstance().startTracking();
        Hawk.init(this)
                .setEncryption(new NoEncryption())
                .build();
    }

}
