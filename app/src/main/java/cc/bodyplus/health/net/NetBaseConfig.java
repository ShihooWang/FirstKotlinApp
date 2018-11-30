package cc.bodyplus.health.net;

/**
 * Created by shihoo_wang on 2017/3/12.
 */

public interface NetBaseConfig {

    // 网络环境类型 1.阿里云正式环境 2.阿里云测试环境 3.Dev开发环境
    int NET_TYPE_OFFICIAL = 1;
    int NET_TYPE_TEST = 2;
    int NET_TYPE_DEV = 3;

//    int NET_TYPE = NET_TYPE_OFFICIAL;  // 阿里云正式环境

    int NET_TYPE = NET_TYPE_TEST;  // 阿里云测试环境

//    int NET_TYPE = NET_TYPE_DEV; // Dev开发环境

    // 阿里云正式环境地址
    String ALI_OFFICIAL = "***********";  //v4

    // 阿里云测试环境地址
    String ALI_TEST = "http://test.api.medical2.bodyplus.cc/"; //v5

    // Dev开发环境地址
    String DEV_TEST = "**********";


    String SERVER_VERSION = "v2";

    String AUTHORI_REFRESH = "system?do=refToken";

    String UPLOAD_FILE = "test?do=test";

}
