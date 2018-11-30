package cc.bodyplus.health.net.subscriber;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Fussen on 2017/3/16.
 */

public class BaseObserver<T> implements Observer<T> {


    public BaseObserver(){

    }
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T value) {

    }


    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
