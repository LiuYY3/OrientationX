package com.xmb.orientationx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XMultiSearchAdaptor;
import com.xmb.orientationx.broadcast.XLocationClient;
import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.interfaces.XCityListener;
import com.xmb.orientationx.interfaces.XMultiLocationListener;
import com.xmb.orientationx.message.XMultiLocationMessageEvent;
import com.xmb.orientationx.utils.XSearchUtils;
import com.xmb.orientationx.utils.XUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by lym on 2018/03/28.
 */
public class XMultiLocationActivity extends XBaseActivity implements XCityListener, OnGetSuggestionResultListener, OnGetPoiSearchResultListener, XMultiSearchAdaptor.MultiSelectedListener{

    @BindView(R.id.id_start_edit_txt)
    EditText mStartPtEditText;
    @BindView(R.id.id_first_edit_txt)
    EditText mFirstPtEditText;
    @BindView(R.id.id_second_edit_txt)
    EditText mSecondPtEditText;
    @BindView(R.id.id_third_edit_txt)
    EditText mThirdPtEditText;
    @BindView(R.id.id_forth_edit_txt)
    EditText mForthPtEditText;
    @BindView(R.id.id_begin_all_journey_btn)
    Button mBeginRouteButton;
    @BindView(R.id.id_all_search_result_recycle)
    RecyclerView mAllRecyclerView;
    @BindString(R.string.app_guideplan)
    String guideplan;
    @BindView(R.id.id_activity_left_icon)
    ImageView mBackImagView;

    private static final long CLICK_GAP = 500;

    private String mLocateCity = "盐城";

    private XMultiSearchAdaptor mAdapter;
    private XSearchInfo[] mFiveLocation = new XSearchInfo[5];
    private int mStyle;
    private RecyclerView.LayoutManager mLayoutManager;
    private SuggestionSearch mSuggestionSearch;
    private PoiSearch mPoiSearch;
    private ArrayList<PoiInfo> mSearchPoiResults;
    private ArrayList<SuggestionResult.SuggestionInfo> mSearchSuggestions, mSearchSubSuggestions, mFinalSuggestions;
    private ArrayList<XSearchInfo> mSearchResults;

