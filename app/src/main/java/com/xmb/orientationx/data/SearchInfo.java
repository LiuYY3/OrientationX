package com.xmb.orientationx.data;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * SearchInfo.
 * implements {@link Serializable}
 * @author 徐梦笔
 */
public class SearchInfo implements Serializable {

    private LatLng pt;
    private String name;

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
}
