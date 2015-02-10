package com.gerenvip.volley.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.gerenvip.volley.net.listener.*;
import com.gerenvip.volley.utils.VolleyCacheMgr;
import com.gerenvip.volley.utils.VolleyLogHelper;
import com.gerenvip.volley.utils.VolleyUtils;

import java.util.List;
import java.util.Map;

public class NetClient {

    private static final String TAG = NetClient.class.getSimpleName();
    private static NetClient mNetClient;
    private final DefaultRequestFactory mRequestFactory;
    private ImageLoader mImageLoader;
    private VolleyController mVolleyController;
    private ImageOptions defaultOptions = null;

    private NetClient() {
        mRequestFactory = new DefaultRequestFactory();
        mImageLoader = VolleyController.getInstance().getImageLoader();
        mVolleyController = VolleyController.getInstance();
    }

    public static NetClient getInstance() {
        if (mNetClient == null) {
            synchronized (NetClient.class) {
                if (mNetClient == null) {
                    mNetClient = new NetClient();
                }
            }
        }
        return mNetClient;
    }


    /** *********************************************数据请求******************************************************************/
    /**
     * 执行一个不带缓存的request请求（不带tag）<br/>
     * note: 将使用默认tag "VolleyController"<br/>
     *TODO:wangwei 由于业务上暂时没有缓存的需求，所以新增该方法，如果日后需要缓存文字请求，请使用 {@link NetClient#executeRequestWithCache}
     * 由于DiskBasedCache 的缓存大小更为50M 缓存到磁盘的同时也会把CacheHeader缓存到内存中，有oom的风险，所以无特殊需要，建议不设缓存
     * @param request
     */
    public Request executeRequestNoCache(Request request) {
        return executeRequestNoCache(request, null);
    }

    /**
     * 执行一个 不带 缓存的request请求（带tag）<br/>
     * 建议使用的时候都带上tag，好方便cancel操作
     *
     * @param request 请求的request
     * @param tag     request Tag标记
     */
    public Request executeRequestNoCache(Request request, String tag) {
        setNoCache(request);
        return mVolleyController.addToRequestQueue(request, tag);
    }

    /**
     * 执行一个 带 缓存的request请求（带tag）,能缓存的前提条件是没有调用setNoCache方法
     * @param request
     * @param tag
     * @return
     */
    public Request executeRequestWithCache(Request request, String tag) {
        return mVolleyController.addToRequestQueue(request, tag);
    }

    /**
     * 执行一个简单的request请求<br/>
     *注意：该方法不提供get参数，请自行组拼到url后使用该方法
     * @param method        请求方式, 见{@link com.android.volley.Request.Method}
     * @param url           请求的url
     * @param headParams    头部参数
     * @param postParams    post请求的参数，当为get请求时，传入null
     * @param isShouldCache 是否需要缓存
     * @param tag           给request设置tag
     * @param listener      请求的回调， 不能为null，否则没有意义，并抛NullPointException
     */
    public Request executeRequest(int method, String url, Map<String, String> headParams, Map<String, String> postParams, boolean isShouldCache, String tag,
                              INetClientListener listener) {
        StringRequest stringRequest = mRequestFactory.produceStringRequest(method, url, headParams, postParams, makeSuccessListener(listener), makeErrorListener(listener));
        if (!isShouldCache) {
            setNoCache(stringRequest);
        }
       return executeRequestNoCache(stringRequest, tag);
    }

    /**
     * 返回类型是字节数组的请求方式
     *
     * @param method     求方式, 见{@link com.android.volley.Request.Method}
     * @param url        请求的url
     * @param headParams 头部参数
     * @param postParams post请求的参数，当为get请求时，传入null
     * @param listener   请求的回调， 不能为null，否则没有意义，并抛NullPointException
     */
    public Request executeRequest(int method, String url, Map<String, String> headParams, Map<String, String> postParams, INetClientBytesListener listener, String tag) {
        BytesRequest bytesRequest = mRequestFactory.produceBytesRequest(method, url, headParams, postParams, makeSuccessListener(listener), makeErrorListener(listener));
        return executeRequestNoCache(bytesRequest, tag);
    }

