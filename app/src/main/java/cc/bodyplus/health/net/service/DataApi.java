package cc.bodyplus.health.net.service;

import java.util.ArrayList;
import java.util.Map;

import cc.bodyplus.health.mvp.module.EcgDataBean;
import cc.bodyplus.health.mvp.module.EcgRecordList;
import cc.bodyplus.health.mvp.module.RecordEcgListBean;
import cc.bodyplus.health.mvp.module.ReportInfo;
import cc.bodyplus.health.mvp.module.ReportInfo2;
import cc.bodyplus.health.mvp.module.UploadResultBean;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by rui.gao on 2018-05-17.
 */

public interface DataApi {

    /**
     * 报告详情
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<ReportInfo2> doReportDetailsData(
            @Url() String url,
            @FieldMap Map<String, String> params);

    /**
     * 报告列表
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> doReportListData(
            @Url() String url,
            @FieldMap Map<String, String> params);

    /**
     * 心电数据
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<EcgDataBean> doEcgData(
            @Url() String url,
            @FieldMap Map<String, String> params);

    /**
     * 上传数据
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<UploadResultBean> doUploadEcgData(
            @Url() String url,
            @FieldMap Map<String, String> params);


    /**
     * 用户提交一分钟心电诊断数据
     * @return
     */
    @POST()
    @FormUrlEncoded
    Observable<EcgRecordList> doRecordList(
            @Url() String url,
            @FieldMap Map<String, String> params);
}
