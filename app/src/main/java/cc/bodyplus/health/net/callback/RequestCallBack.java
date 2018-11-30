package cc.bodyplus.health.net.callback;

/**
 * Created by shihoo_wang on 2017/3/12.
 *
 * 请求的回调
 */

public interface RequestCallBack<T> {
    void onStart();//请求前

    void onSuccess(T data); //请求成功

    void onError(String errorMsg); //请求失败

    void onCompleted();//请求完成
}
