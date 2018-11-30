package cc.bodyplus.health.net.callback;

/**
 * Created by shihoo_wang on 2017/3/11.
 * 给页面的回调 T泛型代表封装的数据类型
 */

public interface ResponseCallBack<T> {
    void onStart();

    void onCompleted();

    void onError(Throwable e);

    void onSuccee(T response);
}
