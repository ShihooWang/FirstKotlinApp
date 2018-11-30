package cc.bodyplus.health.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cc.bodyplus.health.App;
import cc.bodyplus.health.ble.bean.DeviceInfo;
import cc.bodyplus.health.ble.manager.BleConnectionManger;


/**
 * Created by Shihu.Wang on 2017/11/7.bean
 * Email shihu.wang@bodyplus.cc
 */

public class SharedPrefHelperUtils {

    private SharedPreferences mSharedPre;
    private static SharedPrefHelperUtils mInstance;
    private static final String PREF_ID = "bodyplus_health";

    public SharedPrefHelperUtils() {
        mSharedPre = App.instance.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
    }

    public static SharedPrefHelperUtils getInstance() {
        if (mInstance == null) {
            synchronized (SharedPrefHelperUtils.class) {
                if (mInstance == null) {
                    mInstance = new SharedPrefHelperUtils();
                }

            }
        }

        return mInstance;
    }

    // 性别
    private static final String KEY_USER_NICKNAME = "key_user_nickname";

    // 性别
    private static final String KEY_USER_GENDER = "key_user_gender";

    // 年龄
    private static final String KEY_BODY_AGE = "key_body_age";

    private static final String KEY_USER_ID = "userId";

    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String KEY_BP_UID = "bp_uid";

    // 网络请求头
    private static final String NET_WORK_TOKEN = "net_work_token";

    //用户ID
    private static final String KEY_USER_UID = "key_user_uid";

    private static final String KEY_MOBILE = "key_mobile";
    private static final String KEY_IS_BETA = "key_is_beta";
    private static final String KEY_NICKNAME = "key_nickname";
    private static final String KEY_AVATAR = "key_avatar";
    private static final String KEY_GENDER = "key_gender";   //1为男，2为女
    private static final String KEY_AGE = "key_age";
    private static final String KEY_IS_PERFECT = "key_is_perfect"; //是否注册过
    private static final String KEY_TOKEN = "key_token";

    private static final String KEY_AUTO_LOGIN = "key_auto_login";

    public void setMobile(String mobile) {
        mSharedPre.edit().putString(KEY_MOBILE, mobile).commit();
    }

    public String getMobile() {
        return mSharedPre.getString(KEY_MOBILE, "");
    }

    public void setIsBeta(String IsBeta) {
        mSharedPre.edit().putString(KEY_IS_BETA, IsBeta).commit();
    }

    public String getIsBeta() {
        return mSharedPre.getString(KEY_IS_BETA, "");
    }

    public void setNickname(String nickname) {
        mSharedPre.edit().putString(KEY_NICKNAME, nickname).commit();
    }

    public String getNickname() {
        return mSharedPre.getString(KEY_NICKNAME, "");
    }

    public void setGender(String gender) {
        mSharedPre.edit().putString(KEY_GENDER, gender).commit();
    }

    public String getGender() {
        return mSharedPre.getString(KEY_GENDER, "");
    }

    public void setAvatar(String avatar) {
        mSharedPre.edit().putString(KEY_AVATAR, avatar).commit();
    }

    public String getAvatar() {
        return mSharedPre.getString(KEY_AVATAR, "");
    }

    public void setAge(String age) {
        mSharedPre.edit().putString(KEY_AGE, age).commit();
    }

    public String getAge() {
        return mSharedPre.getString(KEY_AGE, "");
    }

    public void setIsPerfect(String isPerfect) {
        mSharedPre.edit().putString(KEY_IS_PERFECT, isPerfect).commit();
    }

    public String getIsPerfect() {
        return mSharedPre.getString(KEY_IS_PERFECT, "");
    }


    public void setAutoLogin(Boolean autoLogin) {
        mSharedPre.edit().putBoolean(KEY_AUTO_LOGIN, autoLogin).commit();
    }

    public boolean getAutoLogin() {
        return mSharedPre.getBoolean(KEY_AUTO_LOGIN, false);
    }


    public String getNetWorkToken() {
        return mSharedPre.getString(NET_WORK_TOKEN, "");
    }

    public void setNetWorkToken(String remark) {
        mSharedPre.edit().putString(NET_WORK_TOKEN, remark).apply();
    }


    /**
     * 更加开闭原则 保证用户信息同步更新
     */
//    public void setUser(UserBean user) {

//        setUserGender(String.valueOf(user.gender));
//        setUserAge(String.valueOf(user.age));
//        setNickname(String.valueOf(user.nickName));
//
//        setUserUid(String.valueOf(user.userId));
//        setIsPerfect(String.valueOf(user.isPerfect));
//        setIsBeta(String.valueOf(user.isBeta));
//        setMobile(String.valueOf(user.mobile));
//        setAvatar(String.valueOf(user.avatar));

//    }


//    public UserBean getUser() {
//        UserBean bean = new UserBean();
//        bean.nickName = getUserNickName();
//        bean.age = getUserAge();
//        bean.gender = getUserGender();
//        bean.userId = getUserUid();
//        bean.isPerfect = getIsPerfect();
//        bean.isBeta = getIsBeta();
//        bean.mobile = getMobile();
//        bean.avatar = getAvatar();
//        return bean;
//    }


    private void setUserNickName(String name) {
        mSharedPre.edit().putString(KEY_USER_NICKNAME, name).commit();
    }

    private String getUserNickName() {
        return mSharedPre.getString(KEY_USER_NICKNAME, "");
    }

    private void setUserGender(String gender) {
        mSharedPre.edit().putString(KEY_USER_GENDER, gender).commit();
    }

    private String getUserGender() {
        return mSharedPre.getString(KEY_USER_GENDER, "1");
    }


    private void setUserAge(String gender) {
        mSharedPre.edit().putString(KEY_BODY_AGE, gender).commit();
    }

    private String getUserAge() {
        return mSharedPre.getString(KEY_BODY_AGE, "");
    }


    public void setUserId(String userId) {
        mSharedPre.edit().putString(KEY_USER_ID, userId).commit();
    }

    public String getUserId() {
        return mSharedPre.getString(KEY_USER_ID, "-1");
    }


    public void setAccessToken(String accessToken) {
        mSharedPre.edit().putString(KEY_ACCESS_TOKEN, accessToken).commit();
    }

    public String getAccessToken() {
        return mSharedPre.getString(KEY_ACCESS_TOKEN, "");
    }

    public void setBpuid(String bp_uid) {
        mSharedPre.edit().putString(KEY_BP_UID, bp_uid).commit();
    }

    public String getBpuid() {
        return mSharedPre.getString(KEY_BP_UID, "");
    }


    public void clearAll() {
        mSharedPre.edit().clear().commit();
    }

    public void clearUserProfile() {
        setAutoLogin(false);
        setUserId("");
        setNetWorkToken("");
        setMobile("");
        setIsBeta("");
        setNickname("");
        setAvatar("");
        setGender("");
        setAge("");
        setIsPerfect("");
    }

}
