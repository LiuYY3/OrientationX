package com.xmb.orientationx.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
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

    private LatLng sPt;

    private LatLng ePt;

    private String sAdr;

    private String eAdr;

    private double distance;

    private String style;

    private double linearDistance;

    private byte[] profileImg;

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

    public LatLng getsPt() {
        return Hawk.get(XDataConstants.START_POINT, new LatLng(0, 0));
    }

    public void setsPt(LatLng sPt) {
        Hawk.put(XDataConstants.START_POINT, sPt);
    }

    public LatLng getePt() {
        return Hawk.get(XDataConstants.END_POINT, new LatLng(0, 0));
    }

    public void setePt(LatLng ePt) {
        Hawk.put(XDataConstants.END_POINT, ePt);
    }

    public String getsAdr() {
        return Hawk.get(XDataConstants.START_ADDRESS, "");
    }

    public void setsAdr(String sAdr) {
        Hawk.put(XDataConstants.START_ADDRESS, sAdr);
    }

    public String geteAdr() {
        return Hawk.get(XDataConstants.END_ADDRESS, "");
    }

    public void seteAdr(String eAdr) {
        Hawk.put(XDataConstants.END_ADDRESS, eAdr);
    }

    public double getDistance() {
        double i = 0;
        return Hawk.get(XDataConstants.DISTANCE, i);
    }

    public void setDistance(double distance) {
        Hawk.put(XDataConstants.DISTANCE, distance);
    }

    public String getStyle() {
        return Hawk.get(XDataConstants.MAP_STYLE, "c");
    }

    public void setStyle(String style) {
        Hawk.put(XDataConstants.MAP_STYLE, style);
    }

    public double getLinearDistance() {
        double i = 0;
        return Hawk.get(XDataConstants.LINEAR_DISTANCE, i);
    }

    public void setLinearDistance(double linearDistance) {
        Hawk.put(XDataConstants.LINEAR_DISTANCE, linearDistance);
    }

    public byte[] getProfileImg() {
        return Hawk.get(XDataConstants.PRO_IMG);
    }

    public void setProfileImg(byte[] profileImg) {
        Hawk.put(XDataConstants.PRO_IMG, profileImg);
    }
}
