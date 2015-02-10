package com.gerenvip.volley.net.listener;

/**
 * string 类型的网络请求回调
 * Created by wangwei-ps on 2014/8/29.
 */
public interface INetClientListener extends INetClientBaseListener<String> {

    /**
     * 请求成功的回调
     *
     * @param content 服务器返回的String格式的内容
     * @param msg     附带数据,目前没有使用，该参数可结合业务场景使用
     */
    void onSuccess(String content, Object... msg);
}
