package com.gerenvip.volley.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by wangwei-ps on 2014/9/3.
 */
public class VolleyLoadImageListActivity extends Activity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_list);
        mListView = (ListView) findViewById(R.id.list_view);
        DemoImageAdaper adapter = new DemoImageAdaper(this);
        mListView.setAdapter(adapter);
    }
}
