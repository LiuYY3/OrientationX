package com.xmb.orientationx.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.xmb.orientationx.R;

/**
 * XSearchBar.
 * subclass of {@link FrameLayout}
 * @author 徐梦笔
 */
public class XSearchBar extends FrameLayout {

    public XSearchBar(final Context context) {
        this(context, null, 0);
    }

    public XSearchBar(final Context context, final AttributeSet attr) {
        this(context, attr, 0);
    }

    public XSearchBar(final Context context, final AttributeSet attr, final int defStyleAttr) {
        super(context, attr, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.component_search_bar, this);
        getAttrs(context, attr);
    }

    private void getAttrs(Context context, AttributeSet attr) {
        TypedArray typedArray = context.obtainStyledAttributes(attr, R.styleable.XSearchBar);
        typedArray.recycle();
    }

}
