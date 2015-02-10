package com.gerenvip.volley.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.DiskBasedCache.CountingInputStream;
import com.gerenvip.volley.net.ImageOptions;
import com.gerenvip.volley.net.LruBitmapCache;
import com.gerenvip.volley.net.VolleyController;

import java.io.*;

public class VolleyCacheMgr {
    /**
     * 获取Volley缓存的总大小
     *
     * @param context
     * @return
     */
    public static long getVolleyCacheSize(Context context) {
        long size = 0;
        File cacheDir = new File(FeatureConfig.ROOT_CACHE_PATH_BASE, FeatureConfig.DEFAULT_CACHE_DIR);
        if (cacheDir.exists() && cacheDir.isDirectory()) {
            size += getFileLength(cacheDir);
        }
        return size;
    }

    /**
     * 获取制定目录文件大小
     *
     * @param directory
     * @return
     */
    private static long getFileLength(File directory) {
        long size = 0;
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    size += getFileLength(item);
                } else {
                    size += item.length();
                }
            }
        }
        return size;
    }

    /**
     * 从磁盘中移除缓存
     *
     * @param url 需要移除的缓存数据对应的url
     */
    public static void removeCache(String url) {
        VolleyController.getInstance().getRequestQueue().getCache().remove(url);
    }

    /**
     * 从磁盘中清空cache
     */
    public static void clearCache() {
        VolleyController.getInstance().getRequestQueue().getCache().clear();
    }

    /**
     * ImageCache的的cachekey
     *
     * @param url       图片的url
     * @param maxWidth  图片的宽
     * @param maxHeight 图片的高
     * @return
     */
    public static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return new StringBuilder(url.length() + 12).append("#W").append(maxWidth)
                .append("#H").append(maxHeight).append(url).toString();
    }

    /**
     * 将磁盘缓存中的imageview拷贝到制定的文件目录中<br/>
     * <b>请在子线程访问<b/>
     *
     * @param url    图片对应的下载地址
     * @param dstDir 保存图片的文件路径
     * @return true 拷贝成功，否则为false
     */
    public static boolean copyImageFromDiskCache(String url, File dstDir) {
        throwIfOnMainThread();
        if (null == dstDir) {
            return false;
        }
        Cache.Entry entry = getCacheFromDiskCache(url);
        if (null == entry || null == entry.data) {
            //volley没有缓存该图片，需要主动下载，便于本地使用
            return FileHelper.downloadFile(url, dstDir);
        }
        FileOutputStream fos = null;
        try {

            if (dstDir.getParentFile().isFile()) {
                dstDir.getParentFile().delete();
            }
            if (!dstDir.getParentFile().exists()) {
                dstDir.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(dstDir);
            fos.write(entry.data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 从磁盘cache中获取缓存
     *
     * @param url 请求对应的url
     * @return
     */
    public static Cache.Entry getCacheFromDiskCache(String url) {
        Cache.Entry entry = null;
        RequestQueue requestQueue = VolleyController.getInstance().getRequestQueue();
        if (requestQueue != null) {
            Cache cache = requestQueue.getCache();
            if (cache != null) {
                entry = cache.get(url);
            }
        }
        return entry;
    }

    private static void throwIfOnMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException(" copyImageFromDiskCache must not be invoked from the main thread.");
        }
    }

    private static String getFilenameForKey(String key) {
        int firstHalfLength = key.length() / 2;
        String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
        localFilename += String.valueOf(key.substring(firstHalfLength).hashCode());
        return localFilename;
    }

    /**
     * 从Disk缓存中获取图片
     *
     * @param url
     * @param maxWidth  想要加载图片的宽度
     * @param maxHeight 想要加载图片的高度
     * @return
     */
    public static Bitmap getImageFromDiskCache2(String url, int maxWidth, int maxHeight) {
        String fileName = getFilenameForKey(url);
        String dir = FeatureConfig.ROOT_CACHE_PATH_BASE + FeatureConfig.DEFAULT_CACHE_DIR;
        File file = new File(dir, fileName);
        if (!file.exists()) {
            return null;
        }
        CountingInputStream cis = null;
        try {
            cis = new CountingInputStream(new FileInputStream(file));
            DiskBasedCache.CacheHeader entry = DiskBasedCache.CacheHeader.readHeader(cis); // eat header
            if (entry == null) {
                return null;
            }
            byte[] data = DiskBasedCache.streamToBytes(cis,
                    (int) (file.length() - cis.bytesRead));

            Bitmap bitmap = null;
            ImageOptions options = computeBitmapSize(data);
            int actualWidth = options.getMaxWidth();
            int actualHeight = options.getMaxHeight();
            // 如果照片实际大小大于手机的分辨率，实际只加载手机分辨率大小的图片
            if (actualWidth > FeatureConfig.widthPixels) {
                maxWidth = FeatureConfig.widthPixels;
            }
            if (actualHeight > FeatureConfig.heightPixels) {
                maxHeight = FeatureConfig.heightPixels;
            }
            int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth);
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
                    actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length, decodeOptions);
            if (tempBitmap != null
                    && (tempBitmap.getWidth() > desiredWidth || tempBitmap
                    .getHeight() > desiredHeight)) {
                // 缩放图片
                bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
                        desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
            return bitmap;
        } catch (IOException e) {
            VolleyLog.d("%s: %s", file.getAbsolutePath(), e.toString());
            return null;
        } finally {
            if (cis != null) {
                try {
                    cis.close();
                } catch (IOException ioe) {
                    return null;
                }
            }
        }
    }

    /**
     * 从Disk缓存中获取图片
     *
     * @param url
     * @param maxWidth  想要加载图片的宽度
     * @param maxHeight 想要加载图片的高度
     * @return
     */
    public static Bitmap getImageFromDiskCache(String url, int maxWidth, int maxHeight) {
        /**
         * TODO:wangwei note：注意，由于LruBitmapCache 使用的是最近最少访问的机制，所以可能会被回收，如果从LruBitmapCache 中
         * 取图片缓存，需要使用的key是getCacheKey（）获得的，因为该key对应ImageCache，与系统保持一致
         * 如果从requestQueue中获得cache，key值必须是url
         */
        Cache.Entry entry = getCacheFromDiskCache(url);
        if (entry != null && entry.data != null && entry.data.length != 0) {
            return doParseBitmapFromCache(entry, maxWidth, maxHeight);
        } else {
            return null;
        }
    }

    /**
     * 从缓存数据中解析bitmap图片<br/>
     * 为了尽量防止oom 采取最大解析手机分辨率大小的图片 方案
     *
     * @param entry
     */
    private static Bitmap doParseBitmapFromCache(Cache.Entry entry, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        try {
            byte[] data = entry.data;

            ImageOptions options = computeBitmapSize(data);
            int actualWidth = options.getMaxWidth();
            int actualHeight = options.getMaxHeight();
            //如果照片实际大小大于手机的分辨率，实际只加载手机分辨率大小的图片
            if (actualWidth > FeatureConfig.widthPixels) {
                maxWidth = FeatureConfig.widthPixels;
            }
            if (actualHeight > FeatureConfig.heightPixels) {
                maxHeight = FeatureConfig.heightPixels;
            }
            int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth);
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize =
                    findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap =
                    BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                //缩放图片
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 计算bitmap图片的实际大小噜啦啦
     *
     * @param data notNull
     * @return ImageOptions 注意 只有{@link com.qihoo.volley.net.ImageOptions#getMaxHeight}和{@link com.qihoo.volley.net.ImageOptions#getMaxWidth}有效
     */
    private static ImageOptions computeBitmapSize(byte[] data) {
        if (data == null) {
            return null;
        }
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        ImageOptions options = new ImageOptions.Builder().setImageSize(actualWidth, actualHeight).build();
        return options;
    }

    /**
     * 计算一个合适的图片宽高
     * @param actualWidth
     * @param actualHeight
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    public static Bitmap getImageFromImageCache(String url, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        String cacheKey = getCacheKey(url, maxWidth, maxHeight);
        LruBitmapCache lruBitmapCache = VolleyController.getInstance().getLruBitmapCache();
        if (lruBitmapCache != null) {
            bitmap = lruBitmapCache.getBitmap(cacheKey);
        }
        return bitmap;
    }

    public static Bitmap getImageFromCache(String url, ImageOptions options) {

        int maxWith = 0;
        int maxHeight = 0;
        if (null != options) {
            maxWith = options.getMaxWidth();
            maxHeight = options.getMaxHeight();
        }

        Bitmap imageCache = getImageFromImageCache(url, maxWith, maxHeight);
        if (imageCache == null) {
            imageCache = getImageFromDiskCache(url, maxWith, maxHeight);
        }

        return imageCache;
    }

    /**
     * 判断图片是否缓存在LruCache中
     * 注意，通过ImageRequest加载的图片不建议使用该方法判断
     *
     * @param requestUrl 图片对应的下载地址
     * @param maxWidth   图片对应的宽度
     * @param maxHeight  图片对应的高度
     * @return
     */
    public static boolean isImageCached(String requestUrl, int maxWidth, int maxHeight) {
        return VolleyController.getInstance().getImageLoader().isCached(requestUrl, maxWidth, maxHeight);
    }

    /**
     * 拿到缓存数据，注意此处拿到的cache是Request的chache，而不是ImageCache
     *
     * @param url 请求的url地址
     * @return String类型的缓存数据
     */
    public static String loadInCache(String url) {
        Cache.Entry entry = VolleyController.getInstance().getRequestQueue().getCache().get(url);
        String data = null;
        if (entry == null) {
            return data;
        }
        try {
            data = new String(entry.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }
}
