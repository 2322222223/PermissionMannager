package com.rlz.permissionmannager.permissionhandle;

/**
 * Created by Z
 * on 2020/5/9
 */
public interface IPermissionResultCallback {

    void onPermissionGraned();

    void onPermissionCancel(int requestCode);

    void onPermissionDenied(int requestCode);
}
