package com.gerenvip.volley.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.gerenvip.volley.net.NetClient;
import com.gerenvip.volley.net.listener.OnBatchLoadImageListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangwei-ps on 2014/9/9.
 */
public class BatchImageActivity extends Activity {

    private static final String TAG = "BatchImageActivity";
    private GridView mGridView;
    private String[] names = {"火车票", "淘宝",
            "天猫"};
    private String[] urls = {"http://p0.qhimg.com/t01a754fc8f4bb69bf3.png",
            "http://test.designer.c-launcher.com/resources/wallpaper/img/246/5397d1310cf267d0f0d15dde/1402458417493/wallpaper_s.jpg",
            "http://test.designer.c-launcher.com/resources/wallpaper/img/848/5397d1250cf267d0f0d15dd8/1402458405568/wallpaper_s.jpg"
    };

    private HashMap<String, Bitmap> mIconCache = null;
    private List<String> mRequestUrls = null;
    private GridViewAdapter adapter;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.batch_image);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mIconCache = new HashMap<String, Bitmap>();
        adapter = new GridViewAdapter();
        mGridView.setAdapter(adapter);
        mRequestUrls = Arrays.asList(urls);
        NetClient.getInstance().batchLoadImage(mRequestUrls, new OnBatchLoadImageListener() {
            @Override
            public void onImageLoadSuccess(String url, Bitmap icon) {
                if (!mIconCache.containsKey(url)) {
                    mIconCache.put(url, icon);
                    Log.e(TAG, "mIconCache.size=" + mIconCache.size());
                    count += 1;
                    if (mRequestUrls.size() == count) {
                        adapter.notifyDataSetInvalidated();
                    }
                }
            }

            @Override
            public void onImageLoadFail(String url) {
                Log.e(TAG, "fail load image=" + url);
            }

            @Override
            public void onImageLoadFinish(String url) {
                Log.e(TAG, "onImageLoadFinish url=" + url);
            }
        });
    }

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(),
                    R.layout.grid_home_item, null);
            ImageView iv_logo = (ImageView) view
                    .findViewById(R.id.iv_item_logo);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_item_name);
            tv_name.setText(names[position]);
            String url = urls[position];
            iv_logo.setImageBitmap(mIconCache.get(url));
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
