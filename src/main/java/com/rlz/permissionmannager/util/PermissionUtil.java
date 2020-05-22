package com.rlz.permissionmannager.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by hinge on 17/4/12.
 */

public final class PermissionUtil {

    private PermissionUtil() {

    }

    /**
     * 跳转到权限设置界面
     */
    public static Intent getAppDetailSettingIntent(Context context) {

        // vivo 点击设置图标>加速白名单>我的app
        // 点击软件管理>软件管理权限>软件>我的app>信任该软件
        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
        if (appIntent != null) {
            return appIntent;
        }

        // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
        // 点击权限隐私>自启动管理>我的app
        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
        if (appIntent != null) {
            return appIntent;
        }

        // miui
        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.miui.securitycenter");
        if (appIntent != null) {
            return appIntent;
        }

        // meizu
        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.meizu.safe");
        if (appIntent != null) {
            return appIntent;
        }
        // huawei
        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.huawei.systemmanager");
        if (appIntent != null) {
            return appIntent;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return intent;
    }

    /**
     * 是否有权限
     *
     * @param context
     * @return
     */
    public static boolean checkSelfPermission(Context context, String permission) {
        if (null == context) {
            return false;
        }
        int per = ContextCompat.checkSelfPermission(context, permission);
        return per == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int... grantResults) {
        // At least one result must be checked.
        if (null == grantResults || grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isGrantSDCardReadPermission(Context context) {
        return checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static void requestSDCardReadPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
    }


    public static boolean hasSelfPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!hasSelfPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSelfPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean shouldShowRequestPermissionRationales(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /*
     * 通过反射 调用方法
     * */

    public static void invokeAnnotaition(Object object, Class annotaition, int requestCode) {

        Class<?> calzz = object.getClass();

        Method[] methods = calzz.getDeclaredMethods();
        if (methods.length < 1) {
            new RuntimeException("没有找到定义的方法  in:" + calzz.getSimpleName());
        }
        for (Method method : methods) {
            //校验是否用的是需要的注解
            if (!method.isAnnotationPresent(annotaition)) {
                //校验参数 是否一致
                continue;
            }
            if (method.getParameterTypes().length != 1) {
                new RuntimeException("改方法只能有一个参数:" + calzz.getSimpleName());
            }
            method.setAccessible(true);
            try {
                method.invoke(object, requestCode);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
