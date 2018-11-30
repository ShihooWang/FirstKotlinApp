package cc.bodyplus.health.mvp.presenter

import cc.bodyplus.health.mvp.contract.VersionContract
import cc.bodyplus.health.mvp.module.AboutInfo

/**
 * Created by rui.gao on 2018-05-15.
 */
class UpdateVersionPresenter :BasePresenter<VersionContract.View>(), VersionContract.Presenter{

    private val updateModel by lazy { UpdateVersionPresenterImpl() }

    override fun requestVersionData(accountInfo : Map<String, String>) {

        checkViewAttached()
        mRootView?.apply {
            showLoading()
        }
        addSubscription(disposable = updateModel.doVersion(accountInfo)
                .subscribe({bean: AboutInfo? ->
                    mRootView?.apply {
                        dismissLoading()
                        setVersionData(bean!!)
                    }},{ throwable: Throwable? ->
                    mRootView?.apply {
                        //处理异常
                        showErrorMsg(throwable.toString())
                    }
                })
        )


    }
}