package cc.bodyplus.health.net.util;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MCrypt {
    private String iv = "f3e00ec14c4cecba";//虚拟的 iv (需更改)
    private String secretKey = "f3e00ec14c4cecba";//虚拟的 密钥 (需更改）
    private static final String ALGORITHM = "AES/CBC/PKCS7Padding";

    public byte[] encrypt(String code) {
        byte[] keyBytes = new byte[0];
        try {
            keyBytes= fillKeyByte(secretKey);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = cipher.doFinal(code.getBytes());

            return encrypted;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyBytes;
    }



    /**
     *   byte数组只有16位，需要补充为32位
     */

    private byte[] fillKeyByte(String secretKey) {

        byte[] temp = new byte[32];
        byte[] keyByte = secretKey.getBytes();
        Arrays.fill(temp, (byte) 0);
        System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
        keyByte = temp;
        return keyByte;
    }


    /**
     * 解密
     *
     * @param code
     * @return
     * @throws Exception
     */
    public byte[] decrypt(String code) throws Exception {
        byte[] keyBytes = new byte[0];
        try {

            keyBytes= fillKeyByte(secretKey);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = cipher.doFinal(hexToBytes(code));
            return encrypted;//new String(encrypted,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyBytes;
    }


    /**
     * 加密
     *
     * @param data
     * @return
     */
    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }

        int len = data.length;
        String str = "";
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16)
                str = str + "0" + Integer.toHexString(data[i] & 0xFF);
            else
                str = str + Integer.toHexString(data[i] & 0xFF);
        }
        return str;
    }


    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }


    private static String padString(String source) {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }

}
