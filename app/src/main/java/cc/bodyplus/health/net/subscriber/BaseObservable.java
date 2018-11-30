package cc.bodyplus.health.net.subscriber;


import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by shihoo_wang on 2017/3/11.
 */

class BaseObservable<T> extends Observable<T> {


    @Override
    protected void subscribeActual(Observer<? super T> observer) {

    }
}
