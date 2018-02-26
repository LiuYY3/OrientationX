package com.xmb.orientationx.message;

import android.text.TextUtils;

import com.xmb.orientationx.interfaces.XClickListener;

/**
 * Created by lym on 2018/02/24.
 */

public class XClickMessageEvent {

    private XClickListener clickListener;

    private static final XClickMessageEvent instance = new XClickMessageEvent();

    public static XClickMessageEvent getInstance() {
        return instance;
    }

    public void setClick() {
        if (clickListener != null) {
            clickListener.onClick();
        }
    }

    public void setClickListener(XClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
