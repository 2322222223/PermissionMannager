package com.rlz.permissionmannager.permissionhandle;

import android.content.Context;

import com.rlz.permissionmannager.PermissionRequestActivity;
import com.rlz.permissionmannager.annotation.PermissionCancel;
import com.rlz.permissionmannager.annotation.PermissionDenied;
import com.rlz.permissionmannager.annotation.PermissionNeed;
import com.rlz.permissionmannager.exception.PermissionException;
import com.rlz.permissionmannager.util.PermissionUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static com.rlz.permissionmannager.util.Constant.REQUEST_PERMISSION_AROUND;
import static com.rlz.permissionmannager.util.Constant.REQUEST_PERMISSION_POINT;

/**
 * Created by Z
 * on 2020/5/9
 */
@Aspect
public class PermissionAspect {

    /*
     * 语法:
     *       @注解 访问权限 返回值类型 类名.函数名(参数)
     *       不限权限 可以省略
     *       返回值不限 使用通配符
     *       类名函数名 也不限
     *       参数通用 使用..
     * */
    @Pointcut(REQUEST_PERMISSION_POINT)
    public void requestPermissin(PermissionNeed permission) {

    }

    /*
     * 通知
     *
     * JoinPoint 包含了 定义的所有参数 与反射类似 可以通过它获取到 所有
     * */
    @Around(REQUEST_PERMISSION_AROUND)
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint, final PermissionNeed permission) {

        final Object obj = joinPoint.getThis();
        Context context = (Context) obj;
        if (obj == null || context == null) {
            new PermissionException("joinPoint 调用失败" + obj.getClass().getSimpleName());
        }

        //开始做拦截操作
        PermissionRequestActivity.startPermissionequest(context, permission.value(),
                permission.reauestCode(), new IPermissionResultCallback() {
                    @Override
                    public void onPermissionGraned() {
                        try {
                            joinPoint.proceed();
                        } catch (Throwable throwable) {
                            new PermissionException("执行Proceed失败" + throwable.getMessage());
                        }
                    }

                    @Override
                    public void onPermissionCancel(int requestCode) {
                        PermissionUtil.invokeAnnotaition(obj, PermissionCancel.class, requestCode);
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) {
                        PermissionUtil.invokeAnnotaition(obj, PermissionDenied.class, requestCode);
                    }
                });
    }

}
