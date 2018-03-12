package com.xmb.orientationx.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xmb.orientationx.R;

import butterknife.ButterKnife;

/**
 * Created by lym on 2018/03/11.
 */

public class XFavoriteViewHolder extends RecyclerView.ViewHolder {

    public CheckBox mPickCheckBox;
    public TextView mAddressTextView;

    public XFavoriteViewHolder(View itemView) {
        super(itemView);
        mPickCheckBox = (CheckBox) itemView.findViewById(R.id.id_pick_btn);
        mAddressTextView = (TextView) itemView.findViewById(R.id.id_favorite_txt);
    }
}
