package cc.bodyplus.health.utils;

/**
 * Created by shihoo.wang on 2018/10/26.
 * Email shihu.wang@bodyplus.cc
 */

public class FastClickUtil {
    private static final int MIN_CLICK_DELAY_TIME = 500; //  点击延时750ms
    private static long lastClickTime = 0;


    public static boolean isFastClick(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            return false;
        }
        return true;
    }

}
