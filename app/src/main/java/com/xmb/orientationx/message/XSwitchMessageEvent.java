package com.xmb.orientationx.message;

import com.xmb.orientationx.interfaces.XSwitchListener;

/**
 * Created by Mr Xu on 2018/3/28.
 */

public class XSwitchMessageEvent {
    private XSwitchListener switchListener;

    private static final XSwitchMessageEvent instance = new XSwitchMessageEvent();

    public static XSwitchMessageEvent getInstance() {
        return instance;
    }

    public void setSwitch(String style) {
        if (switchListener != null) {
            switchListener.onSwitch(style);
        }
    }

    public void setSwitchListener(XSwitchListener switchListener) {
        this.switchListener = switchListener;
    }
}
