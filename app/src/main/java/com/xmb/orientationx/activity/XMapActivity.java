package com.xmb.orientationx.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.fragment.XMapFragment;
import com.xmb.orientationx.fragment.XSearchFragment;
import com.xmb.orientationx.utils.StatusBarUtil;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by lym on 2018/02/08.
 */

public class XMapActivity extends XBaseActivity {

    @BindView(R.id.id_main_search_card)
    CardView mSearchToolCard;
    @BindView(R.id.id_main_search_layout)
    RelativeLayout mInputLayout;
    @BindView(R.id.id_main_search_input)
    EditText mKeyInputEditText;

    private int mContainer = R.id.id_main_container;
    private XMapFragment mMapFragment;
    private XSearchFragment mSearchFragment;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 100);
        initView();
        initMap();
        initRXBinding();
    }

    private void initView() {
        mInputLayout.setVisibility(View.GONE);
    }

    private void initMap() {
        mMapFragment = new XMapFragment();
        getSupportFragmentManager().beginTransaction()
                .add(mContainer, mMapFragment, "Map")
                .commit();
    }

    private void doSearch() {
        mSearchFragment = new XSearchFragment();
        getSupportFragmentManager().beginTransaction()
                .add(mContainer, mSearchFragment, "Search")
                .commit();
    }

    private void initRXBinding() {
        RxView.clicks(mSearchToolCard).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                if (mInputLayout.getVisibility() == View.VISIBLE) {
                    return false;
                }
                return true;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if (b) {
                    mInputLayout.setVisibility(View.VISIBLE);
                    doSearch();
                }
            }
        });
    }

}
