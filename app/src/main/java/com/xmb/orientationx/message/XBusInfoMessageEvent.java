package com.xmb.orientationx.message;

import com.xmb.orientationx.interfaces.XBusInfoListener;

/**
 * Created by lym on 2018/04/06.
 */
public class XBusInfoMessageEvent {

    private XBusInfoListener busListener;

    private static final XBusInfoMessageEvent instance = new XBusInfoMessageEvent();

    public static XBusInfoMessageEvent getInstance() {
        return instance;
    }

    public void setInfo(String info) {
        if (busListener != null) {
            busListener.onBusInfo(info);
        }
    }

    public void setBusListener(XBusInfoListener clickListener) {
        this.busListener = clickListener;
    }

}
