package com.xmb.orientationx.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.orhanobut.logger.Logger;
import com.xmb.orientationx.constant.XConstants;
import com.xmb.orientationx.interfaces.XLocationListener;
import com.xmb.orientationx.service.XLocationService;

/**
 * Created by lym on 2018/02/08.
 */

public class XLocationClient extends BroadcastReceiver {

    private XLocationListener mLocationListener;

    private Context mContext;

    private BDLocation mLastLocationInfo;

    private static final XLocationClient mInstance = new XLocationClient();

    public static XLocationClient getInstance() {
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
        IntentFilter filter = new IntentFilter(XConstants.SERVICE_CALLBACK);
        mContext.registerReceiver(this, filter);
    }

    public void setLocationListener(XLocationListener locationListener) {
        mLocationListener = locationListener;
    }

    public void startTracking() {
        Intent intent = new Intent(mContext, XLocationService.class);
        mContext.startService(intent);
    }

    public void finish() {
        Intent intent = new Intent(mContext, XLocationService.class);
        mContext.stopService(intent);
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mLocationListener != null && intent.getAction().equals(XConstants.SERVICE_CALLBACK)) {
            BDLocation location = (BDLocation) intent.getParcelableExtra(XConstants.LOCATION);
            if (mLastLocationInfo != null) {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng last = new LatLng(mLastLocationInfo.getLatitude(), mLastLocationInfo.getLongitude());
                if (!isLatEqual(current, last)) {
                    mLastLocationInfo = location;
                    mLocationListener.onLocationChange(location);
                }
            } else {
                mLastLocationInfo = location;
                mLocationListener.onLocationChange(location);
            }
        }
    }

    private Boolean isLatEqual(LatLng latLng1, LatLng latLng2) {
        if (latLng1.latitude == latLng2.latitude && latLng1.longitude == latLng2.longitude) {
            return true;
        }
        return false;
    }

}
