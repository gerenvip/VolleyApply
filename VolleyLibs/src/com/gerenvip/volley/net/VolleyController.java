package com.gerenvip.volley.net;


import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.gerenvip.volley.utils.FeatureConfig;
import com.gerenvip.volley.utils.VolleyLogHelper;

/**
 * Volley控制类
 */
public class VolleyController {

    private static final String TAG = VolleyController.class.getSimpleName();
    private static Context mContext;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private LruBitmapCache mLruBitmapCache;
    private Byte[] mLock = new Byte[0];

    /**
     * 使用之前要首先调用该方法
     *
     * @param context
     */
    public static final void intVolley(Context context) {
        mContext = context.getApplicationContext();
        Resources resources = mContext.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        FeatureConfig.widthPixels = displayMetrics.widthPixels;
        FeatureConfig.heightPixels = displayMetrics.heightPixels;
    }

    public static final void setDebug(boolean debug) {
        VolleyLogHelper.setDebugEnable(debug);
    }

    private VolleyController() {
    }

    public static VolleyController getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 拿到一个RequestQueue队列
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            synchronized (mLock) {
                if (mRequestQueue == null) {
                    mRequestQueue = Volley.newRequestQueue(mContext);
                }
            }
        }
        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> Request addToRequestQueue(Request<T> req, String tag) {
        if (null != req) {
            // set the default tag if tag is empty
            req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
            VolleyLog.d("Adding request to queue: %s", req.getUrl());
            return getRequestQueue().add(req);
        } else {
            VolleyLogHelper.d(TAG, "req should not null");
        }
        return null;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            synchronized (mLock) {
                if (mImageLoader == null) {
                    LruBitmapCache lruBitmapCache = getLruBitmapCache();
                    mImageLoader = new ImageLoader(this.mRequestQueue, lruBitmapCache);
                }
            }
        }
        return mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (null == mLruBitmapCache) {
            synchronized (mLock) {
                if (null == mLruBitmapCache) {
                    mLruBitmapCache = new LruBitmapCache(mContext);
                    VolleyLogHelper.d(TAG, "new LruBitmapCache");
                }
            }
        }
        return mLruBitmapCache;
    }

    /**
     * 清空内存中的图片缓存
     * <p/>
     * <strong>Note:<strong/> 该方法清空LruCache中的所有图片，但是内存回收需要一定的时间，内存不会立刻降低
     * <p/>
     */
    public void clearImageCache() {
        if (mLruBitmapCache != null) {
            VolleyLogHelper.d(TAG, "imageCache size before evictAll=" + mLruBitmapCache.size());
            mLruBitmapCache.evictAll();
            VolleyLogHelper.d(TAG, "imageCache size after evictAll=" + mLruBitmapCache.size());
            VolleyLogHelper.d(TAG, "clear ImageCache");
        }
    }

    private static class SingletonHolder {
        public static final VolleyController INSTANCE = new VolleyController();
    }


}
