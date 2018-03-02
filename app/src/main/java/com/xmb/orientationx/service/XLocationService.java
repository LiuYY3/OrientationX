package com.xmb.orientationx.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.orhanobut.logger.Logger;
import com.xmb.orientationx.constant.XConstants;
import com.xmb.orientationx.utils.XUtils;

/**
 * Created by lym on 2018/02/08.
 */

public class XLocationService extends Service {

    private LocationClient mLocationClient;

    private BDAbstractLocationListener mLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Logger.json(XUtils.objToString(bdLocation));
            Intent intent = new Intent();
            intent.setAction(XConstants.SERVICE_CALLBACK);
            intent.putExtra(XConstants.LOCATION, bdLocation);
            sendBroadcast(intent);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(this);
        setLocationClient();
        mLocationClient.registerLocationListener(mLocationListener);
        mLocationClient.start();
    }

    @Override
    public void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }

    private void setLocationClient() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("gcj02");
        option.setScanSpan(1000);
        option.disableCache(true);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(false);
        mLocationClient.setLocOption(option);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
