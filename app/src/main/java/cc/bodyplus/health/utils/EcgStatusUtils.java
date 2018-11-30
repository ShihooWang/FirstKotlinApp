package cc.bodyplus.health.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shihu.Wang on 2017/12/26.
 * Email shihu.wang@bodyplus.cc
 */

public class EcgStatusUtils {

    private static HashMap<Integer,String> StatusHashMap = new HashMap<>();

    static {
        /*StatusHashMap.put(0,"正常");
        StatusHashMap.put(1,"信号弱");
        StatusHashMap.put(2,"干扰大");
        StatusHashMap.put(3,"室性早搏");
        StatusHashMap.put(4,"短阵室性心动过速");
        StatusHashMap.put(5,"连发室性早搏");
        StatusHashMap.put(6,"单发室性早搏");
        StatusHashMap.put(7,"室性早搏二联律");
        StatusHashMap.put(8,"室性早搏三联律");
        StatusHashMap.put(9,"室上性心动过速");
        StatusHashMap.put(10,"室上性心动过缓");
        StatusHashMap.put(11,"起搏器未俘获");
        StatusHashMap.put(12,"起搏器未起搏");
        StatusHashMap.put(13,"漏搏");
        StatusHashMap.put(14,"窦性心律不齐");
        StatusHashMap.put(15,"ST-T异常");
        StatusHashMap.put(16,"ST-T异常");
        StatusHashMap.put(17,"ST-T异常");
        StatusHashMap.put(18,"房颤");
        StatusHashMap.put(19,"束支传导阻滞");*/

        StatusHashMap.put(0,"正常");
        StatusHashMap.put(1,"信号弱");
        StatusHashMap.put(2,"干扰大");
        StatusHashMap.put(3,"室性早搏");
        StatusHashMap.put(4,"心动过速");
        StatusHashMap.put(5,"室性早搏");
        StatusHashMap.put(6,"室性早搏");
        StatusHashMap.put(7,"室性早搏二联律");
        StatusHashMap.put(8,"室性早搏三联律");
        StatusHashMap.put(9,"室上性心动过速");
        StatusHashMap.put(10,"室上性心动过缓");
        StatusHashMap.put(11,"未见异常");
        StatusHashMap.put(12,"未见异常");
        StatusHashMap.put(13,"漏搏");
        StatusHashMap.put(14,"未见异常");
        StatusHashMap.put(15,"未见异常");
        StatusHashMap.put(16,"未见异常");
        StatusHashMap.put(17,"未见异常");
        StatusHashMap.put(18,"房颤");
        StatusHashMap.put(19,"未见异常");
    }

    public static final String NO_ERROR = "未见异常";
    public static final String ERROR = "异常";

//    public static String generateEcgStatus(ArrayList<Integer> data){
//        if (data == null){
//            return NO_ERROR;
//        }
//        if (data.size() == 20){
//            if (data.get(15)>0 || data.get(16)>0 || data.get(17)>0){
//                data.set(15,0);
//                data.set(16,0);
//                data.set(17,0);
//            }
//        }
//        String[] status = new String[]{"",""};
//        for (int i=1;i<data.size();i++){
//            if (data.get(i) > 0){
//                if (status[0].equals("")){
//                    status[0] = StatusHashMap.get(i);
//                }else {
//                    if (status[1].equals("")){
//                        status[1] = StatusHashMap.get(i);
//                    }
//                }
//            }
//        }
//        if (status[0].equals("")){
//            return NO_ERROR;
//        }else if (status[1].equals("")){
//            return status[0];
//        }else {
//            return status[0] + "," + status[1];
//        }
//    }

    public static String generateEcgStatus(ArrayList<Integer> data){
        if (data == null){
            return NO_ERROR;
        }
        if (data.size() == 20){
            if (data.get(15)>0 || data.get(16)>0 || data.get(17)>0){
                data.set(15,0);
                data.set(16,0);
                data.set(17,0);
            }
        }
        int maxIndex = -1;
        int maxValue = 0;
        for (int i=1;i<data.size();i++){
            int err = data.get(i);
            if (err > maxValue){
                maxIndex = i;
                maxValue = err;
            }
        }
        if (maxIndex > -1){
            return StatusHashMap.get(maxIndex);
        }else {
            return NO_ERROR;
        }

    }
}