    @Override
    public void onItemSelected(int style, int position) {
        switch (style) {
            case 0:
                mStartPtEditText.setText(mSearchResults.get(position).getName());
                mStartPtEditText.setEnabled(false);
                mFiveLocation[0] = mSearchResults.get(position);
                break;
            case 1:
                mFirstPtEditText.setText(mSearchResults.get(position).getName());
                mFirstPtEditText.setEnabled(false);
                mFiveLocation[1] = mSearchResults.get(position);
                break;
            case 2:
                mSecondPtEditText.setText(mSearchResults.get(position).getName());
                mSecondPtEditText.setEnabled(false);
                mFiveLocation[2] = mSearchResults.get(position);
                break;
            case 3:
                mThirdPtEditText.setText(mSearchResults.get(position).getName());
                mThirdPtEditText.setEnabled(false);
                mFiveLocation[3] = mSearchResults.get(position);
                break;
            case 4:
                mForthPtEditText.setText(mSearchResults.get(position).getName());
                mForthPtEditText.setEnabled(false);
                mFiveLocation[4] = mSearchResults.get(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCityChanged(String cityname) {
        mLocateCity = cityname;
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        mSearchPoiResults = new ArrayList<PoiInfo>();
        if (XUtils.checkEmptyList(poiResult.getAllPoi())) {
            mSearchPoiResults.addAll(poiResult.getAllPoi());
            updateSearchResults();
            mAdapter.updateResults(mStyle, mSearchResults);
        } else {
            mSearchPoiResults = new ArrayList<PoiInfo>();
            updateSearchResults();
            mAdapter.updateResults(mStyle, mSearchResults);
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        mSearchSuggestions = new ArrayList<SuggestionResult.SuggestionInfo>();
        mSearchSubSuggestions = new ArrayList<SuggestionResult.SuggestionInfo>();
        if (XUtils.checkEmptyList(suggestionResult.getAllSuggestions())) {
            for (SuggestionResult.SuggestionInfo suggestionInfo : suggestionResult.getAllSuggestions()) {
                if (!suggestionInfo.city.contains(mLocateCity)) {
                    mSearchSubSuggestions.add(suggestionInfo);
                } else {
                    mSearchSuggestions.add(suggestionInfo);
                }
            }
            mFinalSuggestions = XSearchUtils.getBestResults(mSearchSuggestions, mSearchSubSuggestions);
            updateSearchResults();
            mAdapter.updateResults(mStyle, mSearchResults);
        } else {
            mFinalSuggestions = new ArrayList<SuggestionResult.SuggestionInfo>();
            updateSearchResults();
            mAdapter.updateResults(mStyle, mSearchResults);
        }
    }

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_multi_location);
        ButterKnife.bind(this);
        XLocationClient.getInstance().setCityListener(this);
        initRxBindings();
        initViews();
    }

    private void initViews() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new XMultiSearchAdaptor(this, 0, mSearchResults);
        mAdapter.setListener(this);
        mAllRecyclerView.setLayoutManager(mLayoutManager);
        mAllRecyclerView.setAdapter(mAdapter);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        this.showTitle(true, guideplan);
        this.showLeftIcon(true, R.mipmap.ic_action_arrow_left);
    }

    private void initRxBindings() {
        RxTextView.textChanges(mStartPtEditText)
                .debounce(CLICK_GAP, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        mStyle = 0;
                        doSearch(charSequence.toString());
                    }
                });
        RxTextView.textChanges(mFirstPtEditText)
                .debounce(CLICK_GAP, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        mStyle = 1;
                        doSearch(charSequence.toString());
                    }
                });
        RxTextView.textChanges(mSecondPtEditText)
                .debounce(CLICK_GAP, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        mStyle = 2;
                        doSearch(charSequence.toString());
                    }
                });
        RxTextView.textChanges(mThirdPtEditText)
                .debounce(CLICK_GAP, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        mStyle = 3;
                        doSearch(charSequence.toString());
                    }
                });
        RxTextView.textChanges(mForthPtEditText)
                .debounce(CLICK_GAP, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        mStyle = 4;
                        doSearch(charSequence.toString());
                    }
                });
        RxView.clicks(mBeginRouteButton).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                int i = 0;
                if (!TextUtils.isEmpty(mStartPtEditText.getText())) {
                    i = i + 1;
                }

                if (!TextUtils.isEmpty(mFirstPtEditText.getText())) {
                    i = i + 1;
                }

                if (!TextUtils.isEmpty(mSecondPtEditText.getText())) {
                    i = i + 1;
                }

                if (!TextUtils.isEmpty(mThirdPtEditText.getText())) {
                    i = i + 1;
                }

                if (!TextUtils.isEmpty(mForthPtEditText.getText())) {
                    i = i + 1;
                }

                if (i < 2) {
                    return false;
                }
                return true;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    XMultiLocationMessageEvent.getInstance().setLocations(mFiveLocation);
                    Intent intent = new Intent(XMultiLocationActivity.this, XMapActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(XMultiLocationActivity.this, "need more locations", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RxView.clicks(mBackImagView).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
            }
        });
    }

    private void doSearch(String key) {
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword(key)
                .city(mLocateCity));
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .keyword(key)
                .city(mLocateCity));
    }

    @Override
    public void onDestroyBase() throws XBaseException {
        super.onDestroyBase();
        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
    }

    private void updateSearchResults() {
        ArrayList<String> sugKeys = new ArrayList<String>();
        mSearchResults = new ArrayList<XSearchInfo>();

        if (XUtils.checkEmptyList(mFinalSuggestions)) {
            for (SuggestionResult.SuggestionInfo suggestionInfo : mFinalSuggestions) {
                XSearchInfo searchInfo = new XSearchInfo();
                if (suggestionInfo.pt != null && !sugKeys.contains(suggestionInfo.key)) {
                    searchInfo.setPt(suggestionInfo.pt);
                    searchInfo.setName(suggestionInfo.key);
                    searchInfo.setAddress(suggestionInfo.address);
                    sugKeys.add(suggestionInfo.key);
                    mSearchResults.add(searchInfo);
                }
            }
        }

        if (XUtils.checkEmptyList(mSearchPoiResults)) {
            for (PoiInfo poiInfo : mSearchPoiResults) {
                XSearchInfo searchInfo = new XSearchInfo();
                if (poiInfo.location != null && !sugKeys.contains(poiInfo.name)) {
                    searchInfo.setPt(poiInfo.location);
                    searchInfo.setName(poiInfo.name);
                    searchInfo.setAddress(poiInfo.address);
                    mSearchResults.add(searchInfo);
                }
            }
        }
    }

}
