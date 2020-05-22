package com.rlz.permissionmannager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.rlz.permissionmannager.permissionhandle.IPermissionResultCallback;
import com.rlz.permissionmannager.util.PermissionUtil;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import static com.rlz.permissionmannager.util.Constant.PERMISSION_ARRAY;
import static com.rlz.permissionmannager.util.Constant.PERMISSION_REQUEST_CODE;

/**
 * Created by Z
 * on 2020/5/9
 */
public class PermissionRequestActivity extends Activity {

    private static IPermissionResultCallback sIPermissionResultCallback;

    public static void startPermissionequest(Context context, String[] permissions,
                                             int requestCode, IPermissionResultCallback permissionResultCallback) {
        sIPermissionResultCallback = new WeakReference<>(permissionResultCallback).get();
        Intent intent = new Intent(context, PermissionRequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSION_ARRAY, permissions);
        bundle.putInt(PERMISSION_REQUEST_CODE, requestCode);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取到发送过来的值
        String[] permissions = null;
        int requestCode = 0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            permissions = bundle.getStringArray(PERMISSION_ARRAY);
            requestCode = bundle.getInt(PERMISSION_REQUEST_CODE);

        }
        requestPermission(permissions, requestCode);
    }

    private void requestPermission(String[] permissions, int requestCode) {
        if (PermissionUtil.hasSelfPermissions(this, permissions)) {
            sIPermissionResultCallback.onPermissionGraned();
            finish();
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionUtil.verifyPermissions(grantResults)) {
            //所有权限通过
            sIPermissionResultCallback.onPermissionGraned();
        } else {
            if (PermissionUtil.shouldShowRequestPermissionRationales(this, permissions)) {
                sIPermissionResultCallback.onPermissionCancel(requestCode);
            } else {
                sIPermissionResultCallback.onPermissionDenied(requestCode);
            }
        }

        finish();
        //关闭动画
        overridePendingTransition(0, 0);
    }
}
