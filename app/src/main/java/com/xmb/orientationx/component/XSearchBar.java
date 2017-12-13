package com.xmb.orientationx.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.jakewharton.rxbinding.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XSearchAdaptor;
import com.xmb.orientationx.application.XApplication;
import com.xmb.orientationx.constant.XConstants;
import com.xmb.orientationx.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * XSearchBar.
 * subclass of {@link FrameLayout}
 * @author 徐梦笔
 */
public class XSearchBar extends FrameLayout implements OnGetPoiSearchResultListener, TextWatcher, OnGetSuggestionResultListener {

    public EditText mInputEditText;
    private ImageView mSearchImageView;
    private RelativeLayout mSearchLayout;
    private LinearLayout mSearchBaseLayout;
    private RecyclerView mHistoryListView;
    private ArrayList<PoiInfo> mPoiInfoResultList;
    public SuggestionSearch mSuggestionSearch;
    public PoiSearch mPoiSearch;
    private String mCity;

    public XSearchBar(Context context) {
        this(context, null, 0);
    }

    public XSearchBar(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public XSearchBar(Context context, AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.component_search_bar, this);
        initViews();
        getAttrs(context, attr);
    }

    private void getAttrs(Context context, AttributeSet attr) {
        TypedArray typedArray = context.obtainStyledAttributes(attr, R.styleable.XSearchBar);
        typedArray.recycle();
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        List<SuggestionInfo> suggestions = suggestionResult.getAllSuggestions();
        if (Utils.checkEmptyList(suggestions)) {
            for (SuggestionInfo suggestion : suggestions) {
                Log.i(XConstants.TAG_BAR, "onGetSuggestionResult: " + suggestion.key + " : " + suggestion.city);
            }
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {

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
        String input = mInputEditText.getText().toString();
        Log.i(XConstants.TAG_BAR, "afterTextChanged: " + input + " : " + mCity);
        mCity = Utils.nullCity(mCity);
        startSearchSuggestions(input);
    }

    public void setCity(String city) {
        mCity = city;
    }

    private void initViews() {
        mInputEditText = (EditText) this.findViewById(R.id.id_search_txt);
        mSearchImageView = (ImageView) this.findViewById(R.id.id_search_img);
        mSearchLayout = (RelativeLayout) this.findViewById(R.id.id_search_layout);
        mSearchBaseLayout = (LinearLayout) this.findViewById(R.id.id_search_base);
        mHistoryListView = (RecyclerView) this.findViewById(R.id.id_search_history_list);
        mSearchImageView.setVisibility(GONE);
        mHistoryListView.setVisibility(GONE);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mInputEditText.addTextChangedListener(this);
        initRecyclerView();
        initRXBindings();
    }

    private void initRecyclerView() {
        mHistoryListView.setLayoutManager(new LinearLayoutManager(XApplication.getContext(), LinearLayoutManager.VERTICAL, false));
        mHistoryListView.setAdapter(new XSearchAdaptor(mPoiInfoResultList));
    }

    private void initRXBindings() {
        RxView.clicks(mInputEditText).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mSearchImageView.setVisibility(VISIBLE);
            }
        });
    }

    private void startSearchSuggestions(String input) {
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword(input)
                .city(mCity));
    }

}
