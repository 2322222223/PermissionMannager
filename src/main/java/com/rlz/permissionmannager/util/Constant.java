package com.rlz.permissionmannager.util;

/**
 * Created by Z
 * on 2020/5/9
 */
public class Constant {

    public static final String REQUEST_PERMISSION_POINT = "execution(@com.rlz.permissionmannager.annotation.PermissionNeed * *(..)) " + "&& @annotation(permission)";

    public static final String REQUEST_PERMISSION_AROUND = "requestPermissin(permission)";

    public static final String PERMISSION_ARRAY = "permission_array";

    public static final String PERMISSION_REQUEST_CODE = "permission_request_code";

}
