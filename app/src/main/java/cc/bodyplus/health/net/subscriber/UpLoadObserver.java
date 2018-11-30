package cc.bodyplus.health.net.subscriber;

import cc.bodyplus.health.net.callback.RequestCallBack;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Fussen on 2017/3/16.
 */

public class UpLoadObserver<T> extends BaseObserver<ResponseBody> {


    private RequestCallBack<T> callBack;

    public UpLoadObserver(RequestCallBack<T> callBack) {

        this.callBack = callBack;
    }



    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    public void onNext(ResponseBody value) {
        super.onNext(value);

        if (callBack!=null){
            callBack.onSuccess((T) value);
        }
    }


    @Override
    public void onComplete() {
        super.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }
}
