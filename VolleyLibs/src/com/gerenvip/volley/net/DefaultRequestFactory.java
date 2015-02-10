package com.gerenvip.volley.net;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.gerenvip.volley.net.listener.OnBatchLoadImageListener;
import com.gerenvip.volley.utils.VolleyCacheMgr;
import com.gerenvip.volley.utils.VolleyLogHelper;
import org.json.JSONObject;

import java.util.Map;

import static com.android.volley.Request.Method;

public class DefaultRequestFactory {
    private static final String TAG = DefaultRequestFactory.class.getSimpleName();

    /**
     * 生成一个StringRequest<br/>
     *
     * @param method        请求方式{@link com.android.volley.Request.Method},例如{@link com.android.volley.Request.Method#GET}
     * @param url           url
     * @param headParams    头参数
     * @param postParams    post请求参数 Map类型<br/>
     *                      如果是get请求，传入null
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public StringRequest produceStringRequest(int method, String url, final Map<String, String> headParams, final Map<String, String> postParams,
                                              Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest strRequest = new StringRequest(method, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headParams != null && !headParams.isEmpty()) {
                    return headParams;
                }
                return super.getHeaders();
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return postParams;
            }
        };
        return strRequest;
    }

    /**
     * 生成一个Get请求方式的StringRequest
     *
     * @param url           url
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public StringRequest produceGetStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        return produceGetStringRequest(url, null, listener, errorListener);
    }

    /**
     * 生成一个Get请求方式的StringRequest<br/>
     * 重载方法
     *
     * @param url           url
     * @param headParams    头参数
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public StringRequest produceGetStringRequest(String url, Map<String, String> headParams, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        return produceStringRequest(Method.GET, url, headParams, null, listener, errorListener);
    }

    /**
     * 生成一个Post请求方式的StringRequest<br/>
     *
     * @param url           url
     * @param headParams    头部参数
     * @param postParams    post请求参数 Map类型
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public StringRequest producePostStringRequest(String url, Map<String, String> headParams, Map<String, String> postParams, Response.Listener<String> listener,
                                                  Response.ErrorListener errorListener) {
        return produceStringRequest(Method.POST, url, headParams, postParams, listener, errorListener);
    }

    /**
     * 生成一个Post请求方式的StringRequest<br/>
     * {@link DefaultRequestFactory#producePostStringRequest}
     *
     * @param url           url
     * @param postParams    post请求参数 Map类型
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public StringRequest producePostStringRequest(String url, Map<String, String> postParams, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        return producePostStringRequest(url, null, postParams, listener, errorListener);
    }

    /**
     * 生成一个Get请求的JsonObjectRequest
     *
     * @param url           url
     * @param headParams    头参数
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public JsonObjectRequest produceGetJsonRequest(String url, final Map<String, String> headParams, Response.Listener<JSONObject> listener,
                                                   Response.ErrorListener errorListener) {

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.GET, url, null, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headParams != null && !headParams.isEmpty()) {
                    return headParams;
                }
                return super.getHeaders();
            }
        };
        return jsonRequest;
    }

    /**
     * 生成一个Get请求的JsonRequest，重载方法
     *
     * @param url           url
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public JsonObjectRequest produceGetJsonRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        return produceGetJsonRequest(url, null, listener, errorListener);
    }

    /**
     * 生成一个Post请求的JsonRequest<br/>
     * 由于使用系统自带的JsonRequest 在post请求的情况下服务端接收不到Params，即JsonRequest的getParams()方法不生效，<br/>
     * 所以使用自定义的
     *
     * @param url           url
     * @param headParams    头参数
     * @param postParams    post请求参数 Map类型
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public CustomJsonRequest producePostJsonRequest(String url, final Map<String, String> headParams, Map<String, String> postParams, Response.Listener<JSONObject> listener,
                                                    Response.ErrorListener errorListener) {
        final CustomJsonRequest jsObjRequest = new CustomJsonRequest(Method.POST, url, postParams, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headParams != null && !headParams.isEmpty()) {
                    return headParams;
                }
                return super.getHeaders();
            }
        };
        return jsObjRequest;
    }


    /**
     * 生成一个ImageRequest
     *
     * @param url           图片url地址
     * @param imageView     imageview实例
     * @param loadingResId  正在加载时显示的图片资源
     * @param errorResId    加载错误时显示的图片资源
     * @param maxWidth      图片的最大宽度
     * @param maxHeight     图片的最大高度
     * @param isShouldCache 是否缓存，true：开启lruCache和DiskCache双缓存, 反之，不做任何缓存
     * @param decodeConfig  图片的质量 {@link android.graphics.Bitmap.Config}
     * @return
     */
    public ImageRequest produceImageRequest(final String url, final ImageView imageView, int loadingResId, final int errorResId, int maxWidth, int maxHeight,
                                            final boolean isShouldCache, Bitmap.Config decodeConfig) {
        if (loadingResId > 0) {
            imageView.setImageResource(loadingResId);
        }
        if (maxWidth < 1) {
            maxWidth = 0;
        }
        if (maxHeight < 1) {
            maxHeight = 0;
        }
        final int finalMaxHeight = maxHeight;
        final int finalMaxWidth = maxWidth;
        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                VolleyLogHelper.e("NetClient", "load img use request from server or disk cache");
                imageView.setImageBitmap(response);
                if (isShouldCache) {//在ImageCache中也缓存一份，该缓存机制执行lru算法
                    VolleyController.getInstance().getLruBitmapCache().putBitmap(VolleyCacheMgr.getCacheKey(url, finalMaxWidth, finalMaxHeight), response);
                }
            }
        }, maxWidth, maxHeight, decodeConfig, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(errorResId);
            }
        });
        if (!isShouldCache) {
            imgRequest.setShouldCache(false);
        }
        return imgRequest;
    }

    public ImageRequest produceImageRequestWithOutImageView(final String url, final Map<String, String> header, final OnBatchLoadImageListener listener, int maxWidth, int maxHeight,
                                                            final boolean isShouldCache,
                                                            Bitmap.Config decodeConfig) {
        if (maxWidth < 1) {
            maxWidth = 0;
        }
        if (maxHeight < 1) {
            maxHeight = 0;
        }
        final int finalMaxHeight = maxHeight;
        final int finalMaxWidth = maxWidth;
        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                VolleyLogHelper.e("NetClient", "load img use request from server or disk cache");
                if (isShouldCache) {//在ImageCache中也缓存一份，该缓存机制执行lru算法
                    VolleyController.getInstance().getLruBitmapCache().putBitmap(VolleyCacheMgr.getCacheKey(url, finalMaxWidth, finalMaxHeight), response);
                }
                listener.onImageLoadSuccess(url, response);
                listener.onImageLoadFinish(url);
            }
        }, maxWidth, maxHeight, decodeConfig, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onImageLoadFail(url);
                listener.onImageLoadFinish(url);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        };
        if (!isShouldCache) {
            imgRequest.setShouldCache(false);
        }
        return imgRequest;
    }

    /**
     * 生成一个BytesRequest
     *
     * @param method        请求方式{@link com.android.volley.Request.Method},例如{@link com.android.volley.Request.Method#GET}
     * @param url           请求url
     * @param headParams    头参数
     * @param postParams    post请求参数 Map类型<br/>                      如果是get请求，传入null
     * @param listener      成功的回调
     * @param errorListener 失败的回调
     * @return
     */
    public BytesRequest produceBytesRequest(int method, String url, final Map<String, String> headParams, final Map<String, String> postParams,
                                            Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {

        BytesRequest bytesRequest = new BytesRequest(method, url, listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headParams != null && !headParams.isEmpty()) {
                    return headParams;
                }
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return postParams;
            }
        };
        return bytesRequest;
    }

}
