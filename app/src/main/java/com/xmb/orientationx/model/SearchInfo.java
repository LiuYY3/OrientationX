package com.xmb.orientationx.model;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;

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
