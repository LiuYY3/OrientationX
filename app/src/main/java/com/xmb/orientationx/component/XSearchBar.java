package com.xmb.orientationx.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xmb.orientationx.R;

/**
 * Created by 徐梦笔 on 2017/10/31.
 */

public class XSearchBar extends FrameLayout {

    private EditText mInputEditText;
    private ImageView mSearchImageView;

    public XSearchBar(Context context) {
        this(context, null, 0);
    }

    public XSearchBar(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public XSearchBar(Context context, AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.component_search_bar, this);
        initViews();
        getAttrs(context, attr);
    }

    private void getAttrs(Context context, AttributeSet attr) {
        TypedArray typedArray = context.obtainStyledAttributes(attr, R.styleable.XSearchBar);
        typedArray.recycle();
    }

    private void initViews() {
        initComponents();
    }

    private void initComponents() {
        mInputEditText = (EditText) this.findViewById(R.id.id_search_txt);
        mSearchImageView = (ImageView) this.findViewById(R.id.id_search_img);
    }

}
