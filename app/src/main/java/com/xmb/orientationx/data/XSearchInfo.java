package com.xmb.orientationx.data;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * SearchInfo.
 * implements {@link Serializable}
 * @author 徐梦笔
 */
public class XSearchInfo implements Serializable {

    private LatLng pt;
    private String name;
    private String address;
    private int searchTimes;
    private boolean save = false;

    public LatLng getPt() {
        return pt;
    }

    public void setPt(LatLng pt) {
        this.pt = pt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSearchTimes() {
        return searchTimes;
    }

    public void setSearchTimes(int searchTimes) {
        this.searchTimes = searchTimes;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }
}
