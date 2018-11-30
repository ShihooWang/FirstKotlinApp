package cc.bodyplus.health.net.util;

import android.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Shihu.Wang on 2017/10/12.
 * Email shihu.wang@bodyplus.cc
 */

public class SafeBase64 {
    private static final String ALGORITHM = "HmacSHA256";


    public static String signSafeParam(String data,String key){
        String sign = hmacSign(data,key);
        return sign.replace("+","-").replace("/","_").replace("=",".");
    }

    private static String hmacSign(String data,String key) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secret = new SecretKeySpec(
                    key.getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(secret);
            return Base64.encodeToString(mac.doFinal(data.getBytes()), Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

