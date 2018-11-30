package cc.bodyplus.health.net.download;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import cc.bodyplus.health.utils.Config;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadManager {
    private static final String BODYPLUS_PATH = Config.UPDATE_PATH_APP ;
    private static final AtomicReference<DownloadManager> INSTANCE = new AtomicReference<>();
    private HashMap<String, Call> downCalls;//用来存放各个下载的请求
    private OkHttpClient mClient;//OKHttpClient;
    private String filePath;

    //获得一个单例类
    public static DownloadManager getInstance() {
        for (; ; ) {
            DownloadManager current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new DownloadManager();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    private DownloadManager() {
        downCalls = new HashMap<>();
        mClient = new OkHttpClient.Builder().build();
    }

    /**
     * 开始下载
     *
     * @param url              下载请求的网址
     * @param downLoadObserver 用来回调的接口
     */
    public void download(String url, DownLoadObserver downLoadObserver) {
        filePath = "";
        Observable.just(url)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !downCalls.containsKey(s);
                    }
                })
                .flatMap(new Function<String, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(String s) throws Exception {
                        return Observable.just(createDownInfo(s));
                    }
                })
                .map(new Function<DownloadInfo, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(DownloadInfo downloadInfo) throws Exception {
                        return getRealFileName(downloadInfo);
                    }
                })
                .flatMap(new Function<DownloadInfo, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(DownloadInfo downloadInfo) throws Exception {
                        return Observable.create(new DownloadSubscribe(downloadInfo));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(downLoadObserver);//添加观察者

    }

    /**
     * 开始下载
     *
     * @param url              下载请求的网址
     * @param filePath         下载保存地址路径
     * @param downLoadObserver 用来回调的接口
     */
    public void download(String url, String filePath, DownLoadObserver downLoadObserver) {
        this.filePath = filePath;

        Observable.just(url)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !downCalls.containsKey(s);
                    }
                })
                .flatMap(new Function<String, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(String s) throws Exception {
                        return Observable.just(createDownInfo(s));
                    }
                })
                .map(new Function<DownloadInfo, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(DownloadInfo downloadInfo) throws Exception {
                        return getRealFileName(downloadInfo);
                    }
                })
                .flatMap(new Function<DownloadInfo, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(DownloadInfo downloadInfo) throws Exception {
                        return Observable.create(new DownloadSubscribe(downloadInfo));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(downLoadObserver);//添加观察者

    }


    /**
     * 开始下载
     *
     * @param url              下载请求的网址
     * @param filePath         下载保存地址路径
     * @param fileName         文件名
     * @param downLoadObserver 用来回调的接口
     */
    public void download(String url, String filePath, final String fileName, DownLoadObserver downLoadObserver) {
        this.filePath = filePath;

        Observable.just(url)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !downCalls.containsKey(s);
                    }
                })
                .flatMap(new Function<String, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(String s) throws Exception {
                        return Observable.just(createDownInfo(s, fileName));
                    }
                })
                .map(new Function<DownloadInfo, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(DownloadInfo downloadInfo) throws Exception {
                        return getRealFileName(downloadInfo);
                    }
                })
                .flatMap(new Function<DownloadInfo, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(DownloadInfo downloadInfo) throws Exception {
                        return Observable.create(new DownloadSubscribe(downloadInfo));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(downLoadObserver);//添加观察者

    }

    /**
     * @param url              下载url地址
     * @param filePath         下载保存地址路径
     * @param isMainThread     下载完成是否在主线程
     * @param downLoadObserver 观察者
     */
    public void download(String url, String filePath, boolean isMainThread, DownLoadObserver downLoadObserver) {
        this.filePath = filePath;
        Scheduler scheduler;

        if (isMainThread) {
            scheduler = AndroidSchedulers.mainThread();
        } else {
            scheduler = Schedulers.io();
        }

        Observable.just(url)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !downCalls.containsKey(s);
                    }
                })
                .flatMap(new Function<String, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(String s) throws Exception {
                        return Observable.just(createDownInfo(s));
                    }
                })
                .map(new Function<DownloadInfo, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(DownloadInfo downloadInfo) throws Exception {
                        return getRealFileName(downloadInfo);
                    }
                })
                .flatMap(new Function<DownloadInfo, ObservableSource<DownloadInfo>>() {
                    @Override
                    public ObservableSource<DownloadInfo> apply(DownloadInfo downloadInfo) throws Exception {
                        return Observable.create(new DownloadSubscribe(downloadInfo));
                    }
                })
                .observeOn(scheduler)//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(downLoadObserver);//添加观察者

    }


    public void cancel(String url) {
        Call call = downCalls.get(url);
        if (call != null) {
            call.cancel();//取消
        }
        downCalls.remove(url);
    }

    /**
     * 创建DownInfo
     *
     * @param url 请求网址
     * @return DownInfo
     */
    private DownloadInfo createDownInfo(String url) {

        DownloadInfo downloadInfo = new DownloadInfo(url);
        long contentLength = getContentLength(url);//获得文件大小
        downloadInfo.setTotal(contentLength);
        String fileName = url.substring(url.lastIndexOf("/"));
        downloadInfo.setFileName(fileName);
        return downloadInfo;
    }

    /**
     * 创建DownInfo
     * 设置文件名
     *
     * @param url 请求网址
     * @return DownInfo
     */
    private DownloadInfo createDownInfo(String url, String fileName) {
        DownloadInfo downloadInfo = new DownloadInfo(url);
        long contentLength = getContentLength(url);//获得文件大小
        downloadInfo.setTotal(contentLength);
        downloadInfo.setFileName(fileName);
        return downloadInfo;
    }


    private DownloadInfo getRealFileName(DownloadInfo downloadInfo) {
        String fileName = downloadInfo.getFileName();
        long downloadLength = 0, contentLength = downloadInfo.getTotal();
        File FilePath;
        if (filePath.equalsIgnoreCase("")) {
            FilePath = new File(BODYPLUS_PATH + "/");
            if (!FilePath.exists()) {
                FilePath.mkdirs();
            }
        } else {
            FilePath = new File(filePath);
            if (!FilePath.exists()) {
                FilePath.mkdirs();
            }
        }

        File file = new File(FilePath, fileName);
        if (file.exists()) {
            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length();
            if (contentLength > downloadLength) {
                //设置改变过的文件名/大小
                downloadInfo.setProgress(downloadLength);
                downloadInfo.setFileName(file.getName());
            } else {
                downloadInfo.setIsFile(true);
                downloadInfo.setProgress(downloadLength);
                downloadInfo.setFileName(file.getName());
            }
        } else {
            downloadInfo.setProgress(downloadLength);
            downloadInfo.setFileName(file.getName());
        }

//        //之前下载过,需要重新来一个文件
//        int i = 1;
//        while (downloadLength >= contentLength) {
//            int dotIndex = fileName.lastIndexOf(".");
//            String fileNameOther;
//            if (dotIndex == -1) {
//                fileNameOther = fileName + "(" + i + ")";
//            } else {
//                fileNameOther = fileName.substring(0, dotIndex)
//                        + "(" + i + ")" + fileName.substring(dotIndex);
//            }
//            File newFile = new File(FilePath, fileNameOther);
//            file = newFile;
//            downloadLength = newFile.length();
//            i++;
//        }
//        //设置改变过的文件名/大小
//        downloadInfo.setProgress(downloadLength);
//        downloadInfo.setFileName(file.getName());
        return downloadInfo;
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<DownloadInfo> {
        private DownloadInfo downloadInfo;

        public DownloadSubscribe(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void subscribe(ObservableEmitter<DownloadInfo> e) throws Exception {
            String url = downloadInfo.getUrl();
            long downloadLength = downloadInfo.getProgress();//已经下载好的长度
            long contentLength = downloadInfo.getTotal();//文件的总长度
            boolean isFile = downloadInfo.isFile();
            if (isFile) {
                //初始进度信息
                e.onNext(downloadInfo);
                e.onComplete();
                return;
            }
            //初始进度信息
            e.onNext(downloadInfo);

            Request request = new Request.Builder()
                    //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .url(url)
                    .build();
            Call call = mClient.newCall(request);
            downCalls.put(url, call);//把这个添加到call里,方便取消
            Response response = call.execute();
            File FilePath;
            if (filePath.equalsIgnoreCase("")) {
                FilePath = new File(BODYPLUS_PATH + "/");
                if (!FilePath.exists()) {
                    FilePath.mkdirs();
                }
            } else {
                FilePath = new File(filePath);
                if (!FilePath.exists()) {
                    FilePath.mkdirs();
                }
            }
            File file = new File(FilePath, downloadInfo.getFileName());
            InputStream is = null;
            FileOutputStream fileOutputStream = null;
            try {
                is = response.body().byteStream();
                fileOutputStream = new FileOutputStream(file, true);
                byte[] buffer = new byte[4096];//缓冲数组2kB  4096  2048
                int len;
                while (is != null && (len = is.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    downloadLength += len;
                    downloadInfo.setProgress(downloadLength);
                    e.onNext(downloadInfo);
                }
                fileOutputStream.flush();
                downCalls.remove(url);
                e.onComplete();//完成
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                //关闭IO流
                IOUtil.closeAll(is, fileOutputStream);
            }

        }
    }

    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        try {
            Response response = mClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? DownloadInfo.TOTAL_ERROR : contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DownloadInfo.TOTAL_ERROR;
    }


}
