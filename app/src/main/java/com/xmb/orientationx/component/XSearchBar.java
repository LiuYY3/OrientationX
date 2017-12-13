package com.xmb.orientationx.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.jakewharton.rxbinding.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XSearchAdaptor;
import com.xmb.orientationx.application.XApplication;

import rx.functions.Action1;

/**
 * XSearchBar.
 * subclass of {@link FrameLayout}
 * @author 徐梦笔
 */
public class XSearchBar extends FrameLayout implements OnGetPoiSearchResultListener {

    private EditText mInputEditText;
    private ImageView mSearchImageView;
    private RelativeLayout mSearchLayout;
    private LinearLayout mSearchBaseLayout;
    private RecyclerView mHistoryListView;

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
    public void onGetPoiResult(PoiResult poiResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    private void initViews() {
        mInputEditText = (EditText) this.findViewById(R.id.id_search_txt);
        mSearchImageView = (ImageView) this.findViewById(R.id.id_search_img);
        mSearchLayout = (RelativeLayout) this.findViewById(R.id.id_search_layout);
        mSearchBaseLayout = (LinearLayout) this.findViewById(R.id.id_search_base);
        mHistoryListView = (RecyclerView) this.findViewById(R.id.id_search_history_list);
        mInputEditText.setVisibility(GONE);
        mSearchImageView.setVisibility(GONE);
        initRXBindings();
    }

    private void initRecyclerView() {
        mHistoryListView.setLayoutManager(new LinearLayoutManager(XApplication.getContext(), LinearLayoutManager.VERTICAL, false));
//        mHistoryListView.setAdapter(new XSearchAdaptor(mDataList, mPointLocationList, mInputEditText, mSearchHistroyListView, mBaiduMap));
    }

    private void initRXBindings() {
        if (mSearchImageView.getVisibility() != VISIBLE && mInputEditText.getVisibility() != VISIBLE) {
            RxView.clicks(mSearchLayout).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    mSearchImageView.setVisibility(VISIBLE);
                    mInputEditText.setVisibility(VISIBLE);
                }
            });
        }
    }

}
