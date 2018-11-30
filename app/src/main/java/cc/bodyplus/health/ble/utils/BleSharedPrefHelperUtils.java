package cc.bodyplus.health.ble.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cc.bodyplus.health.App;
import cc.bodyplus.health.ble.bean.DeviceInfo;
import cc.bodyplus.health.ble.manager.BleConnectionManger;


/**
 * Created by Shihu.Wang on 2018/5/25.bean
 * Email shihu.wang@bodyplus.cc
 */

public class BleSharedPrefHelperUtils {

    private SharedPreferences mSharedPre;
    private static BleSharedPrefHelperUtils mInstance;
    private static final String PREF_ID = "bodyplus_health_ble";

    public BleSharedPrefHelperUtils() {
        mSharedPre = App.instance.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
    }

    public static BleSharedPrefHelperUtils getInstance() {
        if (mInstance == null) {
            synchronized (BleSharedPrefHelperUtils.class) {
                if (mInstance == null) {
                    mInstance = new BleSharedPrefHelperUtils();
                }

            }
        }

        return mInstance;
    }


    // 连接的Core的SN号
    private static final String KEY_CORE_SN = "key_core_sn";

    private static final String KEY_CORE_MAC = "key_core_mac";

    private static final String KEY_CORE_HW = "key_core_hw";

    private static final String KEY_CORE_FW = "key_core_fw";


    private String getCoreSn() {
        return mSharedPre.getString(KEY_CORE_SN, "");
    }

    private void setCoreSn(String sn) {
        mSharedPre.edit().putString(KEY_CORE_SN, sn).apply();
    }

    private String getCoreMac() {
        return mSharedPre.getString(KEY_CORE_MAC, "");
    }

    private void setCoreMac(String mac) {
        mSharedPre.edit().putString(KEY_CORE_MAC, mac).apply();
    }

    private String getCoreHw() {
        return mSharedPre.getString(KEY_CORE_HW, "0");
    }

    private void setCoreHw(String sn) {
        mSharedPre.edit().putString(KEY_CORE_HW, sn).apply();
    }

    private String getCoreSw() {
        return mSharedPre.getString(KEY_CORE_FW, "0");
    }

    private void setCoreSw(String sn) {
        mSharedPre.edit().putString(KEY_CORE_FW, sn).apply();
    }

    public void setCoreInfo(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            BleSharedPrefHelperUtils.getInstance().setCoreSn(deviceInfo.getSn());
            BleSharedPrefHelperUtils.getInstance().setCoreMac(deviceInfo.getMac());
            BleSharedPrefHelperUtils.getInstance().setCoreHw(deviceInfo.getHwVn());
            BleSharedPrefHelperUtils.getInstance().setCoreSw(deviceInfo.getSwVn());
        } else {
            BleSharedPrefHelperUtils.getInstance().setCoreSn("");
            BleSharedPrefHelperUtils.getInstance().setCoreMac("");
            BleSharedPrefHelperUtils.getInstance().setCoreHw("");
            BleSharedPrefHelperUtils.getInstance().setCoreSw("");
        }
    }

    public DeviceInfo getCoreInfo() {
        String sn = getCoreSn();
        if (sn == null || sn.length() < 1) {
            return null;
        } else {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setSn(getCoreSn());
            deviceInfo.setMac(getCoreMac());
            deviceInfo.setHwVn(getCoreHw());
            deviceInfo.setSwVn(getCoreSw());
            return deviceInfo;
        }
    }


}
