package com.gerenvip.volley.net.listener;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by wangwei-ps on 2014/9/10.
 */
public class CustomImageListener implements ImageLoader.ImageListener {
    private String attachUrl;
    private OnBatchLoadImageListener mListener;

    public CustomImageListener(String url, OnBatchLoadImageListener listener) {
        attachUrl = url;
        mListener = listener;
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        if (mListener == null) {
            return;
        }
        if (response.getBitmap() != null) {
            mListener.onImageLoadSuccess(attachUrl, response.getBitmap());
            mListener.onImageLoadFinish(attachUrl);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mListener != null) {
            mListener.onImageLoadFail(attachUrl);
            mListener.onImageLoadFinish(attachUrl);
        }
    }
}
