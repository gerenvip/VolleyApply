package com.gerenvip.volley.net;

/**
 * Created by wangwei-ps on 2014/9/5.
 */
public final class ImageOptions {

    private final int imageResOnLoading;
    private final int imageResOnFail;
    private final int imageMaxWidth;
    private final int imageMaxHeight;

    private ImageOptions(Builder builder) {
        imageResOnLoading = builder.imageResOnLoading;
        imageResOnFail = builder.imageResOnFail;
        imageMaxWidth = builder.imageMaxWidth;
        imageMaxHeight = builder.imageMaxHeight;
    }

    /**
     * 拿到正在加载时需要展示的图片资源
     *
     * @return
     */
    public int getImageResOnLoading() {
        return imageResOnLoading;
    }

    /**
     * 拿到正在加载失败时需要展示的图片资源
     *
     * @return
     */
    public int getImageResOnFail() {
        return imageResOnFail;
    }

    /**
     * 获取要展示图片的宽
     *
     * @return
     */
    public int getMaxWidth() {
        return imageMaxWidth;
    }

    /**
     * 获取要展示图片的高
     *
     * @return
     */
    public int getMaxHeight() {
        return imageMaxHeight;
    }

    public static class Builder {
        private int imageResOnLoading = 0;
        private int imageResOnFail = 0;
        private int imageMaxWidth = 0;
        private int imageMaxHeight = 0;

        public ImageOptions build() {
            return new ImageOptions(this);
        }

        /**
         * 设置默认加载图片资源
         *
         * @param imageRes
         * @return
         */
        public Builder setImageOnLoading(int imageRes) {
            imageResOnLoading = imageRes;
            return this;
        }

        /**
         * 设置加载失败的图片
         *
         * @param imageRes
         * @return
         */
        public Builder setImageOnFail(int imageRes) {
            imageResOnFail = imageRes;
            return this;
        }

        /**
         * 设置加载图片的宽高<br/>
         * note:最终显示的图片大小并不是此处设置的大小，而是根据此处的值进行缩放处理
         *
         * @param maxWidth
         * @param maxHeight
         * @return
         */
        public Builder setImageSize(int maxWidth, int maxHeight) {
            imageMaxWidth = maxWidth;
            imageMaxHeight = maxHeight;
            return this;
        }
    }
}
