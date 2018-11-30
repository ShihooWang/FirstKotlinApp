package cc.bodyplus.health.net.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Fussen on 2017/3/16.
 */

public class UpLoadUtil {


    /**
     * 单文件上传
     *
     * @param file
     * @return
     */
    public static MultipartBody.Part filePart(File file) {

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("application/otcet-stream"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }

    /**
     * 单文件上传
     *
     * @param filePath 文件路径
     * @return
     */
    public static MultipartBody.Part filePart(String filePath) {

        File file = new File(filePath);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("application/otcet-stream"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }


    /**
     * 多文件上传
     *
     * @param files
     * @return
     */
    public static List<MultipartBody.Part> fileParts(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }


    /**
     * 单文件上传方式2 可监督上传进度
     *
     * @param file
     * @return
     */
    public static Map<String, RequestBody> fileParameters(File file) {

        Map<String, RequestBody> requestBodyMap = new HashMap<>();

        UploadFileRequestBody fileRequestBody = new UploadFileRequestBody(file);

        requestBodyMap.put("file\"; filename=\"" + file.getName(), fileRequestBody);

        return requestBodyMap;
    }


}
