package com.xmb.orientationx.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmb.orientationx.R;
import com.xmb.orientationx.component.XSearchBar;
import com.xmb.orientationx.exception.XBaseException;

/**
 * XBaseActivity.
 * @author 徐梦笔
 */
public class XBaseActivity extends AppCompatActivity {

    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {

    }

    public void onDestroyBase() throws XBaseException {

    }

    public void onStartBase() throws XBaseException {

    }

    public void onStopBase() throws XBaseException {

    }

    public void onPauseBase() throws XBaseException {

    }

    public void onResumeBase() throws XBaseException {

    }

    public void onRestartBase() throws XBaseException {

    }

    public void onActivityResultBase(int requestCode, int resultCode, Intent data) throws XBaseException {

    }

    public void showSearchBar(Boolean show) {
        TextView title = (TextView) this.findViewById(R.id.id_middle_txt);
        ImageView leftIcon = (ImageView) this.findViewById(R.id.id_left_img);
        ImageView rightIcon = (ImageView) this.findViewById(R.id.id_right_img);
        XSearchBar searchBar = (XSearchBar) this.findViewById(R.id.id_search_bar);
        if (show) {
            searchBar.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
            leftIcon.setVisibility(View.GONE);
            rightIcon.setVisibility(View.GONE);
        } else {
            searchBar.setVisibility(View.GONE);
            leftIcon.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            rightIcon.setVisibility(View.VISIBLE);
        }
    }

    public void showTitle(Boolean show, String text) {
        TextView title = (TextView) this.findViewById(R.id.id_middle_txt);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/CygnetRound.ttf");
        if (title != null) {
            title.setText(text);
            title.setTypeface(typeface);
            if (show) {
                title.setVisibility(View.VISIBLE);
            } else {
                title.setVisibility(View.GONE);
            }
        }
    }

    public void showLeftIcon(Boolean show, int res) {
        ImageView leftIcon = (ImageView) this.findViewById(R.id.id_left_img);
        if (leftIcon != null) {
            if (res != 0) {
                leftIcon.setImageResource(res);
            }
            if (show) {
                leftIcon.setVisibility(View.VISIBLE);
            } else {
                leftIcon.setVisibility(View.GONE);
            }
        }
    }

    public void showRightIcon(Boolean show, int res) {
        ImageView rightIcon = (ImageView) this.findViewById(R.id.id_right_img);
        if (rightIcon != null) {
            if (res != 0) {
                rightIcon.setImageResource(res);
            }
            if (show) {
                rightIcon.setVisibility(View.VISIBLE);
            } else {
                rightIcon.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
//            StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorTransparent, getTheme()));
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            onCreateBase(savedInstanceState);
        } catch (XBaseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            onStartBase();
        } catch (XBaseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            onStopBase();
        } catch (XBaseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            onDestroyBase();
        } catch (XBaseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            onActivityResultBase(requestCode, resultCode, data);
        } catch (XBaseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            onPauseBase();
        } catch (XBaseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            onResumeBase();
        } catch (XBaseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            onRestartBase();
        } catch (XBaseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
