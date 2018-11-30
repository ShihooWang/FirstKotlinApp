package cc.bodyplus.health.utils;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

/**
 * Created by shihu.wang on 2017/3/15.
 * Email shihu.wang@bodyplus.cc
 */

public class ApkHelper {

    private ApkHelper() {
    }

    /**
     * 获取当前运行进程名
     * @param cxt
     * @param pid 进程ID
     * @return
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 安装APK。
     *
     * @param path
     *            apk文件的绝对路径
     */
    public static void install(Context context, String path) {
        final Intent intent = generateInstallIntent(context, path);
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 获取apk文件中的版本号
     *
     * @param context
     * @param path
     * @return
     */
    public static int getApkVersionCode(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES);
        int version = -1;
        version = info.versionCode;
        return version;
    }

    /**
     * 获取应用版本号。
     */
    public static String getVersion(Context context) {
        try {
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo info = pm.getPackageInfo(context.getPackageName(),
                        0);
                return info.versionName;
            } else {
                return "获取版本号失败";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "获取版本号失败";
        }
    }

    /**
     * 获取应用的版本号。（内部识别号）
     *
     * @param packageName
     *            相关应用的包名
     */
    public static int getVersionCode(Context context, String packageName) {
        int result;
        try {
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo info = context.getPackageManager().getPackageInfo(
                        packageName, 0);
                result = info.versionCode;
            } else {
                result = -1;
            }
        } catch (PackageManager.NameNotFoundException e) {
            result = -1;
        }
        return result;

    }

    /**
     * 获取应用的版本号。（内部识别号）
     * 相关应用的包名
     */
    public static int getVersionCode(Context context) {
        int result;
        try {
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0);
                result = info.versionCode;
            } else {
                result = -1;
            }
        } catch (PackageManager.NameNotFoundException e) {
            result = -1;
        }
        return result;

    }

    /**
     * 静默安装APK。
     *
     * @param pkg
     *            待安装APK的包名
     * @param path
     *            APK文件的绝对路径
     */
	/*
	 * @SuppressWarnings("UnusedDeclaration") public static boolean
	 * installSilence(Context context, String pkg, String path) { boolean result
	 * = false;
	 *
	 * if (LDRootManager.isObtainedRootPermission()) { LDCommandBuilder builder
	 * = new LDCommandBuilder(); final String mark =
	 * String.format("gamecenter:package %s install complete", pkg);
	 * builder.addSilenceInstallCommand(pkg, path, mark);
	 * LDRootManager.doCommand(builder.build());
	 *
	 * String line; while ((line = LDRootManager.readCommand()) != null) { if
	 * (line.contains(mark)) { if (LDApkHelper.isInstalled(pkg)) { result =
	 * true; } break; } } }
	 *
	 * return result; }
	 */

    /**
     * 卸载应用。
     *
     * @param pkg
     *            待卸载应用的相关包名
     */
    public static void uninstall(Context context, String pkg) {
        if (context != null && !TextUtils.isEmpty(pkg)) {
            final Uri uri = Uri.parse("package:" + pkg);
            final Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 判断应用是否已经安装。
     *
     * @param pkg
     *            应用相关的包名
     */
    public static boolean isInstalled(Context context, String pkg) {
        boolean result = false;
        if (!TextUtils.isEmpty(pkg)) {
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                try {
                    final PackageInfo info = pm.getPackageInfo(pkg, 0);
                    if (info != null) {
                        result = true;
                    }
                } catch (Exception e) {
                    result = false;
                }
            }

        }
        return result;
    }

    /**
     * 获取App安装路径，假如App尚未安装将返回空
     *
     * @param packageName
     *            App包名
     */
    @SuppressWarnings("EmptyCatchBlock")
    public static String getAppInstallPath(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }

        final PackageManager pm = context.getPackageManager();
        try {
            if (pm != null) {
                final ApplicationInfo ai = pm
                        .getApplicationInfo(packageName, 0);
                return ai.sourceDir;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        return null;
    }

    /**
     * 启动已安装应用，未安装将不执行任何操作。
     *
     * @param pkg
     *            待启动的应用相关的包名
     */
    public static void launch(Context context, String pkg) {
        final Intent intent = generateLaunchIntent(context, pkg);
        if (intent != null) {

            context.startActivity(intent);
        }
    }

    /**
     * 生成启动已安装应用的 {@link PendingIntent}，未安装将返回空。
     *
     * @param pkg
     *            待启动应用的相关包名
     */
    public static PendingIntent generateLaunchPendingIntent(Context context,
                                                            String pkg) {
        PendingIntent pi = null;

        final Intent intent = generateLaunchIntent(context, pkg);
        if (intent != null) {
            pi = PendingIntent.getActivity(context, 0, intent, 0);
        }

        return pi;
    }

    /**
     * 生成安装APK的 {@link PendingIntent}，APK文件不存在或无效将返回空。
     *
     * @param path
     *            待安装APK的绝对路径
     */
    public static PendingIntent generateInstallPendingIntent(Context context,
                                                             String path) {
        PendingIntent pi = null;
        final Intent intent = generateInstallIntent(context, path);
        if (intent != null) {
            pi = PendingIntent.getActivity(context, 0, intent, 0);
        }

        return pi;
    }

    private static Intent generateLaunchIntent(Context context, String pkg) {
        Intent intent = null;
        if (context != null && !TextUtils.isEmpty(pkg)) {
            final PackageManager pm = context.getPackageManager();
            if (pm != null) {
                intent = pm.getLaunchIntentForPackage(pkg);
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }

        }

        return intent;
    }

    private static Intent generateInstallIntent(Context context, String path) {
        Intent intent = null;
        if (context != null && !TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                intent = new Intent(Intent.ACTION_VIEW);
                final Uri uri = Uri.fromFile(f);
                intent.setDataAndType(uri,
                        "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }

        return intent;
    }

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }
}
