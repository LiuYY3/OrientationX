package com.xmb.orientationx.utils;

import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.xmb.orientationx.constant.XDataConstants;
import com.xmb.orientationx.constant.XTags;
import com.xmb.orientationx.data.XSearchInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lym on 2018/02/22.
 */

public class XAppDataUtils {

    private ArrayList<XSearchInfo> searchHistory;

    private ArrayList<XSearchInfo> favorite;

    private XSearchInfo[] transition;

    private static final XAppDataUtils instance = new XAppDataUtils();

    public static XAppDataUtils getInstance() {
        return instance;
    }

    public ArrayList<XSearchInfo> getSearchHistory() {
        searchHistory = Hawk.get(XDataConstants.SEARCH_HISTORY, new ArrayList<XSearchInfo>());
        return searchHistory;
    }

    public void setSearchHistory(XSearchInfo searchInfo) {
        searchHistory = Hawk.get(XDataConstants.SEARCH_HISTORY, new ArrayList<XSearchInfo>());
        if (searchHistory.contains(searchInfo)) {
            searchHistory.remove(searchInfo);
        }
        searchHistory.add(0, searchInfo);
        Hawk.put(XDataConstants.SEARCH_HISTORY, searchHistory);
    }

    public ArrayList<XSearchInfo> getFavorite() {
        favorite = Hawk.get(XDataConstants.FAVORITE, new ArrayList<XSearchInfo>());
        return favorite;
    }

    public void setFavorite(XSearchInfo searchInfo) {
        boolean isAdd = true;
        favorite = Hawk.get(XDataConstants.FAVORITE, new ArrayList<XSearchInfo>());
        for (XSearchInfo info : favorite) {
            if (info.getName().equals(searchInfo.getName())) {
                isAdd = false;
                break;
            }
        }
        Log.i(XTags.SEARCH, "setFavorite: " + isAdd);
        if (isAdd) {
            favorite.add(0, searchInfo);
        }
        Hawk.put(XDataConstants.FAVORITE, favorite);
    }

    public void removeFavorite(XSearchInfo searchInfo) {
        favorite = Hawk.get(XDataConstants.FAVORITE, new ArrayList<XSearchInfo>());
        XSearchInfo temp = new XSearchInfo();
        for (XSearchInfo info : favorite) {
            if (info.getName().equals(searchInfo.getName())) {
                temp = info;
                break;
            }
        }
        favorite.remove(temp);
        Hawk.put(XDataConstants.FAVORITE, favorite);
    }

    public void updateTransition(int size) {
        transition = new XSearchInfo[size];
        Hawk.put(XDataConstants.TRANSITION, transition);
    }

    public void setTransition(int size, int position, XSearchInfo info) {
        transition = Hawk.get(XDataConstants.TRANSITION, new XSearchInfo[size]);
        transition[position] = info;
        Hawk.put(XDataConstants.TRANSITION, transition);
    }

    public XSearchInfo getTransition(int size, int position) {
        transition = new XSearchInfo[size];
        if (Hawk.get(XDataConstants.TRANSITION, new XSearchInfo[size]).length == size) {
            transition = Hawk.get(XDataConstants.TRANSITION, new XSearchInfo[size]);
        }
        return transition[position];
    }

}
