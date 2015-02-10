package com.gerenvip.volley.utils;

import android.text.TextUtils;

import java.util.Map;

/**
 * Created by wangwei-ps
 * Date: 2015/2/9.
 */
public class VolleyUtils {

    /**
     * 拼接url
     *
     * @param url
     * @param extraParams 请求参数
     * @return
     */
    public static String appendUrlParams(String url, Map<String, String> extraParams) {
        if (extraParams == null) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (!url.contains("?")) {
            sb.append('?');
        } else if (!url.endsWith("?") && !url.endsWith("&")) {
            sb.append("&");
        }
        for (Map.Entry<String, String> entry : extraParams.entrySet()) {
            if (TextUtils.isEmpty(entry.getKey()) || TextUtils.isEmpty(entry.getValue())) {
                continue;
            }
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        String tempUrl = sb.toString();
        if (tempUrl.endsWith("&")) {
            tempUrl = tempUrl.substring(0, sb.length() - 1);
        }
        return tempUrl;
    }
}
