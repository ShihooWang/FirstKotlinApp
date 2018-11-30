package cc.bodyplus.health.net.exception

/**
 * Created by rui.gao on 2018-05-09.
 */
class ApiException  : RuntimeException {

    private var code: Int? = null


    constructor(throwable: Throwable, code: Int) : super(throwable) {
        this.code = code
    }

    constructor(message: String) : super(Throwable(message))
}