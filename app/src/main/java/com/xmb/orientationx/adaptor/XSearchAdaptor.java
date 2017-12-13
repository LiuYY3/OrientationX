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
import com.baidu.mapapi.search.core.PoiInfo;
import com.jakewharton.rxbinding.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.viewholder.XSearchViewHolder;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * XSearchAdaptor.
 * subclass of {@link RecyclerView.Adapter<XSearchViewHolder>}
 * @author 徐梦笔
 */
public class XSearchAdaptor extends RecyclerView.Adapter<XSearchViewHolder>{

    private ArrayList<PoiInfo> mDataList;

    public XSearchAdaptor (ArrayList<PoiInfo> data) {
        this.mDataList = data;
    }

    public void updateData(ArrayList<PoiInfo> data) {
        this.mDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public XSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final XSearchViewHolder holder, final int position) {
        holder.mListTextView.setText(mDataList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

}
