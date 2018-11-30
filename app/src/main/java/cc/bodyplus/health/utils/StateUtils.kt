package cc.bodyplus.health.utils

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by rui.gao on 2018-05-04.
 */
class StateUtils {

    companion object {
        const val STATE_0 = "无明显不适"
        const val STATE_1 = "呼吸困难"
        const val STATE_2 = "肩部不适"
        const val STATE_3 = "头晕，黑蒙"
        const val STATE_4 = "恶心，呕吐"
        const val STATE_5 = "上腹痛"
        const val STATE_6 = "心悸，心慌"
        const val STATE_7 = "胸闷，胸痛"
        const val STATE_8 = "大汗"
        private val StatusHashMap = HashMap<Int, String>()
        fun getInstance(): HashMap<Int, String> {
            StatusHashMap.put(0, STATE_0)
            StatusHashMap.put(1, STATE_1)
            StatusHashMap.put(2, STATE_2)
            StatusHashMap.put(3, STATE_3)
            StatusHashMap.put(4, STATE_4)
            StatusHashMap.put(5, STATE_5)
            StatusHashMap.put(6, STATE_6)
            StatusHashMap.put(7, STATE_7)
            StatusHashMap.put(8, STATE_8)
            return StatusHashMap
        }

        fun generateState(list : ArrayList<Int>) : ArrayList<String>{
            getInstance()
            return list.indices
                    .filter { list[it] > 0 }
                    .mapTo(ArrayList<String>()) { StatusHashMap[it]!! }
        }
    }


}