package com.gerenvip.volley.net;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

import java.lang.ref.WeakReference;

public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {
    private static final String TAG = "LruBitmapCache";
    /**
     * 设置图片缓存最大允许占用15%应用程序内存
     */
    private static final float MAX_MEMORY_CACHE = 0.15f;

    public LruBitmapCache(Context context) {
        this(getDefaultLruCacheSize(context));
    }

    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    /**
     * 重载方法
     *
     * @param cxt
     * @param percent 指定的cache占程序内存的百分比
     */
    public LruBitmapCache(Context cxt, float percent) {
        this(getAllocatedMemorry(cxt, percent));
    }

    public static int getDefaultLruCacheSize(Context context) {
        return getAllocatedMemorry(context, MAX_MEMORY_CACHE);
    }

    private static int getAllocatedMemorry(Context context, float percent) {
        int memory = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        return Math.round(memory * percent) * 1024 * 1024;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        if (evicted) {
            //oldValue.recycle();
            WeakReference reference = new WeakReference(oldValue);
            reference = null;//TODO：wangwei 加快内存的回收
            //BLog.e(TAG, "entryRemoved...because memorry key:" + key);
        } else {
            //BLog.e(TAG, "entryRemoved...because  put or remove method");
        }
    }
}