    /**
     * 执行一个简单的请求（get方式），如果需要post方式请使用{@link NetClient#executePostRequest}<br/>
     *
     * @param url      url
     * @param listener 请求的回调， 不能为null，否则没有意义，并抛NullPointException
     */
    public Request executeGetRequest(String url, INetClientListener listener) {
        return executeGetRequest(url, null, listener);
    }

    /**
     * 执行一个简单的get请求，可以设置请求参数
     * @param url 请求的url
     * @param headParams 头参数
     * @param listener
     */
    public Request executeGetRequest(String url, Map<String, String> headParams, INetClientListener listener) {
        return executeGetRequest(url, headParams, null, listener);
    }

    /**
     * 执行一个简单的get请求，可以设置头参数
     *
     * @param url
     * @param headParams    头参数
     * @param requestParams get请求参数
     * @param listener      请求的回调， 不能为null，否则没有意义，并抛NullPointException
     */
    public Request executeGetRequest(String url, Map<String, String> headParams, Map<String, String> requestParams, INetClientListener listener) {
        return executeGetRequest(url, headParams, requestParams, true, null, listener);
    }

    /**
     * 执行一个简单的get请求，可以设置头参数
     *
     * @param url url地址
     * @param headParams 头参数
     * @param requestParams 请求参数
     * @param isShouldCache 是否缓存
     * @param tag 请求tag，用于取消请求 {@link NetClient#cancelRequest}
     * @param listener
     * @return
     */
    public Request executeGetRequest(String url, Map<String, String> headParams, Map<String, String> requestParams, boolean isShouldCache, String tag, INetClientListener listener) {
        String requestUrl = VolleyUtils.appendUrlParams(url, requestParams);
        StringRequest strRequest = mRequestFactory.produceGetStringRequest(requestUrl, headParams, makeSuccessListener(listener), makeErrorListener(listener));
        if (!isShouldCache) {
            setNoCache(strRequest);
        }
        return executeRequestNoCache(strRequest, tag);
    }

    /**
     * 执行一个get请求，注意该方法回调中返回类型是byte[] 类型
     *
     * @param url 请求url
     * @param headParams 头参数
     * @param requestParams get请求参数
     * @param listener 请求的回调， 不能为null，否则没有意义，并抛NullPointException
     */
    public Request executeGetRequest(String url, Map<String, String> headParams, Map<String, String> requestParams, INetClientBytesListener listener) {
        String requestUrl = VolleyUtils.appendUrlParams(url, requestParams);
        return executeRequest(Request.Method.GET, requestUrl, headParams, null, listener, null);
    }

    /**
     * 执行一个post请求，注意该方法回调中返回类型是byte[]类型
     * @param url 请求url地址
     * @param headParams 头参数
     * @param postParams post请求参数
     * @param listener 请求的回调， 不能为null，否则没有意义，并抛NullPointException
     */
    public Request executePostRequest(String url, Map<String, String> headParams, Map<String, String> postParams, INetClientBytesListener listener) {
       return executeRequest(Request.Method.POST, url, headParams, postParams, listener, null);
    }

    /**
     * 执行一个简单的post请求
     *
     * @param url
     * @param headParams 头参数
     * @param postParams post请求参数
     * @param listener   请求的回调， 不能为null，否则没有意义，并抛NullPointException
     */
    public Request executePostRequest(String url, Map<String, String> headParams, Map<String, String> postParams, INetClientListener listener) {
        StringRequest strRequest = mRequestFactory.producePostStringRequest(url, headParams, postParams, makeSuccessListener(listener), makeErrorListener(listener));
        return executeRequestNoCache(strRequest);
    }

    /**
     * 执行一个简单的post请求
     *
     * @param url        url
     * @param postParams Post请求参数
     * @param listener   请求的回调函数，
     */
    public Request executePostRequest(String url, Map<String, String> postParams, INetClientListener listener) {
        return executePostRequest(url, null, postParams, listener);
    }


