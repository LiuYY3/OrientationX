package com.xmb.orientationx.activity;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.view.RxView;
import com.xmb.orientationx.model.City;
import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XSearchAdaptor;
import com.xmb.orientationx.component.XSearchBar;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.functions.Action1;

/**
 * XMapActivity.
 * subclass of {@link XBaseActivity}
 * @author 徐梦笔
 */
public class XMapActivity extends XBaseActivity implements BDLocationListener, LocationListener, OnGetPoiSearchResultListener, TextWatcher{

    private static final String TAG = "Map";
    private MapView mCenterMapView;
    private XSearchBar mMapSearchBar;
    private BaiduMap mBaiduMap;
    private UiSettings mMapUIsettings;
    private PoiSearch mPoiSearch;
    private TextView mMiddleTextView;
    private RelativeLayout mHeadLayout;
    private EditText mInputEditText;
    private ImageView mSearchImageView;
    private LocationMode mMode;
    private BitmapDescriptor mMarkBitMap;
    private Location mLocation;
    private RecyclerView mSearchHistroyListView;
    private XSearchAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> mDataList = new ArrayList<String>();
    private ArrayList<LatLng> mPointLocationList = new ArrayList<>();
    private String mCurrentCity;
    private String mPossibleCityName;
    private List<City> mAllCities;
    public LocationClient mLocationClient = null;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorTransparent, getTheme()));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        initViews();
    }

    @Override
    public void onResumeBase() throws XBaseException {
        super.onResumeBase();
        mCenterMapView.onResume();
    }

    @Override
    public void onDestroyBase() throws XBaseException {
        super.onDestroyBase();
        mCenterMapView.onDestroy();
        mPoiSearch.destroy();
    }

    @Override
    public void onPauseBase() throws XBaseException {
        super.onPauseBase();
        mCenterMapView.onPause();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        Log.i(TAG, "onReceiveLocation: " + location.getLocTypeDescription());
        setCurrentLocation(location);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStartBase() throws XBaseException {
        super.onStartBase();
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult.getAllPoi() == null) {
            Log.i(TAG, "onGetPoiResult: null");
            mDataList.clear();
            mDataList = new ArrayList<>();
            mPointLocationList = new ArrayList<>();
            for (City city : mAllCities) {
                if (mInputEditText.getText().toString().contains(mCurrentCity.replace("市", ""))) {
                    PoiSearch poiSearch = PoiSearch.newInstance();
                    poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                        @Override
                        public void onGetPoiResult(PoiResult poiResult) {
                            if (poiResult.getAllPoi() == null) {
                                Log.i(TAG, "onGetPoiResult: null");
                            } else {
                                for (PoiInfo poiInfo: poiResult.getAllPoi()) {
                                    LatLng point = poiInfo.location;

                                    if (point == null) {
                                        Log.i(TAG, "onGetPoiResult: null point");
                                    } else {
                                        mPointLocationList.add(point);
                                        mDataList.add(poiInfo.name);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

                        }

                        @Override
                        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

                        }
                    });
                    poiSearch.searchInCity(new PoiCitySearchOption()
                            .city(city.getName()).keyword(mInputEditText.getText().toString()));
                }
            }
            mAdapter.updateData(mDataList, mPointLocationList);
            mBaiduMap.clear();
        } else {
            mDataList.clear();
            for (PoiInfo poiInfo: poiResult.getAllPoi()) {
                LatLng point = poiInfo.location;
                
                if (point == null) {
                    Log.i(TAG, "onGetPoiResult: null point");
                } else {
                    mPointLocationList.add(point);
                    mDataList.add(poiInfo.name);
                    mAdapter.updateData(mDataList, mPointLocationList);
                }

            }
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.i(TAG, "afterTextChanged: " + mInputEditText.getText().toString());
        Log.i(TAG, "afterTextChanged: " + mCurrentCity.replace("市", ""));
        startSearch(mCurrentCity.replace("市", ""), mInputEditText.getText().toString());
    }

    private void initViews() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(this);
        setLocationClient();
        mLocationClient.start();
        initCurrentLocation();
        mCenterMapView = (MapView) this.findViewById(R.id.id_baidu_map);
        mMapSearchBar = (XSearchBar) this.findViewById(R.id.id_search_bar);
        mMiddleTextView = (TextView) this.findViewById(R.id.id_middle_txt);
        mInputEditText = (EditText) this.findViewById(R.id.id_search_txt);
        mSearchImageView = (ImageView) this.findViewById(R.id.id_search_img);
        mSearchHistroyListView = (RecyclerView) this.findViewById(R.id.id_search_history_list);
        initMapView();
        mInputEditText.addTextChangedListener(this);
        initRecyclerView();
        initTitleView();
        initData();
        initRXBindings();
    }

    private void initData() {
        Gson gson = new Gson();
        String namelist = Utils.getJson(this, "city.json");
        String list = namelist.substring(namelist.indexOf("["), namelist.indexOf("]") + 1);
        Log.i(TAG, "afterTextChanged: " + list);
        Type type = new TypeToken<List<City>>(){}.getType();
        mAllCities = gson.fromJson(list,  type);
    }

    private void initSearchBar() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new XSearchAdaptor(mDataList, mPointLocationList, mInputEditText, mSearchHistroyListView, mBaiduMap);
        mSearchHistroyListView.setLayoutManager(mLayoutManager);
        mSearchHistroyListView.setAdapter(mAdapter);
    }

    private void initMapView() {
        mCenterMapView.showZoomControls(false);
        mBaiduMap = mCenterMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapUIsettings = mBaiduMap.getUiSettings();
        mMapUIsettings.setCompassEnabled(false);
        mBaiduMap.setMyLocationEnabled(true);
        mMode = LocationMode.FOLLOWING;
        mMarkBitMap = BitmapDescriptorFactory.fromResource(R.drawable.marker_self);
    }

    private void initTitleView() {
        XMapActivity.this.showSearchBar(true);
    }

    private void initRXBindings() {
        RxView.clicks(mSearchImageView).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mBaiduMap.clear();
                PoiSearch poiSearch = PoiSearch.newInstance();
                poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                    @Override
                    public void onGetPoiResult(PoiResult poiResult) {
                        if (poiResult.getAllPoi() == null) {
                            Log.i(TAG, "onGetPoiResult: null");
                        } else {
                            for (PoiInfo poiInfo: poiResult.getAllPoi()) {
                                LatLng point = poiInfo.location;

                                if (point == null) {
                                    Log.i(TAG, "onGetPoiResult: null point");
                                } else {

                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                                            .fromResource(R.drawable.marker_self);

                                    OverlayOptions option = new MarkerOptions()
                                            .position(point)
                                            .icon(bitmap);

                                    mBaiduMap.addOverlay(option);
                                }
                            }
                        }
                    }

                    @Override
                    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

                    }

                    @Override
                    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

                    }
                });
                poiSearch.searchInCity(new PoiCitySearchOption()
                        .city(mCurrentCity.replace("市", "")).keyword(mInputEditText.getText().toString()));
                mInputEditText.setText("");
                mAdapter.updateData(new ArrayList<String>(), new ArrayList<LatLng>());
            }
        });
        RxView.clicks(mInputEditText).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mSearchHistroyListView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startSearch(String city, String keyword) {
        initSearchBar();
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(city)
                .keyword(keyword));
    }

    private void setLocationClient() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd0911");
        option.disableCache(true);
        option.setScanSpan(2000);
        option.setOpenGps(true);
        option.setEnableSimulateGps(true);
        mLocationClient.setLocOption(option);
    }

    private void setCurrentLocation(BDLocation location) {
        Log.i(TAG, "setCurrentLocation: " + location.getLatitude() + "; " + location.getLongitude());
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        MyLocationConfiguration config = new MyLocationConfiguration(mMode, true, null);
        mBaiduMap.setMyLocationConfiguration(config);
    }

    private void initCurrentLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        PackageManager packageManager = getPackageManager();
        boolean permission1 = (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.xmb.orientationx"));
        boolean permission2 = (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.ACCESS_COARSE_LOCATION", "com.xmb.orientationx"));

        mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mLocation == null) {
            mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        Log.i(TAG, "initCurrentLocation: " + mLocation.getLatitude() + "; " + mLocation.getLongitude());
        List<Address> result = null;
        try {
            if (mLocation != null) {
                Geocoder gc = new Geocoder(this, Locale.CHINA);
                result = gc.getFromLocation(mLocation.getLatitude(),
                        mLocation.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null) {
            for (Address address : result) {
                mCurrentCity = address.getLocality();
            }
        }
    }

    private class RoutePlanTool implements BaiduNaviManager.RoutePlanListener{

        @Override
        public void onJumpToNavigator() {

        }

        @Override
        public void onRoutePlanFailed() {

        }

    }

}
