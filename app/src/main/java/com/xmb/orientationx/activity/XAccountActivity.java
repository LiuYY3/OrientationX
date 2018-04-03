package com.xmb.orientationx.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.jakewharton.rxbinding2.view.RxView;
import com.xmb.orientationx.R;
import com.xmb.orientationx.exception.XBaseException;
import com.xmb.orientationx.interfaces.XSwitchListener;
import com.xmb.orientationx.message.XSwitchMessageEvent;
import com.xmb.orientationx.utils.StatusBarUtil;
import com.xmb.orientationx.utils.XAppDataUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by 徐梦笔 on 2018/03/07.
 */

public class XAccountActivity extends XBaseActivity {

    @BindView(R.id.id_activity_name)
    TextView mNameTextView;
    @BindView(R.id.id_back_to_map_btn)
    Button mBackToMapButton;
    @BindView(R.id.id_to_setting_btn)
    Button mToSetButton;
    @BindView(R.id.id_to_profile_favorite_btn)
    Button mToFavoriteButton;
    @BindView(R.id.id_to_profile_navigation_btn)
    Button mToPlanGuideButton;
    @BindView(R.id.id_cal_distance_btn)
    Button mToDistanceButton;
    @BindView(R.id.id_to_profile_style_btn)
    Button mToSwitchMapButton;
    @BindView(R.id.id_to_profile_multi_location_btn)
    Button mToMultiButton;
    @BindView(R.id.id_profile_img)
    ImageView mProfileImage;

    @BindString(R.string.app_profile)
    String profile;

    private Bitmap mBitmap;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;

    @Override
    public void onCreateBase(Bundle savedInstanceState) throws XBaseException {
        super.onCreateBase(savedInstanceState);
        setContentView(R.layout.activity_profile);
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
        ButterKnife.bind(this);
        initRxBindings();
        initViews();
    }

    private void initViews() {
        this.showTitle(true, profile);
        switch (XAppDataUtils.getInstance().getStyle()) {
            case "a":
                mToSwitchMapButton.setText("热力");
                break;
            case "b":
                mToSwitchMapButton.setText("卫星");
                break;
            default:
                mToSwitchMapButton.setText("平面");
                break;
        }

//        if (XAppDataUtils.getInstance().getProfileImg() != null) {
//            mBitmap = XAppDataUtils.getInstance().getProfileImg();
//            mProfileImage.setImageBitmap(mBitmap);
//        }
    }

    private void initRxBindings() {
        RxView.clicks(mBackToMapButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        RxView.clicks(mToSetButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
            }
        });

        RxView.clicks(mToFavoriteButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XFavoriteActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mToPlanGuideButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XPlanGuideActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mToDistanceButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XDistanceActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mToSwitchMapButton).map(new Function<Object, String>() {
            @Override
            public String apply(Object o) throws Exception {
                if (mToSwitchMapButton.getText().equals("平面")){
                    return "a";
                }else if(mToSwitchMapButton.getText().equals("热力")){
                    return "b";
                }else{
                    return "c";
                }

            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String b) throws Exception {
                XSwitchMessageEvent.getInstance().setSwitch(b);
                XAppDataUtils.getInstance().setStyle(b);
                if( b.equals("a")){
                    mToSwitchMapButton.setText("热力");
                }else if(b.equals("b")){
                    mToSwitchMapButton.setText("卫星");

                }else{
                    mToSwitchMapButton.setText("平面");

                }
            }
        });

        RxView.clicks(mToMultiButton).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent = new Intent(XAccountActivity.this, XMultiLocationActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mProfileImage).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                showChoosePicDialog();
            }
        });

    }

    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(XAccountActivity.this);
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent();
                        openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        openCameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        tempUri = FileProvider.getUriForFile(XAccountActivity.this, XAccountActivity.this.getApplicationContext().getPackageName() + ".provider", new File(Environment
                                .getExternalStorageDirectory(), "temp.jpg"));
                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("XAccountActivity", String.valueOf(resultCode) + " : " + String.valueOf(requestCode));
        if (resultCode == XAccountActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    Log.i("XAccountActivity", "onActivityResult: I am Here");
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 裁剪图片方法实现
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        Log.i("XAccountActivity", uri.toString());
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        XAccountActivity.this.startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
//            ArrayList<Bitmap> temp = new ArrayList<>();
//            temp.add(mBitmap);
//            XAppDataUtils.getInstance().setProfileImg(temp);
            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
            mProfileImage.setImageBitmap(mBitmap);//显示图片
            //在这个地方可以写上上传该图片到服务器的代码，后期将单独写一篇这方面的博客，敬请期待...
        }
    }

}
