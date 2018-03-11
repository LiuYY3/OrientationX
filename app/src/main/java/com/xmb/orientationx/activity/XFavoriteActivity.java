package com.xmb.orientationx.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.adaptor.XFavoriteAdapter;
import com.xmb.orientationx.adaptor.XSearchAdaptor;
import com.xmb.orientationx.data.XSearchInfo;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.utils.StatusBarUtil;
import com.xmb.orientationx.utils.XAppDataUtils;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by lym on 2018/03/11.
 */

public class XFavoriteActivity extends XBaseActivity {

    private ArrayList<XSearchInfo> mFavoriteList;
    private RecyclerView.LayoutManager mLayoutManager;
    private XFavoriteAdapter mAdapter;

    @BindView(R.id.id_activity_left_icon)
    ImageView mBackImagView;
    @BindView(R.id.id_activity_right)
    TextView mEditTextView;
    @BindView(R.id.id_favorite_recycler)
    RecyclerView mFavoriteRecyclerView;
    @BindView(R.id.id_favorite_del_btn)
    Button mFavoriteDelButton;

    @BindString(R.string.app_edit)
    String edit;
    @BindString(R.string.app_done)
    String done;
    @BindString(R.string.app_favorite)
    String favorite;
    @BindColor(R.color.colorRed)
    int red;
    @BindColor(R.color.colorGray)
    int gray;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
        ButterKnife.bind(this);
        initData();
        initViews();
        initRxBindings();
    }

    private void initViews() {
        this.showTitle(true, favorite);
        this.showRightTitle(true, edit);
        this.showLeftIcon(true, R.mipmap.ic_action_arrow_left);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new XFavoriteAdapter(this, mFavoriteList);
        mFavoriteRecyclerView.setLayoutManager(mLayoutManager);
        mFavoriteRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mFavoriteList = XAppDataUtils.getInstance().getFavorite();
    }

    private void initRxBindings() {
        RxView.clicks(mEditTextView).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                return mEditTextView.getText() == edit;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if (b) {
                    mAdapter.setPicker(true);
                    mEditTextView.setText(done);
                    mFavoriteDelButton.setBackgroundColor(red);
                } else {
                    mAdapter.setPicker(false);
                    mEditTextView.setText(edit);
                    mFavoriteDelButton.setBackgroundColor(gray);
                }
            }
        });

        RxView.clicks(mFavoriteDelButton).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                return mEditTextView.getText() == edit;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if (!b) {
                    mAdapter.updateResults(XAppDataUtils.getInstance().getFavorite());
                }
            }
        });

        RxView.clicks(mBackImagView).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                mAdapter.setPicker(false);
                finish();
            }
        });
    }

}
