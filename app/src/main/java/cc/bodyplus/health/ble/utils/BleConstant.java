package cc.bodyplus.health.ble.utils;


import android.os.Environment;

/**
 * Created by shihu.wang on 2017/3/17.
 * Email shihu.wang@bodyplus.cc
 */
public interface BleConstant {

    String BODYPLUS_BLE_PATH = Environment.getExternalStorageDirectory().toString() + "/bodyplus_health/ble";

   //升级路径
    String UPDATE_PATH = BODYPLUS_BLE_PATH + "/update";

   //升级路径
    String UPDATE_STM32_PATH = UPDATE_PATH + "/stm32";

    String BLE_LOG_PATH = BODYPLUS_BLE_PATH + "/log";

    String BLE_LOG_SLEEP_PATH = BLE_LOG_PATH + "/sleep";

    String BLE_LOG_ECG_PATH = BLE_LOG_PATH + "/ecg";
}
