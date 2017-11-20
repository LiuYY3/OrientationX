package com.xmb.orientationx.utils;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

/**
 * PermissionUtil.
 * @author 徐梦笔
 */
public class PermissionUtil {

    public static void checkPermissions(@NonNull Activity context, @NonNull Action1<Boolean> listener) {
        new RxPermissions(context)
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(listener);
    }

}
