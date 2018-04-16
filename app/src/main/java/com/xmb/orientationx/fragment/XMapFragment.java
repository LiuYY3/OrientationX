package com.xmb.orientationx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.xmb.orientationx.R;
import com.xmb.orientationx.activity.XBikeGuideActivity;
import com.xmb.orientationx.activity.XGuideActivity;
import com.xmb.orientationx.activity.XMapActivity;
import com.xmb.orientationx.activity.XWalkGuideActivity;
import com.xmb.orientationx.broadcast.XLocationClient;
import com.xmb.orientationx.constant.XTags;
import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.interfaces.XClickListener;
import com.xmb.orientationx.interfaces.XLocationListener;
import com.xmb.orientationx.interfaces.XMultiLocationListener;
import com.xmb.orientationx.interfaces.XSelectListener;
import com.xmb.orientationx.interfaces.XSwitchListener;
import com.xmb.orientationx.message.XBusInfoMessageEvent;
import com.xmb.orientationx.message.XClickMessageEvent;
import com.xmb.orientationx.message.XMultiLocationMessageEvent;
import com.xmb.orientationx.message.XSearchMessageEvent;
import com.xmb.orientationx.message.XSwitchMessageEvent;
import com.xmb.orientationx.utils.XAppDataUtils;
import com.xmb.orientationx.utils.XUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lym on 2018/02/08.
 */

