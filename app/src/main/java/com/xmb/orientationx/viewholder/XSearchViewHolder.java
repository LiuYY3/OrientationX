package com.xmb.orientationx.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XSearchAdaptor.ItemSelectedListener;

import butterknife.ButterKnife;

/**
 * XSearchViewHolder.
 * subclass of {@link RecyclerView.ViewHolder}
 * @author 徐梦笔
 */
public class XSearchViewHolder extends RecyclerView.ViewHolder {

    public TextView mListTextView;
    public View mUnderLineView;
    public RelativeLayout mBodyLayout;

    public XSearchViewHolder(View itemView) {
        super(itemView);
        mListTextView = (TextView) itemView.findViewById(R.id.id_search_result_txt);
        mUnderLineView = (View) itemView.findViewById(R.id.id_under_line_view);
        mBodyLayout = (RelativeLayout) itemView.findViewById(R.id.id_search_result_body);
    }

    public void bind(final int position, final ItemSelectedListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemSelected(position);
                }
            }
        });
    }

}
