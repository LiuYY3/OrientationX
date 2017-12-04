package com.xmb.orientationx.model;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

/**
 * XLocationListener.
 * subclass of {@link BDAbstractLocationListener}
 */
public abstract class XLocationListener extends BDAbstractLocationListener{
    @Override
    public void onConnectHotSpotMessage(String s, int i) {
        super.onConnectHotSpotMessage(s, i);
    }

    @Override
    public void onLocDiagnosticMessage(int i, int i1, String s) {
        super.onLocDiagnosticMessage(i, i1, s);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

    }
}
