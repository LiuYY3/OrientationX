package com.xmb.orientationx.activity;

import android.content.pm.PackageManager;
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
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XSearchAdaptor;
import com.xmb.orientationx.adaptor.XSearchAdaptor.ItemSelectedListener;
import com.xmb.orientationx.component.XSearchBar;
import com.xmb.orientationx.constant.XConstants;
import com.xmb.orientationx.constant.XTags;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.model.SearchInfo;
import com.xmb.orientationx.utils.XSearchUtils;
import com.xmb.orientationx.utils.XUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

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
        ItemSelectedListener {

    @BindView(R.id.id_baidu_map)
    MapView mMapView;
    @BindView(R.id.id_search_bar)
    XSearchBar mSearchBar;
    @BindView(R.id.id_search_history_list)
    RecyclerView mHistoryList;
    @BindView(R.id.id_search_txt)
    EditText mInputText;
    @BindView(R.id.id_guide_img)
    ImageView mGuideImageView;
    @BindView(R.id.id_gps_img)
    ImageView mGpsImageView;

    private BDLocation mCurrentLocation;
    private LatLng mDestinationLL;
    private BaiduMap mMap;
    private String mCurrentCityName;
    private LocationClient mLocationClient;
    private RoutePlanSearch mRoutePlanSearch;
    private SuggestionSearch mSuggestionSearch;
    private PoiSearch mPoiSearch;
    private BDLocation mLastLocation;
    private XSearchAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SuggestionInfo> mSearchSuggestions, mSearchSubSuggestions, mFinalSuggestions;
    private ArrayList<PoiInfo> mSearchPoiResults;
    private ArrayList<SearchInfo> mSearchResults;
    private MyLocationConfiguration mLocationConfig;
    private String mDestination;

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
        Log.i(XTags.TAG_MAIN, "afterTextChanged: " + mInputText.getText());
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

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (mLastLocation != null) {
            mLocationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
            mCurrentLocation = bdLocation;
            setCurrentLocation(mLocationConfig);
        } else {
            mLocationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null);
            mLastLocation = bdLocation;
            mCurrentLocation = bdLocation;
            setCurrentLocation(mLocationConfig);
        }
        locateCity();
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if (XUtils.checkEmptyList(walkingRouteResult.getRouteLines())) {
            WalkingRouteLine walk = walkingRouteResult.getRouteLines().get(0);
            if (XUtils.checkEmptyList(walk.getAllStep())) {
                for (WalkingRouteLine.WalkingStep step : walk.getAllStep()) {
                    if (XUtils.checkEmptyList(step.getWayPoints())) {
                        OverlayOptions polyline = new PolylineOptions().width(5)
                                .color(getResources().getColor(R.color.colorBlue)).points(step.getWayPoints());
                        mMap.addOverlay(polyline);
                    }
                }
            }
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        if (XUtils.checkEmptyList(transitRouteResult.getRouteLines())) {
            TransitRouteLine walk = transitRouteResult.getRouteLines().get(0);
            if (XUtils.checkEmptyList(walk.getAllStep())) {
                for (TransitRouteLine.TransitStep step : walk.getAllStep()) {
                    if (XUtils.checkEmptyList(step.getWayPoints())) {
                        OverlayOptions polyline = new PolylineOptions().width(5)
                                .color(getResources().getColor(R.color.colorGreen)).points(step.getWayPoints());
                        mMap.addOverlay(polyline);
                    }
                }
            }
        }
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
                        OverlayOptions polyline = new PolylineOptions().width(5)
                                .color(getResources().getColor(R.color.colorRed)).points(step.getWayPoints());
                        mMap.addOverlay(polyline);
                    }
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

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        mSearchSuggestions = new ArrayList<SuggestionInfo>();
        mSearchSubSuggestions = new ArrayList<SuggestionInfo>();
        if (XUtils.checkEmptyList(suggestionResult.getAllSuggestions())) {
            for (SuggestionInfo suggestionInfo : suggestionResult.getAllSuggestions()) {
                if (!suggestionInfo.city.contains(mCurrentCityName)) {
                    mSearchSubSuggestions.add(suggestionInfo);
                } else {
                    mSearchSuggestions.add(suggestionInfo);
                }
            }
            mFinalSuggestions = XSearchUtils.getBestResults(mSearchSuggestions, mSearchSubSuggestions);
            updateSearchResults();
            mAdapter.updateResults(mSearchResults);
        } else {
            mFinalSuggestions = new ArrayList<SuggestionInfo>();
            updateSearchResults();
            mAdapter.updateResults(mSearchResults);
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        mSearchPoiResults = new ArrayList<PoiInfo>();
        if (XUtils.checkEmptyList(poiResult.getAllPoi())) {
            mSearchPoiResults.addAll(poiResult.getAllPoi());
            updateSearchResults();
            mAdapter.updateResults(mSearchResults);
        } else {
            mSearchPoiResults = new ArrayList<PoiInfo>();
            updateSearchResults();
            mAdapter.updateResults(mSearchResults);
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * recycler item selected action.
     *
     * @param position item position.
     */
    @Override
    public void onItemSelected(int position) {
        mMap.clear();
        mInputText.removeTextChangedListener(this);
        mAdapter.updateResults(new ArrayList<SearchInfo>());
        mInputText.setText(mSearchResults.get(position).getName());
        mDestination = mSearchResults.get(position).getName();
        mDestinationLL = mSearchResults.get(position).getPt();
        mGuideImageView.setVisibility(View.VISIBLE);
        mGpsImageView.setVisibility(View.VISIBLE);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.marker);

        MarkerOptions option = new MarkerOptions()
                .position(mDestinationLL)
                .icon(bitmap);

        option.animateType(MarkerOptions.MarkerAnimateType.grow);

        mMap.addOverlay(option);
    }

    private void initRecyclerData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new XSearchAdaptor(mSearchResults);
        mAdapter.setListener(this);
    }

    private void initViews() {
        mGpsImageView.setVisibility(View.GONE);
        mGuideImageView.setVisibility(View.GONE);
        mHistoryList.setLayoutManager(mLayoutManager);
        mHistoryList.setAdapter(mAdapter);
        initMap();
        initRXBindings();
        mLocationClient.start();
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initMap() {
        mMapView.showZoomControls(false);
        mMap = mMapView.getMap();
        mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(false);
        mMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);
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
                    Log.i(XTags.TAG_MAIN, "locateCity: " + mCurrentCityName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRXBindings() {
        RxView.clicks(mInputText).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                mInputText.addTextChangedListener(XMainActivity.this);
            }
        });

        RxView.clicks(mGuideImageView).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                mRoutePlanSearch = RoutePlanSearch.newInstance();
                mRoutePlanSearch.setOnGetRoutePlanResultListener(XMainActivity.this);
                PlanNode stNode = PlanNode.withCityNameAndPlaceName(mCurrentCityName, mCurrentLocation.getAddrStr());

                PlanNode enNode = PlanNode.withCityNameAndPlaceName(mCurrentCityName, mDestination);
