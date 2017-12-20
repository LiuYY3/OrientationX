package com.xmb.orientationx.adaptor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.jakewharton.rxbinding.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.model.SearchInfo;
import com.xmb.orientationx.utils.XUtils;
import com.xmb.orientationx.viewholder.XSearchViewHolder;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * XSearchAdaptor.
 * subclass of {@link RecyclerView.Adapter<XSearchViewHolder>}
 * @author 徐梦笔
 */
public class XSearchAdaptor extends RecyclerView.Adapter<XSearchViewHolder>{

    private ArrayList<SuggestionInfo> mSuggestions;
    private ArrayList<PoiInfo> mPoiResults;
    private SearchInfo mChosenResult;

    public XSearchAdaptor (ArrayList<SuggestionInfo> suggestionInfos, ArrayList<PoiInfo> poiInfos) {
        this.mSuggestions = suggestionInfos;
        this.mPoiResults = poiInfos;
    }

    public void updateSuggestions(ArrayList<SuggestionInfo> suggestionInfos) {
        this.mSuggestions = suggestionInfos;
        notifyDataSetChanged();
    }

    public void updatePoiResults(ArrayList<PoiInfo> poiInfos) {
        this.mPoiResults = poiInfos;
        notifyDataSetChanged();
    }

    @Override
    public XSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final XSearchViewHolder holder, final int position) {
        holder.mUnderLineView.setVisibility(View.VISIBLE);
        ArrayList<String> sugKeys = new ArrayList<String>();
        if (position == getItemCount() - 1) {
            holder.mUnderLineView.setVisibility(View.GONE);
        }
        if (XUtils.checkEmptyList(mSuggestions)) {
            if (position < mSuggestions.size()) {
                if (mSuggestions.get(position).pt != null) {
                    sugKeys.add(mSuggestions.get(position).key);
                    holder.mListTextView.setText(mSuggestions.get(position).key);
                    mChosenResult = new SearchInfo();
                    mChosenResult.setSuggest(mSuggestions.get(position));
                } else {
                    holder.mBodyLayout.setVisibility(View.GONE);
                }
            } else {
                if (!sugKeys.contains(mPoiResults.get(position - mSuggestions.size()).name)) {
                    holder.mListTextView.setText(mPoiResults.get(position - mSuggestions.size()).name);
                    mChosenResult = new SearchInfo();
                    mChosenResult.setPoi(mPoiResults.get(position - mSuggestions.size()));
                }
            }
        } else {
            if (XUtils.checkEmptyList(mPoiResults)) {
                holder.mListTextView.setText(mPoiResults.get(position).name);
                mChosenResult = new SearchInfo();
                mChosenResult.setPoi(mPoiResults.get(position));
            }
        }

        RxView.clicks(holder.mBodyLayout).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mSuggestions == null && mPoiResults == null) {
            return 0;
        } else {
            if (mSuggestions == null) {
                return mPoiResults.size();
            } else if (mPoiResults == null) {
                return mSuggestions.size();
            } else {
                return mSuggestions.size() + mPoiResults.size();
            }
        }
    }

}
