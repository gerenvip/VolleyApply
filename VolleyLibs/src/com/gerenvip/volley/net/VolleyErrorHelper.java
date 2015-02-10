package com.gerenvip.volley.net;

import com.android.volley.*;

public class VolleyErrorHelper {

    /**
     * 超时
     */
    public static final int ERROR_TIME_OUT = 430;
    /**
     * 网络异常
     */
    public static final int ERROR_NET_WORK = 431;
    /**
     * 认证失败
     */
    public static final int ERROR_AUTH_FAILURE = 432;

    /**
     * 服务器的响应不能被解析
     */
    public static final int ERROR_PARSE = 433;

    /**
     * 服务器端错误
     */
    public static final int ERROR_SERVER = 434;

    /**
     * 其他非Volley错误，基本上没有可能性
     */
    public static final int ERROR_OTHERS = 435;

    public static int getErrorMessage(Object error) {
        if (error instanceof TimeoutError) {
            return ERROR_TIME_OUT;
        } else if (error instanceof ServerError) {
            return ERROR_SERVER;
        } else if (error instanceof AuthFailureError) {
            return ERROR_AUTH_FAILURE;
        } else if (error instanceof NetworkError || error instanceof NoConnectionError) {
            return ERROR_NET_WORK;
        } else if (error instanceof ParseError) {
            return ERROR_PARSE;
        }
        return ERROR_OTHERS;
    }
}
