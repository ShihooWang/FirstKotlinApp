package cc.bodyplus.health.net.service;

import java.util.ArrayList;
import java.util.Map;

import cc.bodyplus.health.mvp.module.AboutInfo;
import cc.bodyplus.health.mvp.module.AvatarBean;
import cc.bodyplus.health.mvp.module.DoctorBean;
import cc.bodyplus.health.mvp.module.SysCodeBean;
import cc.bodyplus.health.mvp.module.UserBean;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by shihoo_wang on 2017/3/12.
 */

public interface LoginApi {

    /**
     * 登录的接口 调用此方法
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<UserBean> login(
            @Url() String url,
            @FieldMap Map<String, String> params);

    /**
     * 登录的接口 调用此方法
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> autoLogin(
            @Url() String url,
            @FieldMap Map<String, String> params);

    /**
     * 登录的接口 调用此方法
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<SysCodeBean> syscode(
            @Url() String url,
            @FieldMap Map<String, String> params);

    /**
     * VERSION调用此方法
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<AboutInfo> doVersion(
            @Url() String url,
            @FieldMap Map<String, String> params);

    /**
     * 医生调用此方法
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<ArrayList<DoctorBean>> doDoctor(
            @Url() String url,
            @FieldMap Map<String, String> params);


    @POST
    @Multipart
    Observable<AvatarBean> uploadUserFileInfo(
            @Url String url,
            @QueryMap Map<String, String> params,
            @Part() MultipartBody.Part parts);
}
