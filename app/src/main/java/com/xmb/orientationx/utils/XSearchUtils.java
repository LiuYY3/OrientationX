package com.xmb.orientationx.utils;

import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;

import java.util.ArrayList;

/**
 * XSearchUtils.
 * @author 徐梦笔
 */
public class XSearchUtils {

    public static ArrayList<SuggestionInfo> getBestResults(ArrayList<SuggestionInfo> here, ArrayList<SuggestionInfo> outside) {
        if (here.size() == 0) {
            if (outside.size() == 0) {
                return new ArrayList<SuggestionInfo>();
            }
            return outside;
        }
        return here;
    }

}
