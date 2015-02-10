package com.gerenvip.volley.sample;

import android.app.Application;
import com.gerenvip.volley.net.VolleyController;

/**
 * Created by wangwei-ps on 2014/8/29.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyController.intVolley(this);
        VolleyController.setDebug(true);
    }
}
