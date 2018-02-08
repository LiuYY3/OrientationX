package com.xmb.orientationx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.orhanobut.logger.Logger;
import com.xmb.orientationx.R;
import com.xmb.orientationx.broadcast.XLocationClient;
import com.xmb.orientationx.interfaces.XLocationListener;

/**
 * Created by lym on 2018/02/08.
 */

public class XMapFragment extends Fragment implements XLocationListener {

    private MapView mMapView;
    private BaiduMap mMap;
    private Marker mMyMarker;
    private LatLng mMyPosition;

    @Override
    public void onLocationChange(BDLocation location) {
        if (mMyMarker != null) {
            mMyMarker.remove();
        }

        mMyPosition = new LatLng(location.getLatitude(), location.getLongitude());

        mMyMarker = doShowMarker(mMyPosition, R.mipmap.marker);
    }

    private Marker doShowMarker(LatLng position, int marker) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(marker);

        MarkerOptions option = new MarkerOptions()
                .position(position)
                .icon(bitmap);

        option.animateType(MarkerOptions.MarkerAnimateType.grow);
        return (Marker) mMap.addOverlay(option);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.id_main_map);
        initMap();
        setLocation();
    }

    private void setLocation() {
        XLocationClient.getInstance().setLocationListener(this);
        XLocationClient.getInstance().startTracking();
    }

    private void initMap() {
        mMap = mMapView.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

}
