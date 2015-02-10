package com.gerenvip.volley.net.listener;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by wangwei-ps on 2014/9/1.
 */
public abstract class AnimationImageListener implements ImageLoader.ImageListener {
    private Context mCxt;
    private ImageView mImageView;
    private int mErrorResId;

    public AnimationImageListener(Context cxt, ImageView imageView, int errorResId) {
        mCxt = cxt;
        mImageView = imageView;
        mErrorResId = errorResId;
    }

    public AnimationImageListener(Context cxt, ImageView imageView, int loadingResId, int errorResId) {
        this(cxt, imageView, errorResId);
        if (loadingResId > 0) {
            mImageView.setImageResource(loadingResId);
        }
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        if (mImageView != null) {
            if (response.getBitmap() != null) {
                int animResId = getAnimationResId();
                if (animResId > 0) {
                    mImageView.startAnimation(AnimationUtils.loadAnimation(mCxt, animResId));
                }
                mImageView.setImageBitmap(response.getBitmap());
            } else if (mErrorResId != 0) {
                mImageView.setImageResource(mErrorResId);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mErrorResId != 0) {
            mImageView.setImageResource(mErrorResId);
        }
    }

    public abstract int getAnimationResId();
}
