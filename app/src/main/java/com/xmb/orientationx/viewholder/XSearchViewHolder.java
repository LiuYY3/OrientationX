package com.xmb.orientationx.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XMultiSearchAdaptor;
import com.xmb.orientationx.adaptor.XSearchAdaptor.ItemSelectedListener;

import butterknife.ButterKnife;

/**
 * XSearchViewHolder.
 * subclass of {@link RecyclerView.ViewHolder}
 * @author 徐梦笔
 */
public class XSearchViewHolder extends RecyclerView.ViewHolder {

    public TextView mListTextView;
    public RelativeLayout mBodyLayout;
    public ImageView mSaveImageView;
    public boolean mSave;

    public XSearchViewHolder(View itemView) {
        super(itemView);
        mListTextView = (TextView) itemView.findViewById(R.id.id_search_result_txt);
        mBodyLayout = (RelativeLayout) itemView.findViewById(R.id.id_search_result_body);
        mSaveImageView = (ImageView) itemView.findViewById(R.id.id_search_save_img);
    }

    public void setSaved(boolean save) {
        mSave = save;
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

    public void bind(final int style, final int position, final XMultiSearchAdaptor.MultiSelectedListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemSelected(style, position);
                }
            }
        });
    }

}
