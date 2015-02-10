package com.gerenvip.volley.net;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * 返回类型为byte[] 类型的Request
 * Created by wangwei-ps on 2014/9/12.
 */
public class BytesRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link com.android.volley.Request.Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public BytesRequest(int method, String url, Response.Listener<byte[]> listener,
                        Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public BytesRequest(String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        byte[] data = response.data;
        return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }
}
