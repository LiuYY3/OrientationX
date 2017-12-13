package com.xmb.orientationx.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by lym on 2017/11/12.
 */

public class Utils {

    public static String getJson(Context context, String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    assetManager.open(filename)));
            String next = "";
            while (null != (next = br.readLine())) {
                stringBuilder.append(next);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            stringBuilder.delete(0, stringBuilder.length());
        }
        return stringBuilder.toString().trim();
    }

    public static Boolean checkEmptyList(List<?> list) {
        return list != null && list.size() > 0;
    }

    public static String nullCity(String city) {
        if (city == null) {
            return "全国";
        }
        return city;
    }

}
