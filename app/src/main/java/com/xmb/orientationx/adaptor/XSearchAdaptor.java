package com.xmb.orientationx.adaptor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmb.orientationx.R;
import com.xmb.orientationx.model.SearchInfo;
import com.xmb.orientationx.viewholder.XSearchViewHolder;

import java.util.ArrayList;

/**
 * XSearchAdaptor.
 * subclass of {@link RecyclerView.Adapter<XSearchViewHolder>}
 * @author 徐梦笔
 */
public class XSearchAdaptor extends RecyclerView.Adapter<XSearchViewHolder>{

    private ArrayList<SearchInfo> mSearchResults;
    private ItemSelectedListener mItemSelectedListener;

    public XSearchAdaptor (ArrayList<SearchInfo> searchInfos) {
        mSearchResults = searchInfos;
    }

    public void updateResults(ArrayList<SearchInfo> searchInfos) {
        mSearchResults = searchInfos;
        notifyDataSetChanged();
    }

    @Override
    public XSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final XSearchViewHolder holder, final int position) {
        if (position == getItemCount() - 1) {
            holder.mUnderLineView.setVisibility(View.GONE);
        }

        holder.mListTextView.setText(mSearchResults.get(position).getName());

        holder.bind(position, mItemSelectedListener);
    }

    @Override
    public int getItemCount() {
        return mSearchResults == null ? 0: mSearchResults.size();
    }

    public void setListener(ItemSelectedListener itemSelectedListener) {
        mItemSelectedListener = itemSelectedListener;
    }

    public interface ItemSelectedListener {
        void onItemSelected(int position);
    }

}
