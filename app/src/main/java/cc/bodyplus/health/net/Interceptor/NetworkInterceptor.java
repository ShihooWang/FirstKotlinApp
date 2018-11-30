package cc.bodyplus.health.net.Interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * Created by shihoo_wang on 2017/3/12.
 *
 * 网络拦截器
 *可以添加、删除或替换请求头信息，还可以改变的请求携带的实体。
 * 例如，如果你连接到一个支持压缩的网络服务器你可以使用一个应用拦截器来添加请求实体压缩。
 *
 *  1能够操作中间过程的响应,如重定向和重试.
    2当网络短路而返回缓存响应时不被调用.
    3只观察在网络上传输的数据.
    4携带请求来访问连接.
 */

public class NetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
            return chain.proceed(originalRequest);
        }


        Request compressedRequest = originalRequest.newBuilder()
                .header("Content-Encoding", "gzip")
                .method(originalRequest.method(), gzip(originalRequest.body()))
                .build();

        return chain.proceed(compressedRequest);

    }

    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }


            @Override
            public long contentLength() {
                return -1; // We don't know the compressed length in advance!
            }


            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}
