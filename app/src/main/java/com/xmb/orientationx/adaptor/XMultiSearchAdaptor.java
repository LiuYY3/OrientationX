package com.xmb.orientationx.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.utils.XAppDataUtils;
import com.xmb.orientationx.viewholder.XSearchViewHolder;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by lym on 2018/03/28.
 */
public class XMultiSearchAdaptor extends RecyclerView.Adapter<XSearchViewHolder> {

    private ArrayList<XSearchInfo> mSearchResults;
    private MultiSelectedListener mMultiSelectedListener;
    private Context mContext;
    private int mStyle;

    public XMultiSearchAdaptor (Context context, int style, ArrayList<XSearchInfo> searchInfos) {
        mContext = context;
        mSearchResults = searchInfos;
        mStyle = style;
    }

    public void updateResults(int style, ArrayList<XSearchInfo> searchInfos) {
        mSearchResults = searchInfos;
        mStyle = style;
        notifyDataSetChanged();
    }

    @Override
    public XSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final XSearchViewHolder holder, final int position) {

        holder.mSaveImageView.setVisibility(View.GONE);

        holder.mListTextView.setText(mSearchResults.get(position).getName());

        holder.bind(mStyle, position, mMultiSelectedListener);
    }

    @Override
    public int getItemCount() {
        return mSearchResults == null ? 0: mSearchResults.size();
    }

    public void setListener(MultiSelectedListener MultiSelectedListener) {
        mMultiSelectedListener = MultiSelectedListener;
    }

    public interface MultiSelectedListener {
        void onItemSelected(int style, int position);
    }

}
