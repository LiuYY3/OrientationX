package com.xmb.orientationx.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.xmb.orientationx.R;
import com.xmb.orientationx.broadcast.XLocationClient;
import com.xmb.orientationx.constant.XTags;
import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.interfaces.XLocationListener;
import com.xmb.orientationx.interfaces.XSelectListener;
import com.xmb.orientationx.message.XSearchMessageEvent;
import com.xmb.orientationx.utils.XUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lym on 2018/02/08.
 */

public class XMapFragment extends Fragment implements XLocationListener,
        XSelectListener,
        OnGetRoutePlanResultListener {

    @BindView(R.id.id_main_map)
    MapView mMapView;

    private String mLocateCity = "盐城";

    private Unbinder mBinder;
    private BaiduMap mMap;
    private Marker mDestinationMarker;
    private BDLocation mMyLocation;
    private LatLng mMyPosition;
    private LatLng mDestination;
    private RoutePlanSearch mRoutePlanSearch;
    private String mDestinationAddress;
    private PolylineOptions mPolyline;
    private Overlay mRoute;
    private ArrayList<Overlay> mRoutes = new ArrayList<>();

    private boolean isFirst = true;

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (XUtils.checkEmptyList(drivingRouteResult.getRouteLines())) {
            DrivingRouteLine drive = drivingRouteResult.getRouteLines().get(0);
            if (XUtils.checkEmptyList(drive.getAllStep())) {
                for (DrivingRouteLine.DrivingStep step : drive.getAllStep()) {
                    if (XUtils.checkEmptyList(step.getWayPoints())) {
                        mPolyline = new PolylineOptions().width(8)
                                .color(getResources().getColor(R.color.colorBlack)).points(step.getWayPoints());
                        mRoute = mMap.addOverlay(mPolyline);
                        mRoutes.add(mRoute);
                    }
                }
            }
        }
    }

    private void doRouteSearch() {
        if (mRoutes.size() > 0) {
            for (Overlay r : mRoutes) {
                r.remove();
            }
        }

        mRoutes.clear();

        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(XMapFragment.this);
        Log.i(XTags.MAP, "doRouteSearch: " + mMyLocation.getAddrStr() + " : " + mDestinationAddress);
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(mLocateCity, mMyLocation.getAddrStr());
        PlanNode enNode = PlanNode.withCityNameAndPlaceName(mLocateCity, mDestinationAddress);

        mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }

    @Override
    public void onLocationChange(BDLocation location) {
        mMyLocation = location;
        mMyPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mLocateCity = location.getCity();

        mMap.setMyLocationData(new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build()
        );

        if (isFirst) {
            mMap.setMyLocationConfiguration(new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.FOLLOWING,
                    true,
                    BitmapDescriptorFactory.fromResource(R.mipmap.m_marker)
            ));
            isFirst = false;
        } else {
            mMap.setMyLocationConfiguration(new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL,
                    true,
                    BitmapDescriptorFactory.fromResource(R.mipmap.m_marker)
            ));
        }
    }

    private Marker doShowMarker(LatLng position, int marker, MarkerOptions.MarkerAnimateType type) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(marker);

        MarkerOptions option = new MarkerOptions()
                .position(position)
                .icon(bitmap);

        option.animateType(type);
        return (Marker) mMap.addOverlay(option);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mBinder = ButterKnife.bind(this, view);
        XSearchMessageEvent.getInstance().setSelectListener(this);
        return view;
    }

    @Override
    public void onSelected(XSearchInfo info) {
        mDestination = info.getPt();

        if (!TextUtils.isEmpty(info.getAddress().trim())) {
            mDestinationAddress = info.getAddress();
        } else if (!TextUtils.isEmpty(info.getName().trim())) {
            mDestinationAddress = info.getName();
        } else {
            mDestinationAddress = "";
        }


        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
        }

        mDestinationMarker = doShowMarker(mDestination, R.mipmap.d_marker, MarkerOptions.MarkerAnimateType.jump);
        doRouteSearch();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        mBinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();
        setLocation();
    }

    private void setLocation() {
        XLocationClient.getInstance().setLocationListener(this);
    }

    private void initMap() {
        mMap = mMapView.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.showZoomControls(false);
    }

}
