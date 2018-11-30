package cc.bodyplus.health.mvp.view.report.view

import android.annotation.SuppressLint
import android.os.Build
import android.support.design.widget.TabLayout
import android.widget.LinearLayout
import cc.bodyplus.health.App
import java.lang.reflect.Field


/**
 * Created by rui.gao on 2018-05-02.
 */
object TabLayoutHelper{

    @SuppressLint("ObsoleteSdkInt")
    fun setUpIndicatorWidth(tabLayout: TabLayout) {
        val tabLayoutClass = tabLayout.javaClass
        var tabStrip: Field? = null
        try {
            tabStrip = tabLayoutClass.getDeclaredField("mTabStrip")
            tabStrip!!.isAccessible = true
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

        var layout: LinearLayout? = null
        try {
            if (tabStrip != null) {
                layout = tabStrip.get(tabLayout) as LinearLayout
            }
            for (i in 0 until layout!!.childCount) {
                val child = layout.getChildAt(i)
                child.setPadding(0, 0, 0, 0)
                val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.marginStart = dip2px(20f)!!
                    params.marginEnd = dip2px(20f)!!
                }
                child.layoutParams = params
                child.invalidate()
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    private fun dip2px(dpValue: Float): Int {
        val scale = App.instance.getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }
}