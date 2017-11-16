package com.xmb.orientationx.adaptor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.jakewharton.rxbinding.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.viewholder.XSearchViewHolder;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * Created by 徐梦笔 on 2017/11/11.
 */

public class XSearchAdaptor extends RecyclerView.Adapter<XSearchViewHolder>{

    private ArrayList<String> mDataList;
    private ArrayList<LatLng> mPointLocationList;
    private EditText mSearchBarTextView;
    private RecyclerView mSearchHistroyListView;
    private BaiduMap mBaiduMap;

    public XSearchAdaptor (ArrayList<String> data, ArrayList<LatLng> pointList, EditText editText, RecyclerView recyclerView, BaiduMap baiduMap) {
        this.mDataList = data;
        this.mSearchBarTextView = editText;
        this.mSearchHistroyListView = recyclerView;
        this.mPointLocationList = pointList;
        this.mBaiduMap = baiduMap;
    }

    public void updateData(ArrayList<String> data, ArrayList<LatLng> pointList) {
        this.mDataList = data;
        this.mPointLocationList = pointList;
        notifyDataSetChanged();
    }

    @Override
    public XSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final XSearchViewHolder holder, final int position) {
        holder.mListTextView.setText(mDataList.get(position));
        RxView.clicks(holder.mListTextView).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mSearchBarTextView.setText(holder.mListTextView.getText());
                mSearchHistroyListView.setVisibility(View.GONE);
                mBaiduMap.clear();
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.marker_self);

                OverlayOptions option = new MarkerOptions()
                        .position(mPointLocationList.get(holder.getAdapterPosition()))
                        .icon(bitmap);

                mBaiduMap.addOverlay(option);
                updateData(new ArrayList<String>(), new ArrayList<LatLng>());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

}
