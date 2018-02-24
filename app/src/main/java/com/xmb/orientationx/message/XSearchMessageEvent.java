package com.xmb.orientationx.message;

import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.interfaces.XCloseStatusListener;
import com.xmb.orientationx.interfaces.XSelectListener;

/**
 * Created by lym on 2018/02/24.
 */

public class XSearchMessageEvent {

    private boolean search = false;

    private CharSequence input = "";

    private XSearchInfo info;

    private XCloseStatusListener statusListener;

    private XSelectListener selectListener;

    private static final XSearchMessageEvent instance = new XSearchMessageEvent();

    public static XSearchMessageEvent getInstance() {
        return instance;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        statusListener.onCloseStatusChanged(search);
        this.search = search;
    }

    public void setStatusListener(XCloseStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    public CharSequence getInput() {
        return input;
    }

    public void setInput(CharSequence input) {
        this.input = input;
    }

    public void setSelectListener(XSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public XSearchInfo getInfo() {
        return info;
    }

    public void setInfo(XSearchInfo info) {
        selectListener.onSelected(info);
        this.info = info;
    }
}
