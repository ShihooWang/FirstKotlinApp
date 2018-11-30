package cc.bodyplus.health.utils;

import android.os.Environment;

/**
 * Created by 45108 on 2017/10/5.
 */

public interface Config {

    //本地文件存储总路径名称
    String BODYPLUS_PATH_NAME = "/bodyplus_health";

    //总路径
    String BODYPLUS_MEDICAL_PATH = Environment.getExternalStorageDirectory().toString() + BODYPLUS_PATH_NAME;

    //日志总路径
    String LOG_PATH = BODYPLUS_MEDICAL_PATH + "/log/";

    //crash日志文件名称
    String LOG_NAME = "crash-log.log";

    // 报告
    String DATA_HEART_REPORT = BODYPLUS_MEDICAL_PATH + "/data/heartReport";
    String DATA_SLEEP_REPORT = BODYPLUS_MEDICAL_PATH + "/data/sleepReport";
    String DATA_SLEEP_REPORT_ACC = BODYPLUS_MEDICAL_PATH + "/data/sleepReport/acc";


    // ECG报告png图片地址
    String DATA_ECG_PNG = BODYPLUS_MEDICAL_PATH + "/data/ecg/png";


    // app升级路径
    String UPDATE_PATH_APP = BODYPLUS_MEDICAL_PATH + "/updateApp";

    //剪切后的图片名称
    String CROPPED_IMAGE_NAME = "CropImage.jpg";

    //拍照储存路径
    String CAMERA_IMG_PATH = BODYPLUS_MEDICAL_PATH + "/data/cameraImg";
    //拍照剪切路径
    String CAMERA_CROP_PATH = BODYPLUS_MEDICAL_PATH + "/data/CropImg";

    String CACHE_DATA_PATH = BODYPLUS_MEDICAL_PATH + "/data/cache2";
    //图片压缩路径
    String COMPRESS_IMG_PATH = BODYPLUS_MEDICAL_PATH + "/data/compressImg";
    //头像缓存路径
    String AVATAR_CACHE_IMG_PATH = BODYPLUS_MEDICAL_PATH + "/data/cache_avatar.jpg";

    String HISTORY_TYPE_HEART = "1";
    String HISTORY_TYPE_SLEEP = "2";

    int UPDATE_USER_INFO = 1;
    int UPDATE_REPORT_DETAILS = 2;
    int UPDATE_LOGIN_DATA = 3;
}