    /**
     * ************************************************************图片加载********************************************************************
     */

    /**
     * 给ImageLoader设置默认的加载图片和失败时显示的图片
     *
     * @param loadingResId  正在加载时显示的资源
     * @param errorResId    加载出错时显示的资源
     * @param imageMaxWidht 要显示图片的宽, 如果显示默认宽度，请赋值为0
     * @param imgMaxHeight  要显示图片的高，如果显示默认高度，请赋值为0<br/>
     *                      note:最终显示的图片大小并不是此处设置的大小，而是根据此处的值进行缩放处理
     */
    /*public void setDefaultImageLoaderRes(int loadingResId, int errorResId, int imageMaxWidht, int imgMaxHeight) {
        defaultOptions = new ImageOptions.Builder()
                .setImageOnLoading(loadingResId)
                .setImageOnFail(errorResId)
                .setImageSize(imageMaxWidht, imgMaxHeight)
                .build();
    }*/

    /**
     * 使用imageLoader请求一张图片
     *
     * @param url       图片的地址
     * @param imageView 显示图片的imageview实例
     * @return
     */
    public ImageLoader.ImageContainer loadImage(String url, ImageView imageView) {
        return loadImage(url, imageView, null);
    }

    /**
     * 使用imageLoader请求一张图片
     *
     * @param url       图片url地址
     * @param imageView imageView
     * @param options   展示图片的配置项 见：{@link ImageOptions}
     * @return
     */
    public ImageLoader.ImageContainer loadImage(String url, ImageView imageView, ImageOptions options) {
        if (null == options) {
            options = defaultOptions;
        }
        if (null != options) {
            return mImageLoader.get(url, ImageLoader.getImageListener(imageView, options.getImageResOnLoading(), options.getImageResOnFail()), options.getMaxWidth(),
                    options.getMaxHeight());
        } else {
            return mImageLoader.get(url, ImageLoader.getImageListener(imageView, 0, 0));
        }

    }

    /**
     * 批量加载图片，通过OnBatchLoadImageListener回调到业务层
     *
     * @param requestUrls
     * @param listener
     */
    public void batchLoadImage(List<String> requestUrls, OnBatchLoadImageListener listener) {
        batchLoadImage(requestUrls, null, listener);
    }

    public void batchLoadImage(List<String> requestUrls, ImageOptions options, OnBatchLoadImageListener listener) {
        int maxWith = 0;
        int maxHeight = 0;
        if (null != options) {
            maxWith = options.getMaxWidth();
            maxHeight = options.getMaxHeight();
        }
        for (String url : requestUrls) {
            mImageLoader.get(url, new CustomImageListener(url, listener), maxWith, maxHeight);
        }

    }

    public void batchLoadSingleImage(String url, ImageOptions options, OnBatchLoadImageListener listener) {
        int maxWith = 0;
        int maxHeight = 0;
        if (null != options) {
            maxWith = options.getMaxWidth();
            maxHeight = options.getMaxHeight();
        }

        mImageLoader.get(url, new CustomImageListener(url, listener), maxWith, maxHeight);
    }

