package com.gerenvip.volley.net.listener;

import android.graphics.Bitmap;

/**
 * Created by wangwei-ps on 2014/9/9.
 */
public interface OnBatchLoadImageListener {
    /**
     * 加载成功
     *
     * @param url  对应的url
     * @param icon bitmap图片
     */
    public void onImageLoadSuccess(String url, Bitmap icon);

    /**
     * 加载失败
     *
     * @param url
     */
    public void onImageLoadFail(String url);

    /**
     * 加载完成(成功，失败，无响应等等)
     *
     * @param url 图片对应的url
     */
    public void onImageLoadFinish(String url);
}
