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
import com.xmb.orientationx.utils.XAppDataUtils;
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
 * Created by Mr Xu on 2018/4/10.
 */

public class XCommonActivity extends  XBaseActivity implements XCityListener,OnGetSuggestionResultListener, OnGetPoiSearchResultListener,XMultiSearchAdaptor.MultiSelectedListener {
    @BindView(R.id.id_first_txt)
    EditText mFirstCoEditText;
    private XMultiSearchAdaptor mAdapter;
    private XSearchInfo[] mFiveLocation = new XSearchInfo[5];
    private int mStyle;
    private RecyclerView.LayoutManager mLayoutManager;
    private SuggestionSearch mSuggestionSearch;
    private PoiSearch mPoiSearch;
    private ArrayList<PoiInfo> mSearchPoiResults;
    private ArrayList<SuggestionResult.SuggestionInfo> mSearchSuggestions, mSearchSubSuggestions, mFinalSuggestions;
    private ArrayList<XSearchInfo> mSearchResults;
    private String mLocateCity = "盐城";
    private static final long CLICK_GAP = 500;

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemSelected(int style, int position) {
        switch (style) {
            case 0:
                mFirstCoEditText.setText(mSearchResults.get(position).getName());
                XAppDataUtils.getInstance().setsPt(mSearchResults.get(position).getPt());
                XAppDataUtils.getInstance().setCommon1(mSearchResults.get(position).getName());
                mSearchResults = new ArrayList<XSearchInfo>();
                mAdapter.updateResults(0, mSearchResults);
                break;
            case 1:
                mSecondCoEditText.setText(mSearchResults.get(position).getName());
                XAppDataUtils.getInstance().setCommon2(mSearchResults.get(position).getName());
                mSearchResults = new ArrayList<XSearchInfo>();
                mAdapter.updateResults(1, mSearchResults);
                break;
            default:
                break;
        }


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
    public void onCityChanged(String cityname) {

    }

    @BindView(R.id.id_second_txt)
    EditText mSecondCoEditText;
    @BindView(R.id.id_common_search_result_recycle)
    RecyclerView mAllRecyclerView;
    @BindView(R.id.id_activity_left_icon)
    ImageView mBackImagView;
    @BindView(R.id.id_activity_right)
    TextView mEditTextView;
    @BindString(R.string.app_edit)
    String edit;
    @BindString(R.string.app_done)
    String done;
    @BindString(R.string.app_common)
    String common;



    private void doSearch(String key) {
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword(key)
                .city(mLocateCity));
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .keyword(key)
                .city(mLocateCity));
    }
    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_commom);
        ButterKnife.bind(this);
        XLocationClient.getInstance().setCityListener(this);
        initRxBindings();
        initViews();
    }


    private void initViews() {
        this.showRightTitle(true, edit);
        mSearchResults = new ArrayList<XSearchInfo>();
        mFirstCoEditText.setEnabled(false);
        mFirstCoEditText.setText(XAppDataUtils.getInstance().getCommon1());
        mSecondCoEditText.setEnabled(false);
        mSecondCoEditText.setText(XAppDataUtils.getInstance().getCommon2());
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new XMultiSearchAdaptor(this, 0, mSearchResults);
        mAdapter.setListener(this);
        mAllRecyclerView.setLayoutManager(mLayoutManager);
        mAllRecyclerView.setAdapter(mAdapter);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        this.showTitle(true, common);
        this.showLeftIcon(true, R.mipmap.ic_action_arrow_left);
    }
    private void initRxBindings() {
        RxView.clicks(mBackImagView).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
            }
        });

        RxView.clicks(mEditTextView).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                return mEditTextView.getText() == edit;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if (b) {

                    mEditTextView.setText(done);
                    mFirstCoEditText.setEnabled(true);
                    mSecondCoEditText.setEnabled(true);
                    RxTextView.textChanges(mSecondCoEditText)
                            .debounce(CLICK_GAP, TimeUnit.MILLISECONDS)
                            .subscribe(new Consumer<CharSequence>() {
                                @Override
                                public void accept(CharSequence charSequence) throws Exception {
                                    mStyle = 1;
                                    doSearch(charSequence.toString());
                                }
                            });
                    RxTextView.textChanges(mFirstCoEditText)
                            .debounce(CLICK_GAP, TimeUnit.MILLISECONDS)
                            .subscribe(new Consumer<CharSequence>() {
                                @Override
                                public void accept(CharSequence charSequence) throws Exception {
                                    mStyle = 0;
                                    doSearch(charSequence.toString());
                                }
                            });

                } else {
                    mFirstCoEditText.setEnabled(false);
                    mSecondCoEditText.setEnabled(false);
                    mSearchResults = new ArrayList<XSearchInfo>();
                    mAdapter.updateResults(0, mSearchResults);
                    mEditTextView.setText(edit);

                }
            }
        });


    }

}
