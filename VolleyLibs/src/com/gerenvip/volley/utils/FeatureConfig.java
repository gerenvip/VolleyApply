package com.gerenvip.volley.utils;

import android.os.Environment;

/**
 * Created by wangwei-ps on 2014/9/9.
 */
public class FeatureConfig {

    public static final String ROOT_CACHE_PATH_BASE = Environment.getExternalStorageDirectory().getPath() + "/VolleyApply/";
    /**
     * 缓存目录
     */
    public static final String DEFAULT_CACHE_DIR = "data";

    public static int widthPixels;
    public static int heightPixels;
}
