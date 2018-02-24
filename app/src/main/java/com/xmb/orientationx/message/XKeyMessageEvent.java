package com.xmb.orientationx.message;

import android.text.TextUtils;

/**
 * Created by lym on 2018/02/24.
 */

public class XKeyMessageEvent {

    private CharSequence key;

    private static final XKeyMessageEvent instance = new XKeyMessageEvent();

    public static XKeyMessageEvent getInstance() {
        return instance;
    }

    public CharSequence getKey() {
        if (TextUtils.isEmpty(key)) {
            return "";
        }
        return key;
    }

    public void setKey(CharSequence key) {
        this.key = key;
    }
}
