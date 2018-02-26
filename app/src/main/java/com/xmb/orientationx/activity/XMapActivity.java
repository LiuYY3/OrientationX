package com.xmb.orientationx.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.constant.XTags;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.fragment.XMapFragment;
import com.xmb.orientationx.fragment.XSearchFragment;
import com.xmb.orientationx.interfaces.XCloseStatusListener;
import com.xmb.orientationx.message.XClickMessageEvent;
import com.xmb.orientationx.message.XKeyMessageEvent;
import com.xmb.orientationx.message.XSearchMessageEvent;
import com.xmb.orientationx.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by lym on 2018/02/08.
 */

public class XMapActivity extends XBaseActivity implements XCloseStatusListener {

    @BindView(R.id.id_main_search_card)
    CardView mSearchToolCard;
    @BindView(R.id.id_main_search_layout)
    RelativeLayout mInputLayout;
    @BindView(R.id.id_main_search_input)
    EditText mKeyInputEditText;
    @BindView(R.id.id_main_search_show)
    TextView mKeyShowTextView;
    @BindView(R.id.id_guide_start_btn)
    Button mStartGuideButton;

    private int mContainer = R.id.id_main_container;

    private FragmentManager mFragmentManager;
    private XMapFragment mMapFragment;
    private XSearchFragment mSearchFragment;

    private static final long CLICK_GAP = 500;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        XSearchMessageEvent.getInstance().setStatusListener(this);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 100);
        initView();
        initMap();
        initRXBinding();
    }

    @Override
    public void onDestroyBase() throws XBaseException {
        super.onDestroyBase();
    }

    @Override
    public void onCloseStatusChanged(boolean close) {
        if (close) {
            updateSearchBar();
        }
    }

    private void updateSearchBar() {
        mFragmentManager.beginTransaction().remove(mSearchFragment).commit();
        mInputLayout.setVisibility(View.GONE);
        mKeyShowTextView.setText(XSearchMessageEvent.getInstance().getInput());
        mKeyShowTextView.setVisibility(View.VISIBLE);
    }

    private void initView() {
        mInputLayout.setVisibility(View.GONE);
        mFragmentManager = getSupportFragmentManager();
    }

    private void initMap() {
        mMapFragment = new XMapFragment();
        mFragmentManager.beginTransaction()
                .add(mContainer, mMapFragment, XTags.MAP)
                .commit();
    }

    private void doSearch() {
        XSearchMessageEvent.getInstance().setSearch(false);
        mSearchFragment = new XSearchFragment();
        mFragmentManager.beginTransaction()
                .add(mContainer, mSearchFragment, XTags.SEARCH)
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
                    mKeyShowTextView.setVisibility(View.GONE);
                    doSearch();
                    mKeyInputEditText.setText(XSearchMessageEvent.getInstance().getInput());
                }
            }
        });

        RxView.clicks(mStartGuideButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                XClickMessageEvent.getInstance().setClick();
            }
        });

        RxTextView.textChanges(mKeyInputEditText)
                .debounce(CLICK_GAP, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        Log.i(XTags.MAP, "accept: " + charSequence);
                        XSearchMessageEvent.getInstance().setInput(charSequence);
                        XKeyMessageEvent.getInstance().setKey(charSequence);
                        EventBus.getDefault().post(XKeyMessageEvent.getInstance());
                    }
                });

    }

}
