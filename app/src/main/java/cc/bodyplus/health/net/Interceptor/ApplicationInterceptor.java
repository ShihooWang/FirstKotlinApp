package cc.bodyplus.health.net.Interceptor;

import android.os.SystemClock;

import java.io.IOException;

import cc.bodyplus.health.App;
import cc.bodyplus.health.net.NetBaseConfig;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by shihoo_wang on 2017/3/12.
 * 应用拦截器：
 * 网络请求的固定参数
 * 添加请求头
 * 主要用于查看请求信息及返回信息，如链接地址、头信息、参数信息等，参考下面的log-拦截器定义：
 * <p>
 * 1不需要担心中间过程的响应,如重定向和重试.
 * 2总是只调用一次,即使HTTP响应是从缓存中获取.
 * 3观察应用程序的初衷. 不关心OkHttp注入的头信息如: If-None-Match.
 * 4允许短路而不调用 Chain.proceed(),即中止调用.
 * 5允许重试,使 Chain.proceed()调用多次.
 */

public class ApplicationInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

//        if (!NetUtil.hasNet()) {
//            throw new NetException(2008, App.getAppContext().getString(R.string.analyze_no_net));
//        }

        Request original = chain.request();

        Request.Builder builder = original.newBuilder();

        String author = App.instance.getAuthorization();
        String timeZone = App.instance.getTimeZone();
        String languageType = App.instance.getLanguageType();
        String appVersion = App.instance.getAuthorization();
        if (author == null || author.equals("")) {
            author = "bodyplus";
        }
        builder
                .addHeader("Accept", "application/vnd.LtApi." + NetBaseConfig.SERVER_VERSION + "+json")
                .addHeader("BP-X-HAS_UPLOAD", "2")
                .addHeader("BP-X-LANG", languageType)
                .addHeader("BP-X-ZONE", timeZone)
                .addHeader("BP-X-TYPE", "2")
                .addHeader("BP-X-VERSION", appVersion)
                .addHeader("Authorization", author);

        /**
         * Accept : application/vnd.LtApi.v1+json
         BP-X-HAS_UPLOAD: 1
         BP-X-VERSION: 1.0.0
         Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC8xMjcuMC4wLjFcL2FwcEFwaVwvcHVibGljXC9hcGlcL3VzZXJzIiwiaWF0IjoxNDg5NDU1NzI4LCJleHAiOjE0ODk0NTkzMjgsIm5iZiI6MTQ4OTQ1NTcyOCwianRpIjoiMjIyOTE1OTBhNjZkYmIxYzZjYmZkY2JhNTM2M2MxYWUiLCJzdWIiOjE0fQ.JArQmnsGlGfTbLh9qwmpq91aXJF74g7q48UJgwCwoiE
         */

        Request request = builder.build();
        /**
         * /请求网络，等待响应 耗时的过程
         */
        Response response = chain.proceed(request);
        int code = response.code();
        /**
         * 判断是否请求成功
         */
        try {
            switch (response.code()) {
                case 200: // 成功
                    Headers headers200 = response.headers();
                    String authorization200 = headers200.get("Authorization");
                    App.instance.setAuthorization(authorization200);
                    break;
                case 300: // 请求参数有误
                    Headers headers300 = response.headers();
                    String authorization300 = headers300.get("Authorization");
                    App.instance.setAuthorization(authorization300);
//                    ToastUtil.showToast(R.string.network_response_error_300);

//                    LogUtil.wshLog().i("status = 300   请求参数有误");
                    break;
                case 400: // 请求条件有误
//                    String message = response.message();
//                    String string = response.toString();
//                    String string1 = response.body().toString();
//                    Response response1 = response.networkResponse();
//                    ToastUtil.showToast(R.string.network_response_error_400);
                    refreshToken();
//                    LogUtil.wshLog().i("status = 400  请求条件有误");
                    break;
                case 401: // Authorization认证失败
//                    ToastUtil.showToast(R.string.network_response_error_401);
                    refreshToken();
//                    LogUtil.wshLog().i("status = 401   Authorization认证失败");
                    break;
                case 500: // 服务器内部错误
                    refreshToken();
//                    ToastUtil.showToast(R.string.network_response_error_500);
//                    LogUtil.wshLog().i("status = 500  服务器内部错误 ");
                    break;
            }
        } catch (Exception e) {

        }
        return response;
    }

    private void refreshToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                long timestamp = System.currentTimeMillis() / 1000;
                if (timestamp - App.instance.getLastAuthorizationTime() > 30) {
                    App.instance.setLastAuthorizationTime(timestamp);
                    try {
//                        String userId = UserPrefHelperUtils.getInstance().getUserUid();
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("userId", userId);
//                        jsonObject.put("timestamp", timestamp + "");
//
//                        String json = jsonObject.toString();
//                        String sign = LoginUtil.encryptString(json);
//
//                        Map<String, String> params = new HashMap<>();
//                        params.put("sign", sign);
//                        params.put("timestamp", timestamp + "");
//                        new AuthorizationManger().getAuthorization(NetBaseConfig.AUTHORI_REFRESH, params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
