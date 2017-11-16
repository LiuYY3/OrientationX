package com.xmb.orientationx.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xmb.orientationx.R;

/**
 * Created by 徐梦笔 on 2017/11/11.
 */

public class XSearchViewHolder extends RecyclerView.ViewHolder {

    public TextView mListTextView;
//    public EditText mSearchBarTextView;

    public XSearchViewHolder(View itemView) {
        super(itemView);
        mListTextView = (TextView) itemView.findViewById(R.id.id_search_result_txt);
//        mSearchBarTextView = (EditText) itemView.findViewById(R.id.id_search_txt);
    }

}