//                mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
//                        .from(stNode)
//                        .to(enNode));
                LatLng ll = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                WalkNaviLaunchParam param = new WalkNaviLaunchParam().stPt(ll).endPt(mDestinationLL);
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
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(mCurrentLocation.getRadius())
                .latitude(mCurrentLocation.getLatitude())
                .longitude(mCurrentLocation.getLongitude()).build();
        mMap.setMyLocationData(locData);
        mMap.setMyLocationConfiguration(config);
    }

    private void updateSearchResults() {
        ArrayList<String> sugKeys = new ArrayList<String>();
        mSearchResults = new ArrayList<SearchInfo>();

        if (XUtils.checkEmptyList(mFinalSuggestions)) {
            for (SuggestionInfo suggestionInfo : mFinalSuggestions) {
                SearchInfo searchInfo = new SearchInfo();
                if (suggestionInfo.pt != null) {
                    searchInfo.setPt(suggestionInfo.pt);
                    searchInfo.setName(suggestionInfo.key);
                    sugKeys.add(suggestionInfo.key);
                    mSearchResults.add(searchInfo);
                }
            }
        }

        if (XUtils.checkEmptyList(mSearchPoiResults)) {
            for (PoiInfo poiInfo : mSearchPoiResults) {
                SearchInfo searchInfo = new SearchInfo();
                if (poiInfo.location != null && !sugKeys.contains(poiInfo.name)) {
                    searchInfo.setPt(poiInfo.location);
                    searchInfo.setName(poiInfo.name);
                    mSearchResults.add(searchInfo);
                }
            }
        }
    }

}
