package com.gerenvip.volley.net.listener;

import org.json.JSONObject;

/**
 * Created by wangwei-ps on 2014/8/29.
 */
public interface INetClientJsonListener extends INetClientBaseListener {
    void onSuccess(JSONObject json, String... msg);
}
