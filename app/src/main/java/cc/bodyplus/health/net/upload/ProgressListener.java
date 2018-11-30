package cc.bodyplus.health.net.upload;

public interface ProgressListener {

    /**
     *
     * @param hasWrittenLen 已上传
     * @param totalLen 文件总大小
     * @param hasFinish 是否完成上传
     */
        void onProgress(long hasWrittenLen, long totalLen, boolean hasFinish);
    }
