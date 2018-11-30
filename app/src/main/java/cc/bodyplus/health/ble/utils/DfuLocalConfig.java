package cc.bodyplus.health.ble.utils;

import java.util.HashMap;

/**
 * Created by Shihu.Wang on 2017/12/27.
 * Email shihu.wang@bodyplus.cc
 */

public class DfuLocalConfig {

    private static String HW_102_FILE_PATH = "M2_0103_0038.zip";
    private static String HW_103_FILE_PATH = "M2_0103_0038.zip";
    private static String HW_131_FILE_PATH = "M2_0131_0038.zip";

    private static int HW_102_NEW_SW = 56; // 0.37
    private static int HW_103_NEW_SW = 56; // 0.37
    private static int HW_131_NEW_SW = 56; // 0.37

    private static int HW_102 = 258; // 1.02
    private static int HW_103 = 259; // 1.03
    private static int HW_131 = 305; // 1.31

    public static String DFU_MEG = "core固件版本0.38\n"+"1.bug修复";

    private static HashMap<Integer,Integer> SW_NEW_VERSION = new HashMap<>();

    private static HashMap<Integer,String> SW_NEW_FILE = new HashMap<>();

    static {
        SW_NEW_VERSION.put(HW_103,HW_103_NEW_SW);
        SW_NEW_VERSION.put(HW_102,HW_102_NEW_SW);
        SW_NEW_VERSION.put(HW_131,HW_131_NEW_SW);

        SW_NEW_FILE.put(HW_103,HW_103_FILE_PATH);
        SW_NEW_FILE.put(HW_102,HW_102_FILE_PATH);
        SW_NEW_FILE.put(HW_131,HW_131_FILE_PATH);
    }

    public static String checkUpdateDfu(int hw, int sw){
        int byHwSw = getNewSwVersionByHw(hw);
        if (byHwSw > sw){
            return getNewSwFilePathByHw(hw);
        }
        return null;
    }


    private static int getNewSwVersionByHw(int hw){
        Integer integer = SW_NEW_VERSION.get(hw);
        if (integer==null){
            return 0;
        }else {
            return integer;
        }
    }

    public static String getNewSwFilePathByHw(int hw){
        String s = SW_NEW_FILE.get(hw);
        if (s==null){
            return "";
        }else {
            return s;
        }
    }
}
