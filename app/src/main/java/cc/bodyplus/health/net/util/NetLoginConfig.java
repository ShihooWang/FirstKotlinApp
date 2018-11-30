package cc.bodyplus.health.net.util;

/**
 * Created by kate.chen on 2017/3/25.
 * chunyan.chen@bodyplus.cc
 */

public interface NetLoginConfig {
    String USER_LOGIN_URL = "user?do=login";
    String SMS_CODE_URL="sms?do=getSmsCode";
    String USER_PROFILE="user?do=updateInfo";
    String USER_LOGIN_AUTO="user?do=autoLogin";
    // 更新头像
    String USER_UPDATE_ACATAR="user?do=updateAvatar";
    String APP_VERSION="system?do=getNewVersion";

    String GET_MY_DOCTOR="user?do=getMyDoctor";
    String REPORT_LIST_DATA="detect?do=reportList";
    String REPORT_DETAILS_DATA="detect?do=getReportInfo";
    String APP_VERSION_UPDATE_URL = "app?do=getNewesVn";

    String UPLOAD_ECG="detect?do=saveRecord";
    String GET_ECG_DATA="detect?do=getEcgData";

    String GET_ECG_RECORD_LIST="detect?do=recordList";


    //获取邮箱验证码
    String EMAIL_SMS_CODE_URL="email?do=sendEmail";
    String EMAIL_FINISH_FORGOT_PW_URL="users?do=resetPasswd";
//    //验证
    String EMAIL_ENTER_SMS_CODE_URL="email?do=codeVerify";
    //注册邮箱
    String EMAIL_USER_REGISTER_URL="users?do=registerEmail";
    //更新邮箱
//    String UPDATE_SMS_MOBILE_URL="users?do=updateEmail";

}
