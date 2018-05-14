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
import com.baidu.mapapi.utils.DistanceUtil;
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
    @BindView(R.id.id_text)
    TextView mTextview;

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

        RxView.clicks(mBeginRouteButton).map(new Function<Object, Integer>() {
            @Override
            public Integer apply(Object o) throws Exception {
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

                if (i < 3) {
                    return 0;
                }

                if (i == 3) {
                    return 1;
                }
                if (i == 4) {
                    return 2;
                }
                return 3;


            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer a) throws Exception {
//                for (int i = 0 ; i <4;i++){
//
//                Log.i("Test", String.valueOf(i) + mFiveLocation[i].getPt().toString());
//                }
                mAllRecyclerView.setVisibility(View.GONE);
                if (a == 0) {
//                    XMultiLocationMessageEvent.getInstance().setLocations(mFiveLocation);
//                    Intent intent = new Intent(XMultiLocationActivity.this, XMapActivity.class);
//                    startActivity(intent);
                    Toast.makeText(XMultiLocationActivity.this, "need more locations", Toast.LENGTH_SHORT).show();
                } else if (a == 1) {

                    double a1 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt());
                    double a2 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt());
                    if(a1<a2){mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName());
                    }else {mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName());}
//
//                    XMultiLocationMessageEvent.getInstance().setLocations(mFiveLocation);
//                    Intent intent = new Intent(XMultiLocationActivity.this, XMapActivity.class);
//                    startActivity(intent);
//                    finish();
                } else if(a==2){

                    double a1 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[3].getPt());
                    double a2 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[2].getPt());
                    double a3 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt());
                    double a4 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[1].getPt());
                    double a5 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt());
                    double a6 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt());
                    double aa []=new double[]{a1,a2,a3,a4,a5,a6};
                    double dis = a1;
                    for(int i = 0;i<6;i++){
                        Log.i("Test", "record: "+String.valueOf(aa[i]));
                        if(dis> aa[i]){
                            dis = aa[i]; Log.i("Test", "change: "+String.valueOf(dis)); }

                    }
//                    Log.i("Test", "record: "+String.valueOf(aa[5]));
                    Log.i("Test", "record: "+String.valueOf(dis));
                    if (dis == a1) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[3].getName());
                    }else if (dis == a2) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[2].getName());
                    }else if (dis == a3) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[3].getName());
                    }else if (dis == a4) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[1].getName());
                    }else if (dis == a5) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName());
                    }else if (dis == a6) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName());
                    }

                }else if (a==3){
                    double a1 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[4].getPt());
                    double a2 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[3].getPt());
                    double a3 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[4].getPt());
                    double a4 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[2].getPt());
                    double a5 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[3].getPt());
                    double a6 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[2].getPt());
                    double a7 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[4].getPt());
                    double a8 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[4].getPt());
                    double a9 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[3].getPt());
                    double a10 = DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[4].getPt());
                    double a11= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt());
                    double a12= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[1].getPt());
                    double a13= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[4].getPt());
                    double a14= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[2].getPt());
                    double a15= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[4].getPt());
                    double a16= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[1].getPt());
                    double a17= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt());
                    double a18= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt());
                    double a19= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[3].getPt());
                    double a20= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[2].getPt());
                    double a21= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[3].getPt());
                    double a22= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[1].getPt());
                    double a23= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[1].getPt())+DistanceUtil.getDistance(mFiveLocation[1].getPt(),mFiveLocation[2].getPt());
                    double a24= DistanceUtil.getDistance(mFiveLocation[0].getPt(),mFiveLocation[4].getPt())+DistanceUtil.getDistance(mFiveLocation[4].getPt(),mFiveLocation[3].getPt())+DistanceUtil.getDistance(mFiveLocation[3].getPt(),mFiveLocation[2].getPt())+DistanceUtil.getDistance(mFiveLocation[2].getPt(),mFiveLocation[1].getPt());
                    double aa[]=new double[]{a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12,a13,a14,a15,a16,a17,a18,a19,a20,a21,a22,a23,a24};
                    double dis = a1 ;
                    for (int i = 0 ; i<24;i++){
                        if(dis> aa[i]){
                            dis = aa[i];  }

                    }
                    if (dis == a1) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[4].getName());
                }else if (dis == a2) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[3].getName());
                    }else if (dis == a3) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[4].getName());
                    }else if(dis == a4) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[2].getName());
                    }else if(dis == a5) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[3].getName());
                    }else if(dis == a6) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[2].getName());
                    }else if(dis == a7) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[4].getName());
                    }else if(dis == a8) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[3].getName());
                    }else if(dis == a9) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[4].getName());
                    }else if(dis == a10) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[1].getName());
                    }else if(dis == a11) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[3].getName());
                    }else if(dis == a12) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[1].getName());
                    }else if(dis == a13) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[4].getName());
                    }else if(dis == a14) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[2].getName());
                    }else if(dis == a15) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[4].getName());
                    }else if(dis == a16) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[1].getName());
                    }else if(dis == a17) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName());
                    }else if(dis == a18) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName());
                    }else if(dis == a19) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[3].getName());
                    }else if(dis == a20) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[2].getName());
                    }else if(dis == a21) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[3].getName());
                    }else if(dis == a22) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[1].getName());
                    }else if(dis == a23) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[1].getName()+"、"+mFiveLocation[2].getName());
                    }else if(dis == a24) {
                        mTextview.setText(mFiveLocation[0].getName()+"、"+mFiveLocation[4].getName()+"、"+mFiveLocation[3].getName()+"、"+mFiveLocation[2].getName()+"、"+mFiveLocation[1].getName());
                    }
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
