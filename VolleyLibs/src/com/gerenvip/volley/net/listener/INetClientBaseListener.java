package com.gerenvip.volley.net.listener;

/**
 * Created by wangwei-ps on 2014/8/29.
 */
public interface INetClientBaseListener<T> {

    /**
     * 请求成功的回调
     *
     * @param content 服务器返回的指定格式的内容
     * @param msg     附带数据,目前没有使用，该参数可结合业务场景使用
     */
    void onSuccess(T content, Object... msg);

    /**
     * 请求失败
     *
     * @param errorCode 自定义的errorCode,具体定义见:{@link com.gerenvip.volley.net.VolleyErrorHelper}
     * @param msg       错误消息,可以是任何类型，可以根据自己的需要使用<br/>
     *                  note: 注意{@link com.gerenvip.volley.net.NetClient#processError(com.android.volley.VolleyError, INetClientBaseListener)} 中处理该回调时此参数为http协议的ErrorCode
     */
    void onFailure(int errorCode, Object msg);

    /**
     * 请求完成
     */
    void onFinish();
}
