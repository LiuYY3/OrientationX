package com.xmb.orientationx.utils;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * XPermissionUtils.
 * @author 徐梦笔
 */
public class XPermissionUtils {

    public static void checkPermissions(@NonNull Activity context, @NonNull Consumer<Boolean> listener) {
        new RxPermissions(context)
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE)
                .subscribe(listener);
    }

}
