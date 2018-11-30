package cc.bodyplus.health.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cc.bodyplus.health.App;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;


/**
 * Created by bodyplus on 2016/1/26.
 */
public class ShareInfo {

    public static void showShareUrl(Context mContext, String title, final String content, final String ImagePath, final String url){
        showShareUrl(title,content,ImagePath,url);
    }

    public static void showShareUrls(Context mContext, String title, String content, String ImagePath, String url){
        showShareUrl(title,content,ImagePath,url);
    }

    /**
     * 分享链接，带标题内容和缩略图，（缩略图可以是本地图片也可以来自网络）
     * @param title
     * @param content
     * @param ImagePath
     * @param url
     */
    public static void showShareUrl(final String title, final String content, final String ImagePath, final String url){
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {

                if ("WechatMoments".equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setText(content); // 朋友圈不显示此字段
                    paramsToShare.setUrl(url);
                }else if ("SinaWeibo".equals(platform.getName())){
                    paramsToShare.setText(title+"\n"+content+"\n"+url);
                }else if (platform.getName().equalsIgnoreCase("Wechat")){
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setText(content);
                    paramsToShare.setUrl(url);
                }else if (platform.getName().equalsIgnoreCase("QQ")){
                    paramsToShare.setTitleUrl(url);
//                    paramsToShare.setText(content.length()>30? content.substring(0,30):content);
                    paramsToShare.setText(content);
                }
                paramsToShare.setTitle(title);
                if (ImagePath.startsWith("http://")){
                    paramsToShare.setImageUrl(ImagePath);
                }else {
                    paramsToShare.setImagePath(ImagePath);
                }
            }
        });
        oks.show(App.instance);
    }


    /**
     * 只分享本地图片 没有链接
     * @param title
     * @param content
     * @param ImageUrl
     */
    public static void showSharePic(final String title, final String content, final String ImageUrl) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
//        oks.addHiddenPlatform(Wechat.NAME);//WechatFavorite

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (platform.getName().equalsIgnoreCase("WechatMoments")) { // Wechat  SinaWeibo QQ
                    paramsToShare.setTitle(paramsToShare.getTitle() + " " + paramsToShare.getText());
                    paramsToShare.setShareType(Platform.SHARE_IMAGE);
                    paramsToShare.setImagePath(ImageUrl);
                }else if (platform.getName().equalsIgnoreCase("Wechat")){
                    paramsToShare.setShareType(Platform.SHARE_IMAGE);
                    paramsToShare.setImagePath(ImageUrl);
                }else if (platform.getName().equalsIgnoreCase("SinaWeibo")){
                    paramsToShare.setText(title+"\n"+content);
                    paramsToShare.setImagePath(ImageUrl);
                }else if (platform.getName().equalsIgnoreCase("QQ")){
                    paramsToShare.setImagePath(ImageUrl);
                }
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                int a = i;
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                int a = i;
                throwable.printStackTrace();
                Log.d("health health"," i = "+i+ " ------ "+throwable.getMessage());

            }

            @Override
            public void onCancel(Platform platform, int i) {
                int a = i;
            }
        });
        oks.show(App.instance);
    }

    /**
     * 截取scrollview的屏幕
     * fileName  20160314.png
     *
     * **/
    public static String getBitmapByView(ScrollView scrollView, String fileName) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            //scrollView.getChildAt(i).setBackgroundResource(R.drawable.layout_color_white);
        }
        Log.d("aa", "实际高度:" + h);
        Log.d("aa", " 高度:" + scrollView.getHeight());
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h , Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);

        File Files = new File(Config.CAMERA_IMG_PATH);//创建目录
        if (!Files.exists()) {// 目录存在返回false
            Files.mkdirs();// 创建一个目录
        }

        File f = new File(Config.CAMERA_IMG_PATH, fileName);
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Config.CAMERA_IMG_PATH+"/"+fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return f.getAbsolutePath().toString();
    }

    public static String takeScreenShot(Activity activity, String FileName) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        System.out.println("b1......................"+b1);

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay() .getHeight();
        // 去掉标题栏
        Bitmap bitmap = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();

        File Files = new File(Config.CAMERA_IMG_PATH);//创建目录
        if (!Files.exists()) {// 目录存在返回false
            Files.mkdirs();// 创建一个目录
        }

        File f = new File(Config.CAMERA_IMG_PATH, FileName);
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Config.CAMERA_IMG_PATH+"/"+FileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return f.getAbsolutePath().toString();
    }

    public static String takeScreenShot(Activity activity, String FileName, int top_height) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay() .getHeight();
        // 去掉标题栏
        Bitmap bitmap = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight - top_height);
        view.destroyDrawingCache();

        File Files = new File(Config.CAMERA_IMG_PATH);//创建目录
        if (!Files.exists()) {// 目录存在返回false
            Files.mkdirs();// 创建一个目录
        }

        File f = new File(Config.CAMERA_IMG_PATH, FileName);
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Config.CAMERA_IMG_PATH+"/"+FileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return f.getAbsolutePath().toString();
    }

    public static File takeScreenShot2(Fragment fragment, String FileName) {
        // View是你需要截图的View
//        View view = fragment.getActivity().getWindow().getDecorView();
        View view = fragment.getView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        System.out.println("b1......................" + b1);

        // 获取状态栏高度
        Rect frame = new Rect();
        fragment.getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);

        // 获取屏幕长和高
