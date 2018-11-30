package cc.bodyplus.health.net.subscriber;

import cc.bodyplus.health.net.callback.ResponseCallBack;
import okhttp3.ResponseBody;


/**
 * Created by shihoo_wang on 2017/3/11.
 */

/**
 * 服务器返回统一格式处理
 * 增加回调给用户的接口 即回调给View层
 */
public class NetSubscriber<T> extends BaseObservable<ResponseBody> {

    private ResponseCallBack<T> callBack;

    public NetSubscriber(ResponseCallBack<T> callBack) {
        this.callBack = callBack;
    }

}
