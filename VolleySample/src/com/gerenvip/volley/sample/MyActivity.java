package com.gerenvip.volley.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.gerenvip.volley.net.NetClient;
import com.gerenvip.volley.net.VolleyController;
import com.gerenvip.volley.net.listener.INetClientListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyActivity extends Activity {
    private static final String TAG = "MyActivity";

    private TextView mTv;

    private static final String url = "http://m.360.cn/";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTv = (TextView) findViewById(R.id.tv);
    }

    public void loadImage(View view) {

        Intent i = new Intent(this, NetImageActivity.class);
        startActivity(i);
    }

    public void loadImageList(View view) {
        Intent i = new Intent(this, VolleyLoadImageListActivity.class);
        startActivity(i);
    }

    public void requestGetString(View view) {
        requestGetStr();
    }

    public void requestPostString(View view) {
        requestPostStr();
    }

    private void requestGetStr() {
        NetClient.getInstance().executeGetRequest(url, new INetClientListener() {
            @Override
            public void onSuccess(String content, Object... msg) {
                Log.e(TAG, "MyActivity#requestGetStr$onSuccess:");
                mTv.setText("requestGetStr#onSuccess:" + content);
            }

            @Override
            public void onFailure(int errorCode, Object msg) {
                Log.e(TAG, "MyActivity#requestGetStr$onFailure custom errorCode=:" + errorCode + ";http errorCode=" + msg);
                mTv.setText("MyActivity#requestString$onFailure: errorCode" + msg);
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "MyActivity#onFinish");
            }
        });
    }

    private void requestPostStr() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("JSESSIONID", "");
        header.put("TOKEN", "");
        header.put("IMEI", "sgdfgdfgh");

        ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<String, String>();
        urlParams.put("content", "da sha cha");
        NetClient.getInstance().executePostRequest(UrlHelper.TEST_POST_URL, null, urlParams, new INetClientListener() {
            @Override
            public void onSuccess(String content, Object... msg) {
                Log.e(TAG, "requestPostStr#onSuccess:" + content);
                mTv.setText("requestPostStr#onSuccess:" + content);
            }

            @Override
            public void onFailure(int errorCode, Object msg) {
                Log.e(TAG, "requestPostStr#onFailure:" + errorCode + ";http errorCode=" + msg);
                mTv.setText("MyActivity#requestString$onFailure: errorCode" + msg);
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "requestPostStr#onFinish");
            }
        });
    }


    public void batchRequestImage(View view) {
        Intent i = new Intent(this, BatchImageActivity.class);
        startActivity(i);
    }

    public void test(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VolleyController.getInstance().clearImageCache();
    }
}
