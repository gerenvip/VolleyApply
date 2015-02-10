package com.gerenvip.volley.net.listener;

/**
 * 返回值为字节数组的回调
 * Created by wangwei-ps on 2014/9/12.
 */
public interface INetClientBytesListener extends INetClientBaseListener<byte[]> {
    /**
     * 请求成功的回调
     *
     * @param content 服务器返回的String格式的内容
     * @param msg     附带数据,目前没有使用，该参数可结合业务场景使用
     */
    void onSuccess(byte[] content, Object... msg);
}
