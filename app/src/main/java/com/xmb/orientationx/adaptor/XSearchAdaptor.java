package com.xmb.orientationx.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.constant.XTags;
import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.utils.XAppDataUtils;
import com.xmb.orientationx.viewholder.XSearchViewHolder;

import java.util.ArrayList;
import java.util.Observable;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * XSearchAdaptor.
 * subclass of {@link RecyclerView.Adapter<XSearchViewHolder>}
 * @author 徐梦笔
 */
public class XSearchAdaptor extends RecyclerView.Adapter<XSearchViewHolder> {

    private ArrayList<XSearchInfo> mSearchResults;
    private ItemSelectedListener mItemSelectedListener;
    private Context mContext;

    public XSearchAdaptor (Context context, ArrayList<XSearchInfo> searchInfos) {
        mContext = context;
        mSearchResults = searchInfos;
    }

    public void updateResults(ArrayList<XSearchInfo> searchInfos) {
        mSearchResults = searchInfos;
        notifyDataSetChanged();
    }

    @Override
    public XSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new XSearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(final XSearchViewHolder holder, final int position) {

        if (mSearchResults.get(position).isSave()) {
            holder.mSaveImageView.setImageResource(R.mipmap.ic_saved);
            holder.setSaved(true);
        } else {
            holder.mSaveImageView.setImageResource(R.mipmap.ic_notsaved);
            holder.setSaved(false);
        }

        RxView.clicks(holder.mSaveImageView).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                return holder.mSave;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean o) throws Exception {
                XSearchInfo temp = mSearchResults.get(position);
                if (o) {
                    holder.mSaveImageView.setImageResource(R.mipmap.ic_notsaved);
                    holder.setSaved(false);
                    XAppDataUtils.getInstance().removeFavorite(temp);
                } else {
                    holder.mSaveImageView.setImageResource(R.mipmap.ic_saved);
                    holder.setSaved(true);
                    temp.setSave(true);
                    XAppDataUtils.getInstance().setFavorite(temp);
                }
            }
        });

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