//        int width = fragment.getActivity().getWindowManager().getDefaultDisplay().getWidth();
//        int height = fragment.getActivity().getWindowManager().getDefaultDisplay() .getHeight();
        int width = view.getWidth();
        int height = view.getHeight();
        // 去掉标题栏
//        Bitmap bitmap = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        Bitmap bitmap = Bitmap.createBitmap(b1, 0, 0, width, height);
        view.destroyDrawingCache();

        File Files = new File(Config.CAMERA_IMG_PATH);//创建目录
        if (!Files.exists()) {// 目录存在返回false
            Files.mkdirs();// 创建一个目录
        }

        File f = new File(Config.CAMERA_IMG_PATH, FileName);
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Config.CAMERA_IMG_PATH+"/"+FileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return f;
    }

    public static File takeScreenShot3(RelativeLayout relativeLayout, String FileName) {
        // View是你需要截图的View
//        View view = fragment.getActivity().getWindow().getDecorView();
//        View view = relativeLayout.getRootView();
        View view = relativeLayout.getChildAt(1);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        System.out.println("b1......................" + b1);
        // 获取屏幕长和高
//        int width = fragment.getActivity().getWindowManager().getDefaultDisplay().getWidth();
//        int height = fragment.getActivity().getWindowManager().getDefaultDisplay() .getHeight();
        int width = view.getWidth();
        int height = view.getHeight();
        // 去掉标题栏
//        Bitmap bitmap = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        Bitmap bitmap = Bitmap.createBitmap(b1, 0, 0, width, height);
        view.destroyDrawingCache();

        File Files = new File(Config.CAMERA_IMG_PATH);//创建目录
        if (!Files.exists()) {// 目录存在返回false
            Files.mkdirs();// 创建一个目录
        }

        File f = new File(Config.CAMERA_IMG_PATH, FileName);
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Config.CAMERA_IMG_PATH+"/"+FileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return f;
    }

    public static File createImageFromResouse(Context context , int reouseId ) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),reouseId);

        File Files = new File(Config.CAMERA_IMG_PATH);//创建目录
        if (!Files.exists()) {// 目录存在返回false
            Files.mkdirs();// 创建一个目录
        }

        File f = new File(Config.CAMERA_IMG_PATH, "icon.png");
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Config.CAMERA_IMG_PATH+"/"+"icon.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return f;
    }
}
