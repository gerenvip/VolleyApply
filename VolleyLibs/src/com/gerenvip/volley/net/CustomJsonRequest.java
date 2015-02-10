package com.gerenvip.volley.net;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 之所以不使用系统的JsonRequest，是因为它的getParames()不生效，导致服务端接收不到参数，请见：<br/>
 * http://stackoverflow.com/questions/19837820/volley-jsonobjectrequest-post-request-not-working <br/>
 * Created by wangwei-ps on 2014/8/29.
 */
public class CustomJsonRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    /**
     * 默认请求方式GET
     *
     * @param url
     * @param params
     * @param responseListener
     * @param errorListener
     */
    public CustomJsonRequest(String url, Map<String, String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }

    public CustomJsonRequest(int method, String url, Map<String, String> params,
                             Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}
