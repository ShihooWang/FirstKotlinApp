package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.App
import cc.bodyplus.health.mvp.module.AboutInfo
import cc.bodyplus.health.net.util.NetLoginConfig
import cc.bodyplus.health.net.util.RxjavaHelperUtil
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * Created by rui.gao on 2018-05-15.
 */
class UpdateVersionPresenterImpl {
    fun doVersion(accountInfo: Map<String, String>): Observable<AboutInfo> {
        return  App.instance.appComponent.getLoginApi().doVersion(NetLoginConfig.APP_VERSION, accountInfo).compose(RxjavaHelperUtil.schedulersTransformer() as ObservableTransformer<AboutInfo, AboutInfo>)
    }
}