    public ImageRequest batchLoadSingleImageWithHeader(String url, Map<String, String> header, ImageOptions options, OnBatchLoadImageListener listener, Bitmap.Config decodeConfig) {

        int maxWith = 0;
        int maxHeight = 0;
        if (null != options) {
            maxWith = options.getMaxWidth();
            maxHeight = options.getMaxHeight();
        }

        //取缓存
        Bitmap cachedBitmap;
        if (null == options) {
            //按默认大小去取缓存
            cachedBitmap = VolleyCacheMgr.getImageFromImageCache(url, 0, 0);
            if (cachedBitmap != null) {
                listener.onImageLoadSuccess(url, cachedBitmap);
                listener.onImageLoadFinish(url);
                VolleyLogHelper.e(TAG, "load from imageCache w=0 & h = 0 &url=" + url);
                return null;
            }

            options = defaultOptions;
            if (options != null) {//按照预设的大小去取缓存
                cachedBitmap = VolleyCacheMgr.getImageFromImageCache(url, options.getMaxWidth(), options.getMaxHeight());
                if (cachedBitmap != null) {
                    listener.onImageLoadSuccess(url, cachedBitmap);
                    listener.onImageLoadFinish(url);
                    VolleyLogHelper.e(TAG, "load from imageCache defaultOptions w=" + options.getMaxWidth() + "; h=" + options.getMaxHeight() + "&url=" + url);
                    return null;
                }
            }
        } else {
            cachedBitmap = VolleyCacheMgr.getImageFromImageCache(url, options.getMaxWidth(), options.getMaxHeight());
            if (cachedBitmap != null) {
                listener.onImageLoadSuccess(url, cachedBitmap);
                listener.onImageLoadFinish(url);
                VolleyLogHelper.e(TAG, "load from imageCache custom Options w=" + options.getMaxWidth() + "; h=" + options.getMaxHeight() + "&url=" + url);
                return null;
            }
        }

        ImageRequest imgRequest;
        if (null == options) {
            imgRequest = mRequestFactory.produceImageRequestWithOutImageView(url, header, listener, 0, 0, true, decodeConfig);
        } else {
            imgRequest = mRequestFactory.produceImageRequestWithOutImageView(url, header, listener, maxWith, maxHeight, true, decodeConfig);
        }

        executeRequestWithCache(imgRequest, null);

        return imgRequest;
    }

    /**
     * 加载图片时带动画<br/>
     * 如果不需要动画，请使用{@link NetClient#loadImage}
     *
     * @param context   上下文
     * @param url       图片地址
     * @param imageView
     * @param options   展示图片的配置项 见：{@link ImageOptions}
     * @param animResId anim in xml
     */
    public void loadImageWithAnimation(Context context, String url, ImageView imageView, ImageOptions options, int animResId) {
        if (null == options) {
            options = defaultOptions;
        }
        AnimationImageListener animImageListener;
        if (null == options) {
            animImageListener = getAnimationImageListener(context, imageView, 0, 0, animResId);
            mImageLoader.get(url, animImageListener);
        } else {
            animImageListener = getAnimationImageListener(context, imageView, options.getImageResOnLoading(), options.getImageResOnFail(), animResId);
            mImageLoader.get(url, animImageListener, options.getMaxWidth(), options.getMaxHeight());
        }
    }

    private AnimationImageListener getAnimationImageListener(Context context, ImageView imageView, int loadingResId, int errorResId, final int animResId) {
        return new AnimationImageListener(context, imageView, loadingResId, errorResId) {
            @Override
            public int getAnimationResId() {
                if (animResId < 1) {
                    return 0;
                }
                return animResId;
            }
        };
    }

