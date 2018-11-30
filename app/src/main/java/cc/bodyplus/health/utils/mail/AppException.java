package cc.bodyplus.health.utils.mail;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.bodyplus.health.App;
import cc.bodyplus.health.net.util.NetUtil;
import cc.bodyplus.health.utils.Config;
import cc.bodyplus.health.utils.DateUtils;
import cc.bodyplus.health.utils.SharedPrefHelperUtils;


/**
 * Created by Fussen on 2017/4/18.
 */

public class AppException extends Exception implements Thread.UncaughtExceptionHandler {
    public static String LOG_NAME = "crash.log";
    private static AppException mAppException;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private AppException() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    public static AppException startTask() {
        if (mAppException == null) {
            mAppException = new AppException();
        }
        return mAppException;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        throwable.printStackTrace();

        // 收集异常信息 并且发送到服务器
        if (Looper.myLooper() != Looper.getMainLooper()) {// 判断当前线程是否在主线程
            saveErrorLog(throwable);
            handleException(throwable);// 处理发送邮件
        } else {
            final Throwable e = throwable;

            try {
                saveErrorLog(throwable);
                handleException(throwable);// 处理发送邮件
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
        mDefaultHandler.uncaughtException(thread, throwable);
    }

    private boolean handleException(Throwable ex) {
        if (null == ex) {
            return false;
        }
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = null;
        FileOutputStream fos = null;
        String fileName = "";
        try {
            printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);

            sb.append("ERROR: \r\n");
            sb.append("\r\n");
            sb.append("TIME:" + DateUtils.getStringDate("MM-dd kk:mm") + "\r\n");
            sb.append("VERSIOINFO: " + getVersion() + "\r\n");
            sb.append("Android MODEL:" + android.os.Build.MODEL + "\r\n");
            sb.append("Android BRAND:" + android.os.Build.BRAND + "\r\n");
            sb.append("Android PRODUCT:" + android.os.Build.PRODUCT + "\r\n");
            sb.append("Android CPU ABI:" + android.os.Build.CPU_ABI + "\r\n");
            sb.append("Android SDK:" + android.os.Build.VERSION.SDK_INT + "\r\n");
            sb.append("Android VERSION:" + android.os.Build.VERSION.RELEASE + "\r\n");
            sb.append("Android IP:" + NetUtil.getLocalIpAddress()+ "\r\n");
            try {
                String userInfo = "USER  INFO [ " + " UserName:" + SharedPrefHelperUtils.getInstance().getNickname()
                        + ",UserUID:" + SharedPrefHelperUtils.getInstance().getUserId() + " ]\r\n";
                sb.append(userInfo);
                SendEmailUtils.getInstance().setSendText(userInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sb.append("\r\n");
            sb.append("CRASH INFO:");
            sb.append("\r\n");
            sb.append("\r\n");

            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            sb.append(writer.toString());

            // String date = Utils.formatTimeDefault(timestamp);
            fileName = LOG_NAME;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(Config.LOG_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                fos = new FileOutputStream(Config.LOG_PATH + fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
            }
        } catch (Exception e) {
            // LogUtils.i(false, "an error occured while writing file..." +
            // e.getMessage());
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // File mFile = new
        // File(AppException.DIR_LOG+AppException.LOG_NAME);//发送错误日志到服务器
        // if(mFile.exists()){
        // SendEmailUtils.getInstance().sendEmail(AppException.DIR_LOG+AppException.LOG_NAME);
        // }
        return false;
    }


    /**
     * 保存异常日志
     *
     * @param excp
     */
    public void saveErrorLog(Throwable excp) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String errorLog = "errorLog_" + sf.format(new Date()) + ".txt";
        String savePath = "";
        String logFilePath = "";
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            // 判断是否挂载了SD卡
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                savePath = Config.LOG_PATH;
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                logFilePath = savePath + LOG_NAME;
            }
            // 没有挂载SD卡，无法写文件
            if (TextUtils.isEmpty(logFilePath)) {
                return;
            }
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            pw.println("--------------------" + (new Date().toLocaleString()) + "---------------------");
            excp.printStackTrace(pw);
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                }
            }
        }
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = App.instance.getPackageManager();
            PackageInfo info = manager.getPackageInfo(App.instance.getPackageName(), 0);
            String version = "Version Name : " + info.versionName;
            String verInfo = "    Version Code : " + info.versionCode;
            return version + verInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return "未获取到版本信息";
        }
    }

}
