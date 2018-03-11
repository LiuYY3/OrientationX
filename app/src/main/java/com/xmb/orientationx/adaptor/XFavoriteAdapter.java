package com.xmb.orientationx.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.utils.XAppDataUtils;
import com.xmb.orientationx.viewholder.XFavoriteViewHolder;
import com.xmb.orientationx.viewholder.XSearchViewHolder;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by lym on 2018/03/11.
 */

public class XFavoriteAdapter extends RecyclerView.Adapter<XFavoriteViewHolder> {
    
    private Context mContext;
    private ArrayList<XSearchInfo> mFavoriteList;
    private boolean isPick = false;

    public XFavoriteAdapter (Context context, ArrayList<XSearchInfo> searchInfos) {
        mContext = context;
        mFavoriteList = searchInfos;
        Log.i("Check", "XFavoriteAdapter: " + mFavoriteList.size());
    }

    public void updateResults(ArrayList<XSearchInfo> searchInfos) {
        mFavoriteList = searchInfos;
        notifyDataSetChanged();
    }

    @Override
    public XFavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XFavoriteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(final XFavoriteViewHolder holder, final int position) {
        holder.mAddressTextView.setText(mFavoriteList.get(position).getName());
//        holder.mPickCheckBox.setChecked(false);

        if (isPick) {
            holder.mPickCheckBox.setVisibility(View.VISIBLE);
            holder.mPickCheckBox.setChecked(false);
        } else {
            if (holder.mPickCheckBox.isChecked()) {
                XAppDataUtils.getInstance().setFavorite(mFavoriteList.get(position));
            }
            holder.mPickCheckBox.setChecked(false);
            holder.mPickCheckBox.setVisibility(View.GONE);
        }

        RxView.clicks(holder.mPickCheckBox).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                return holder.mPickCheckBox.isChecked();
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    XAppDataUtils.getInstance().removeFavorite(mFavoriteList.get(position));
                } else {
                    XAppDataUtils.getInstance().setFavorite(mFavoriteList.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFavoriteList == null ? 0: mFavoriteList.size();
    }

    public void setPicker(boolean is) {
        isPick = is;
        notifyDataSetChanged();
    }

}