    /**
     * 使用imageRequest来加载图片
     * TODO:wangwei 该方法加载图片会比ImageLoader加载图片内存峰值高，建议使用{@link NetClient#loadImage(String, android.widget.ImageView, ImageOptions)}
     *
     * @param url           图片url地址
     * @param imageView     imageview控件对象
     * @param options       展示图片的配置项 见：{@link ImageOptions}
     * @param isShouldCache 如果为false，就不使用cache
     * @param decodeConfig  请求图片的质量
     * @param tag           给request请求添加一个tag，可以为null
     * @return 返回一个ImageRequest，并且已经执行加载图片的任务
     */
    public ImageRequest loadImageByRequest(String url, final ImageView imageView, ImageOptions options, boolean isShouldCache, Bitmap.Config decodeConfig, String tag) {
        Bitmap cachedBitmap;
        //取缓存
        if (null == options) {
            //按默认大小去取缓存
            cachedBitmap = VolleyCacheMgr.getImageFromImageCache(url, 0, 0);
            if (cachedBitmap != null) {
                imageView.setImageBitmap(cachedBitmap);
                VolleyLogHelper.e(TAG, "load from imageCache w=0 & h = 0 &url=" + url);
                return null;
            }

            options = defaultOptions;
            if (options != null) {//按照预设的大小去取缓存
                cachedBitmap = VolleyCacheMgr.getImageFromImageCache(url, options.getMaxWidth(), options.getMaxHeight());
                if (cachedBitmap != null) {
                    imageView.setImageBitmap(cachedBitmap);
                    VolleyLogHelper.e(TAG, "load from imageCache defaultOptions w=" + options.getMaxWidth() + "; h=" + options.getMaxHeight() + "&url=" + url);
                    return null;
                }
            }
        } else {
            cachedBitmap = VolleyCacheMgr.getImageFromImageCache(url, options.getMaxWidth(), options.getMaxHeight());
            if (cachedBitmap != null) {
                imageView.setImageBitmap(cachedBitmap);
                VolleyLogHelper.e(TAG, "load from imageCache custom Options w=" + options.getMaxWidth() + "; h=" + options.getMaxHeight() + "&url=" + url);
                return null;
            }
        }

        //去加载
        if (null == options) {
            options = defaultOptions;
        }
        ImageRequest imgRequest;

        if (null == options) {
            imgRequest = mRequestFactory.produceImageRequest(url, imageView, 0, 0, 0, 0, isShouldCache, decodeConfig);
        } else {
            imgRequest = mRequestFactory.produceImageRequest(url, imageView, options.getImageResOnLoading(), options.getImageResOnFail(), options.getMaxWidth(),
                    options.getMaxHeight(),
                    isShouldCache,
                    decodeConfig);
        }

        executeRequestWithCache(imgRequest, tag);
        return imgRequest;
    }

    /**
     * 使用ImageRequest加载图片
     *
     * @param url       图片url
     * @param imageView imageview实例
     * @return
     */
    public ImageRequest loadImageByRequest(String url, ImageView imageView) {
        return loadImageByRequest(url, imageView, null, true, Bitmap.Config.RGB_565, null);
    }

    /**
     * 构建Request的请求成功回调
     *
     * @param listener 不能为null，否则没有意义
     * @return Response.Listener
     */
    private <T> Response.Listener<T> makeSuccessListener(final INetClientBaseListener<T> listener) {
        Response.Listener<T> successListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (null != listener) {
                    listener.onSuccess(response, null);
                    listener.onFinish();
                } else {
                    VolleyLogHelper.e(TAG, "INetClientLitener is null");
                    throw new NullPointerException("NetClient # makeSuccessListener :listener == null");
                }
            }
        };
        return successListener;
    }

    /**
     * 创建接收异常的listener
     *
     * @param listener
     * @return
     */
    private Response.ErrorListener makeErrorListener(final INetClientBaseListener listener) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                processError(error, listener);
            }
        };
        return errorListener;
    }

    private void processError(VolleyError error, INetClientBaseListener listener) {
        int errorCode = VolleyErrorHelper.ERROR_OTHERS;
        if (null != listener) {
            if (null == error) {
                listener.onFailure(errorCode, null);
                return;
            }
            try {
                errorCode = VolleyErrorHelper.getErrorMessage(error);
                listener.onFailure(errorCode, (error.networkResponse == null) ? 0 : error.networkResponse.statusCode);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //请求完成的回调
                listener.onFinish();
            }
        }
    }

    /**
     * 关闭cache功能,默认request是缓存的
     *
     * @param request
     * @return
     */
    private Request setNoCache(Request request) {
        if (null != request) {
            request.setShouldCache(false);
        }
        return request;
    }

    /**
     *取消指定tag的请求,如果你默认没有指定tag，你的request的tag会被设置为VolleyController，你可以使用该tag来取消请求，但是会取消所有相同tag的请求<br/>
     *
     * @param tag
     */
    public void cancelRequest(String tag) {
        RequestQueue requestQueue = mVolleyController.getRequestQueue();
        if (requestQueue != null && !TextUtils.isEmpty(tag)) {
            requestQueue.cancelAll(tag);
        }
    }

    /**
     * 取消所有的请求
     */
    public void cancelAllRequest() {
        RequestQueue requestQueue = mVolleyController.getRequestQueue();
        if (requestQueue != null) {
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }
}
