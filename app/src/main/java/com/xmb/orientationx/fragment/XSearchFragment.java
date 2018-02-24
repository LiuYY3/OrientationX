package com.xmb.orientationx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XSearchAdaptor;
import com.xmb.orientationx.broadcast.XLocationClient;
import com.xmb.orientationx.constant.XTags;
import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.interfaces.XCityListener;
import com.xmb.orientationx.message.XSearchMessageEvent;
import com.xmb.orientationx.message.XKeyMessageEvent;
import com.xmb.orientationx.utils.XSearchUtils;
import com.xmb.orientationx.utils.XUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lym on 2018/02/08.
 */

public class XSearchFragment extends Fragment implements XCityListener,
        OnGetSuggestionResultListener,
        XSearchAdaptor.ItemSelectedListener,
        OnGetPoiSearchResultListener {

    @BindView(R.id.id_main_history_card)
    CardView mHistoryListCard;
    @BindView(R.id.id_search_result_recycler)
    RecyclerView mHistoryRecyclerView;

    private String mLocateCity = "盐城";

    private Unbinder mBinder;
    private XSearchAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SuggestionSearch mSuggestionSearch;
    private PoiSearch mPoiSearch;
    private ArrayList<SuggestionResult.SuggestionInfo> mSearchSuggestions, mSearchSubSuggestions, mFinalSuggestions;
    private ArrayList<PoiInfo> mSearchPoiResults;
    private ArrayList<XSearchInfo> mSearchResults;

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
            mAdapter.updateResults(mSearchResults);
        } else {
            mFinalSuggestions = new ArrayList<SuggestionResult.SuggestionInfo>();
            updateSearchResults();
            mAdapter.updateResults(mSearchResults);
        }
    }

    @Override
    public void onCityChanged(String cityName) {
        mLocateCity = cityName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mBinder = ButterKnife.bind(this, view);
        XLocationClient.getInstance().setCityListener(this);
        EventBus.getDefault().register(this);
        initSearch();
        return view;
    }

    private void initSearch() {
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mBinder.unbind();
        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
    }

    @Override
    public void onItemSelected(int position) {
        Log.i(XTags.SEARCH, "onItemSelected: clicked");
        XSearchMessageEvent.getInstance().setInfo(mSearchResults.get(position));
        XSearchMessageEvent.getInstance().setInput(mSearchResults.get(position).getName());
        XSearchMessageEvent.getInstance().setSearch(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new XSearchAdaptor(mSearchResults);
        mAdapter.setListener(this);
        mHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mHistoryRecyclerView.setAdapter(mAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInputMessage(XKeyMessageEvent event) {
        String key = event.getKey().toString().trim();

        Log.i(XTags.SEARCH, "onMessage: " + key);

        if (TextUtils.isEmpty(key)) {
            mHistoryListCard.setVisibility(View.INVISIBLE);
        } else {
            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(key)
                    .city(mLocateCity));
            mPoiSearch.searchInCity((new PoiCitySearchOption())
                    .keyword(key)
                    .city(mLocateCity));
            mHistoryListCard.setVisibility(View.VISIBLE);
        }
    }

    private void updateSearchResults() {
        ArrayList<String> sugKeys = new ArrayList<String>();
        mSearchResults = new ArrayList<XSearchInfo>();

        if (XUtils.checkEmptyList(mFinalSuggestions)) {
            for (SuggestionResult.SuggestionInfo suggestionInfo : mFinalSuggestions) {
                XSearchInfo searchInfo = new XSearchInfo();
                if (suggestionInfo.pt != null) {
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
