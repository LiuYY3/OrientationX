package com.xmb.orientationx.message;

import android.text.TextUtils;

import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.interfaces.XMultiLocationListener;

/**
 * Created by lym on 2018/03/28.
 */
public class XMultiLocationMessageEvent {

    private XMultiLocationListener xMultiLocationListener;

    private static final XMultiLocationMessageEvent instance = new XMultiLocationMessageEvent();

    public static XMultiLocationMessageEvent getInstance() {
        return instance;
    }

    public void setLocations(XSearchInfo[] searchInfos) {
        if (xMultiLocationListener != null) {
            xMultiLocationListener.onLocations(searchInfos);
        }
    }

    public void setMultiLocationListener(XMultiLocationListener xMultiLocationListener) {
        this.xMultiLocationListener = xMultiLocationListener;
    }
}
