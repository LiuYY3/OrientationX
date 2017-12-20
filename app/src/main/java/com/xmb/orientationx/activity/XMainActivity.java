package com.xmb.orientationx.activity;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.jakewharton.rxbinding.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XSearchAdaptor;
import com.xmb.orientationx.application.XApplication;
import com.xmb.orientationx.component.XSearchBar;
import com.xmb.orientationx.constant.XConstants;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.utils.XSearchUtils;
import com.xmb.orientationx.utils.XUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.functions.Action1;

/**
 * XMainActivity.
 * extends from {@link XBaseActivity}
 * @author 徐梦笔
 */
public class XMainActivity extends XBaseActivity implements BDLocationListener,
        OnGetRoutePlanResultListener,
        TextWatcher,
        OnGetSuggestionResultListener,
        OnGetPoiSearchResultListener,
        SensorEventListener {

    private BDLocation mCurrentLocation;
    private BDLocation mDestinationLocation;
    private MapView mMapView;
    private BaiduMap mMap;
    private XSearchBar mSearchBar;
    private RecyclerView mHistoryList;
    private EditText mInputText;
    private ImageView mSearchIcon;
    private String mCurrentCityName;
    private LocationClient mLocationClient;
//    private RoutePlanSearch mRoutePlanSearch;
//    private Polyline mPolyline;
    private SuggestionSearch mSuggestionSearch;
    private PoiSearch mPoiSearch;
    private BDLocation mLastLocation;
    private XSearchAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SuggestionInfo> mSearchSuggestions, mSearchSubSuggestions, mFinalSuggestions;
    private ArrayList<PoiInfo> mSearchPoiResults;
    private MyLocationConfiguration mLocationConfig;
    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;
    private Float mDirection;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_map);
        initRecyclerData();
        initViews();
    }

    @Override
    public void onDestroyBase() throws XBaseException {
        super.onDestroyBase();
        mLocationClient.stop();
        mMapView.onDestroy();
        mInputText.removeTextChangedListener(this);
        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onStartBase() throws XBaseException {
        super.onStartBase();
    }

    @Override
    public void onStopBase() throws XBaseException {
        super.onStopBase();
    }

    @Override
    public void onPauseBase() throws XBaseException {
        super.onPauseBase();
        mMapView.onPause();
    }

    @Override
    public void onResumeBase() throws XBaseException {
        super.onResumeBase();
        mMapView.onResume();
    }

    @Override
    public void onRestartBase() throws XBaseException {
        super.onRestartBase();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.i(XConstants.TAG_MAIN, "afterTextChanged: " + mInputText.getText());
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword(mInputText.getText().toString())
                .city(mCurrentCityName));
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .keyword(mInputText.getText().toString())
                .city(mCurrentCityName));
    }

    /**
     * Called when pointer capture is enabled or disabled for the current window.
     *
     * @param hasCapture True if the window has pointer capture.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     * <p>
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mDirection = (float) Math.toDegrees(event.values[2]);
            Log.i(XConstants.TAG_MAIN, "onSensorChanged: " + mDirection);
            setCurrentLocation(mLocationConfig);
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     * <p>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.i(XConstants.TAG_MAIN, "onReceiveLocation: " + bdLocation.getAddress().address + " : " + bdLocation.getRadius());
        if (mLastLocation != null) {
            Log.i(XConstants.TAG_MAIN, "onReceiveLocation: Modify Location");
            mLocationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
            mCurrentLocation = bdLocation;
        } else {
            mLocationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null);
            mLastLocation = bdLocation;
            mCurrentLocation = bdLocation;
            if (mSensorManager != null) {
                mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                mSensorManager.registerListener(this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
        locateCity();
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
//        if (XUtils.checkEmptyList(drivingRouteResult.getRouteLines())) {
//            DrivingRouteLine drive = drivingRouteResult.getRouteLines().get(0);
//            if (XUtils.checkEmptyList(drive.getAllStep())) {
//                for (DrivingRouteLine.DrivingStep step : drive.getAllStep()) {
//                    if (XUtils.checkEmptyList(step.getWayPoints())) {
//                        for (LatLng ll : step.getWayPoints()) {
//                            Log.i(XConstants.TAG_MAIN, "onGetDrivingRouteResult: " + ll.latitude + " : " + ll.longitude);
//                        }
//                        OverlayOptions ooPolyline = new PolylineOptions().width(5)
//                                .color(0xAAFF0000).points(step.getWayPoints());
//                        mPolyline = (Polyline) mMap.addOverlay(ooPolyline);
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        mSearchSuggestions = new ArrayList<SuggestionInfo>();
        mSearchSubSuggestions = new ArrayList<SuggestionInfo>();
        if (XUtils.checkEmptyList(suggestionResult.getAllSuggestions())) {
            for (SuggestionInfo suggestionInfo : suggestionResult.getAllSuggestions()) {
                Log.i(XConstants.TAG_MAIN, "onGetSuggestionResult: " + suggestionInfo.city + " : " + suggestionInfo.key);
                if (!suggestionInfo.city.contains(mCurrentCityName)) {
                    mSearchSubSuggestions.add(suggestionInfo);
                } else {
                    mSearchSuggestions.add(suggestionInfo);
                }
            }
            mFinalSuggestions = XSearchUtils.getBestResults(mSearchSuggestions, mSearchSubSuggestions);
            mAdapter.updateSuggestions(mFinalSuggestions);
        } else {
            mAdapter.updateSuggestions(new ArrayList<SuggestionInfo>());
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        mSearchPoiResults = new ArrayList<PoiInfo>();
        if (XUtils.checkEmptyList(poiResult.getAllPoi())) {
            mSearchPoiResults.addAll(poiResult.getAllPoi());
            mAdapter.updatePoiResults(mSearchPoiResults);
        } else {
            mAdapter.updatePoiResults(new ArrayList<PoiInfo>());
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    private void initRecyclerData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new XSearchAdaptor(mFinalSuggestions, mSearchPoiResults);
    }

    private void initViews() {
        mSensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        mMapView = (MapView) this.findViewById(R.id.id_baidu_map);
        mSearchBar = (XSearchBar) this.findViewById(R.id.id_search_bar);
        mInputText = (EditText) this.findViewById(R.id.id_search_txt);
        mSearchIcon = (ImageView) this.findViewById(R.id.id_search_img);
        mHistoryList = (RecyclerView) this.findViewById(R.id.id_search_history_list);
        mHistoryList.setLayoutManager(mLayoutManager);
        mHistoryList.setAdapter(mAdapter);
        mSearchIcon.setVisibility(View.GONE);
        initMap();
        initRXBindings();
        mLocationClient.start();
        mInputText.addTextChangedListener(XMainActivity.this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
//        mRoutePlanSearch = RoutePlanSearch.newInstance();
//        mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "西二旗地铁站");
//
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "知春路地铁站");
//        mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
//                .from(stNode)
//                .to(enNode));
    }

    private void initMap() {
        mMapView.showZoomControls(false);
        mMap = mMapView.getMap();
        mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(false);
        mMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(XApplication.getContext());
        setLocationClient();
        mLocationClient.registerLocationListener(this);
    }

    private void locateCity() {
        PackageManager packageManager = getPackageManager();
        boolean permission1 = (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", XConstants.PACKAGE_NAME));
        boolean permission2 = (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.ACCESS_COARSE_LOCATION", XConstants.PACKAGE_NAME));
        try {
            Geocoder gc = new Geocoder(this, Locale.CHINA);
            List<Address> result = gc.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
            if (XUtils.checkEmptyList(result)) {
                for (Address address : result) {
                    mCurrentCityName = address.getLocality().replace("市", "");
                    Log.d(XConstants.TAG_MAIN, "locateCity: " + mCurrentCityName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRXBindings() {
        RxView.clicks(mInputText).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mSearchIcon.setVisibility(View.VISIBLE);
            }
        });

        RxView.clicks(mSearchIcon).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

            }
        });

    }

    private void setLocationClient() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd0911");
        option.setScanSpan(1000);
        option.disableCache(true);
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(false);
        mLocationClient.setLocOption(option);
    }

    private void setCurrentLocation(MyLocationConfiguration config) {
        if (mDirection == null) {
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(mCurrentLocation.getRadius())
                    .latitude(mCurrentLocation.getLatitude())
                    .longitude(mCurrentLocation.getLongitude()).build();
            mMap.setMyLocationData(locData);
        } else {
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(mCurrentLocation.getRadius())
                    .direction(mDirection)
                    .latitude(mCurrentLocation.getLatitude())
                    .longitude(mCurrentLocation.getLongitude()).build();
            mMap.setMyLocationData(locData);
        }
        mMap.setMyLocationConfiguration(config);
    }

    private void showList() {
        if (mInputText.getText().length() == 0) {
            mHistoryList.setVisibility(View.GONE);
        } else {
            mHistoryList.setVisibility(View.VISIBLE);
        }
    }

}