public class XMapFragment extends Fragment implements XLocationListener,
        XSelectListener,
        XClickListener,
        XSwitchListener,
        XMultiLocationListener,
        OnGetRoutePlanResultListener {

    @BindView(R.id.id_main_map)
    MapView mMapView;

    private String mLocateCity = "盐城";
    private static final String APP_FOLDER_NAME = "XMapFragment";
    private HashMap<Integer, Double> mDistanceMap = new HashMap<>();

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
    private PlanNode mStNode, mEnNode;
    private String mSDCardPath;
    private GeoCoder mGeoCoder;
    private WalkNavigateHelper mWalkHelper;
    private BikeNavigateHelper mBikeHelper;
    private double mPtsDistance = 0;
    private double mFirstPtsDistance = 0;
    private double mSecondPtsDistance = 0;
    private List<LatLng> mFirstLine;
    private List<LatLng> mSecondLine;
    private double mStartLat;
    private double mStartLng;
    private String mStartAddress;
    private LatLng mStartPosition;

    private boolean isFirst = true;

    @Override
    public void onSwitch(String style){
        switch (style){
            case "a":
                mMap.setBaiduHeatMapEnabled(true);
                break;
            case "b":
                mMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                mMap.setBaiduHeatMapEnabled(false);
                break;
            default:
                mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mMap.setBaiduHeatMapEnabled(false);
                break;
        }
    }

    @Override
    public void onLocations(XSearchInfo[] xSearchInfos) {
        mMap.clear();

        Log.i("Three", "onLocations: " + "I am Here !");

        if (xSearchInfos[0].getPt() != null) {
            doShowMarker(xSearchInfos[0].getPt(), R.mipmap.d_marker, MarkerOptions.MarkerAnimateType.jump);
        }

        if (xSearchInfos[1].getPt() != null) {
            doShowMarker(xSearchInfos[1].getPt(), R.mipmap.d_marker, MarkerOptions.MarkerAnimateType.jump);
        }

        if (xSearchInfos[2].getPt() != null) {
            doShowMarker(xSearchInfos[2].getPt(), R.mipmap.d_marker, MarkerOptions.MarkerAnimateType.jump);
        }

//        if (xSearchInfos[3].getPt() != null) {
//            doShowMarker(xSearchInfos[3].getPt(), R.mipmap.d_marker, MarkerOptions.MarkerAnimateType.jump);
//        }
//
//        if (xSearchInfos[4].getPt() != null) {
//            doShowMarker(xSearchInfos[4].getPt(), R.mipmap.d_marker, MarkerOptions.MarkerAnimateType.jump);
//        }

        if (xSearchInfos[0] != null && xSearchInfos[1] != null && xSearchInfos[2] != null) {
            doMultiRouteSearch(R.color.colorBlack, xSearchInfos[0].getPt(), xSearchInfos[1].getPt());
            doMultiRouteSearch(R.color.colorRed, xSearchInfos[1].getPt(), xSearchInfos[2].getPt());
            doMultiRouteSearch(R.color.colorBlue, xSearchInfos[0].getPt(), xSearchInfos[2].getPt());
        }

//        if (xSearchInfos[2].getPt() != null && xSearchInfos[3].getPt() != null) {
//            doMultiRouteSearch(R.color.colorBlue, xSearchInfos[2].getPt(), xSearchInfos[3].getPt());
//        }
//
//        if (xSearchInfos[3].getPt() != null && xSearchInfos[4].getPt() != null) {
//            doMultiRouteSearch(R.color.colorGreen, xSearchInfos[3].getPt(), xSearchInfos[4].getPt());
//        }
    }

    private void doMultiRouteSearch(final int rcolor, final LatLng st, LatLng ed) {
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
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
                        double temp = 0;
                        List<LatLng> tempL = new ArrayList<>();
                        for (DrivingRouteLine.DrivingStep step : drive.getAllStep()) {
                            if (XUtils.checkEmptyList(step.getWayPoints())) {

                                Log.i("Three", "onGetDrivingRouteResult: " + "I am Here !");
                                for (int i = 0; i < step.getWayPoints().size() - 1; i++) {
                                    Log.i("Three", "onGetDrivingRouteResult: " + step.getWayPoints().size());
                                    temp = temp + DistanceUtil.getDistance(step.getWayPoints().get(i), step.getWayPoints().get(i + 1));
                                    tempL.add(step.getWayPoints().get(i));
                                }
                                tempL.add(step.getWayPoints().get(step.getWayPoints().size() - 1));
                            }
                        }

                        if (temp < mFirstPtsDistance || mFirstPtsDistance == 0) {
                            mFirstPtsDistance = temp;
                            mFirstLine = tempL;
                        } else {
                            if (temp < mSecondPtsDistance || mSecondPtsDistance == 0) {
                                mSecondPtsDistance = temp;
                                mSecondLine = tempL;
                            }
                        }

                        if (rcolor == R.color.colorBlue) {
                            mPolyline = new PolylineOptions().width(8)
                                    .color(getResources().getColor(R.color.colorBlack)).points(mFirstLine);
                            mRoute = mMap.addOverlay(mPolyline);
                            mRoutes.add(mRoute);
                            mPolyline = new PolylineOptions().width(8)
                                    .color(getResources().getColor(R.color.colorRed)).points(mSecondLine);
                            mRoute = mMap.addOverlay(mPolyline);
                            mRoutes.add(mRoute);
                        }
                    }
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
        mStNode = PlanNode.withLocation(st);
        mEnNode = PlanNode.withLocation(ed);

        mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(mStNode)
                .to(mEnNode)
        );
    }

    private OnGetGeoCoderResultListener mGeoListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

        }
    };

    @Override
    public void onClick(int click) {
        if (mDestination != null) {
            switch (click) {
                case 0:
                    if (BaiduNaviManager.isNaviInited()) {
                        BNRoutePlanNode stNode = new BNRoutePlanNode(mMyPosition.longitude,
                                mMyPosition.latitude,
                                mMyLocation.getAddrStr(),
                                null,
                                BNRoutePlanNode.CoordinateType.GCJ02);

                        BNRoutePlanNode endNode = new BNRoutePlanNode(mDestination.longitude,
                                mDestination.latitude,
                                mDestinationAddress,
                                null,
                                BNRoutePlanNode.CoordinateType.GCJ02);
                        doStartNavigator(stNode, endNode);
                    }
                    break;
                case 2:
                    BikeNaviLaunchParam param = new BikeNaviLaunchParam().stPt(mMyPosition).endPt(mDestination);
                    doBikeNavigator(param);
                    break;
                default:
                    PlanNode st = PlanNode.withLocation(mMyPosition);
                    PlanNode ed = PlanNode.withLocation(mDestination);

                    mRoutePlanSearch.masstransitSearch((new MassTransitRoutePlanOption().from(st).to(ed)));
                    break;
            }
        }
    }

    private void doBikeNavigator(final BikeNaviLaunchParam param) {
        mBikeHelper = BikeNavigateHelper.getInstance();
        mBikeHelper.initNaviEngine(this.getActivity(), new IBEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                bikeRoutePlan(param);
            }

            @Override
            public void engineInitFail() {

            }
        });
    }

    private void bikeRoutePlan(BikeNaviLaunchParam param) {
        mBikeHelper.routePlanWithParams(param, new IBRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {

            }

            @Override
            public void onRoutePlanSuccess() {
                Intent intent = new Intent(XMapFragment.this.getActivity(), XBikeGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError bikeRoutePlanError) {

            }
        });
    }

    private void walkRoutePlan(WalkNaviLaunchParam param) {
        mWalkHelper.routePlanWithParams(param, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {

            }

            @Override
            public void onRoutePlanSuccess() {
                Intent intent = new Intent(XMapFragment.this.getActivity(), XWalkGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {

            }
        });
    }

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
        if (XUtils.checkEmptyList(massTransitRouteResult.getRouteLines())) {
            MassTransitRouteLine mass = massTransitRouteResult.getRouteLines().get(0);
            if (XUtils.checkEmptyList(mass.getNewSteps())) {
                String busInfo = "";
                for (List<MassTransitRouteLine.TransitStep> massList : mass.getNewSteps()) {
                    if (XUtils.checkEmptyList(massList)) {
                        for (MassTransitRouteLine.TransitStep step : massList) {
                            if (!TextUtils.isEmpty(step.getInstructions())) {
                                busInfo = busInfo + "\n" + step.getInstructions();
                            }
                        }
                    }
                }
                XBusInfoMessageEvent.getInstance().setInfo(busInfo);
            }
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (XUtils.checkEmptyList(drivingRouteResult.getRouteLines())) {
            DrivingRouteLine drive = drivingRouteResult.getRouteLines().get(0);
            if (XUtils.checkEmptyList(drive.getAllStep())) {
                mPtsDistance = 0;
                for (DrivingRouteLine.DrivingStep step : drive.getAllStep()) {
                    if (XUtils.checkEmptyList(step.getWayPoints())) {
                        mPolyline = new PolylineOptions().width(8)
                                .color(getResources().getColor(R.color.colorHoloBlue)).points(step.getWayPoints());
                        mRoute = mMap.addOverlay(mPolyline);
                        for (int i = 0; i < step.getWayPoints().size() - 1; i++) {
                            mPtsDistance = mPtsDistance + DistanceUtil.getDistance(step.getWayPoints().get(i), step.getWayPoints().get(i + 1));
                        }
                        mRoutes.add(mRoute);
                    }
                }
                XAppDataUtils.getInstance().setDistance(mPtsDistance);
                Log.i("Distance", "onGetDrivingRouteResult: " + mPtsDistance);
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
        mStNode = PlanNode.withLocation(mMyPosition);
        mEnNode = PlanNode.withLocation(mDestination);

        mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(mStNode)
                .to(mEnNode)
        );
    }

    @Override
    public void onLocationChange(BDLocation location) {
        mMyLocation = location;
        mMyPosition = new LatLng(location.getLatitude(), location.getLongitude());
        XAppDataUtils.getInstance().setsPt(mMyPosition);
        XAppDataUtils.getInstance().setsAdr(mMyLocation.getAddrStr());
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
        XClickMessageEvent.getInstance().setClickListener(this);
        XSwitchMessageEvent.getInstance().setSwitchListener(this);
        XMultiLocationMessageEvent.getInstance().setMultiLocationListener(this);
        if (initDirs()) {
            initNavigator();
        }
        return view;
    }

    @Override
    public void onSelected(XSearchInfo info) {
        mDestination = info.getPt();
        XAppDataUtils.getInstance().setePt(mDestination);
        XAppDataUtils.getInstance().setLinearDistance(DistanceUtil.getDistance(mMyPosition, mDestination));

        if (!TextUtils.isEmpty(info.getAddress().trim())) {
            mDestinationAddress = info.getAddress();
        } else if (!TextUtils.isEmpty(info.getName().trim())) {
            mDestinationAddress = info.getName();
        } else {
            mDestinationAddress = "";
        }

        XAppDataUtils.getInstance().seteAdr(mDestinationAddress);
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
        int mapStyle = BaiduMap.MAP_TYPE_NORMAL;
        mMap = mMapView.getMap();
        mMap.setMyLocationEnabled(true);
        switch (XAppDataUtils.getInstance().getStyle()) {
            case "a":
                mMap.setBaiduHeatMapEnabled(true);
                break;
            case "b":
                mMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                mMap.setBaiduHeatMapEnabled(false);
                break;
            default:
                mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mMap.setBaiduHeatMapEnabled(false);
                break;
        }
        mMapView.showZoomControls(false);
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initNavigator() {
        BaiduNaviManager.getInstance().init(this.getActivity(), mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int i, String s) {
                Log.i(XTags.MAP, "onAuthResult " + i + " : " + s);
            }

            @Override
            public void initStart() {

            }

            @Override
            public void initSuccess() {
                Log.i(XTags.MAP, "initSuccess");
            }

            @Override
            public void initFailed() {
                Log.i(XTags.MAP, "initFailed");
            }
        }, null, null, null);
    }

    private void doStartNavigator(BNRoutePlanNode stNode, BNRoutePlanNode endNode) {
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(stNode);
        list.add(endNode);
        BaiduNaviManager.getInstance().launchNavigator(this.getActivity(), list, 1, true, new XRoutePlanListener(stNode));
    }

    public class XRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public XRoutePlanListener(BNRoutePlanNode node){
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            Log.i(XTags.MAP, "onJumpToNavigator");
            Intent intent = new Intent(XMapFragment.this.getContext(), XGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("plan", (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            Toast.makeText(XMapFragment.this.getContext(), "Cannot Open Route Guide !", Toast.LENGTH_SHORT).show();
        }
    }

}
