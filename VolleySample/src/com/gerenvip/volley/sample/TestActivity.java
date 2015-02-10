package com.gerenvip.volley.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.gerenvip.volley.net.ImageOptions;
import com.gerenvip.volley.utils.VolleyCacheMgr;

/**
 * Created by wangwei-ps on 2014/11/4.
 */
public class TestActivity extends Activity {

    private static final String TAG = "TestActivity";
    private ImageView mTestImg;
    private static final String testBigPic = "http://pic1.win4000.com/wallpaper/4/512ad4d27ced4.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        mTestImg = (ImageView) findViewById(R.id.test_img);
    }

    public void getCacheImg(View view) {
        ImageOptions options = new ImageOptions.Builder()
                .setImageOnLoading(android.R.drawable.ic_dialog_dialer)
                .setImageOnFail(android.R.drawable.ic_delete)
                .setImageSize(480, 800)
                .build();
        //Bitmap bitmap = VolleyCacheMgr.getImageFromCache(testBigPic, options);
        //Bitmap bitmap = VolleyCacheMgr.getImageFromDiskCache(testBigPic, 480, 800);
        Bitmap bitmap = VolleyCacheMgr.getImageFromDiskCache2(testBigPic, 480, 800);
        if (bitmap != null) {
            mTestImg.setImageBitmap(bitmap);
            int byteCount = bitmap.getRowBytes() * bitmap.getHeight();
            Log.e(TAG, "total bytes====" + byteCount);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.e(TAG, "bitmap width=" + width + ";; height=" + height);
        } else {
            Toast.makeText(this, "不能从缓存中获取图片", Toast.LENGTH_LONG).show();
        }
    }
}
