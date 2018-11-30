package cc.bodyplus.health.net.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class LoginUtil {

    /**
     * 加密字符串
     */
    public static String encryptString(String account) {
        MCrypt mCrypt = new MCrypt();
        try {
            return mCrypt.bytesToHex(mCrypt.encrypt(account));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 解密
     *
     * @param encryptString
     * @return
     */
    public static String decryptString(String encryptString) {
        MCrypt mCrypt = new MCrypt();
        try {

            return new String(mCrypt.decrypt(encryptString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证手机号规则
     */
    public static boolean isIDCode(String IDCode) {
        Pattern p = Pattern.compile("([0-9]{4})");
        Matcher m = p.matcher(IDCode);
        return m.matches();
    }

    /**
     * 验证手机号规则
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(14[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

}
