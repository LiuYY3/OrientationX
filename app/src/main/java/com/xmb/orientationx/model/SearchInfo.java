package com.xmb.orientationx.model;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;

import java.io.Serializable;

/**
 * SearchInfo.
 * implements {@link Serializable}
 * @author 徐梦笔
 */
public class SearchInfo implements Serializable {

    private SuggestionInfo suggest;
    private PoiInfo poi;

    public SuggestionInfo getSuggest() {
        return suggest;
    }

    public void setSuggest(SuggestionInfo suggest) {
        this.suggest = suggest;
    }

    public PoiInfo getPoi() {
        return poi;
    }

    public void setPoi(PoiInfo poi) {
        this.poi = poi;
    }

}
