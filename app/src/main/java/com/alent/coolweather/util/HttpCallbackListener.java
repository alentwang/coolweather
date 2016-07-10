package com.alent.coolweather.util;

/**
 * Created by wyl on 2016/7/9.
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
