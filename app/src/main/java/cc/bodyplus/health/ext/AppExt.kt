package cc.bodyplus.health.ext

import android.content.Context
import cc.bodyplus.health.App

/**
 * Created by shihoo.wang on 2018/4/19.
 * Email shihu.wang@bodyplus.cc
 * 扩展类
 */

/**
 * 扩展上下文类，用来在程序中方便的获取公有APPComponent对象
 */
fun Context.getAppComponent() = App.instance.appComponent