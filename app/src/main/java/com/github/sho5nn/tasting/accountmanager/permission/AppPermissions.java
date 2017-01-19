package com.github.sho5nn.tasting.accountmanager.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import hugo.weaving.DebugLog;


public class AppPermissions {

    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_AUTHENTICATE_ACCOUNTS = "android.permission.AUTHENTICATE_ACCOUNTS";
    public static final String PERMISSION_MANAGE_ACCOUNTS = "android.permission.MANAGE_ACCOUNTS";
    public static final String PERMISSION_USE_CREDENTIALS = "android.permission.USE_CREDENTIALS";


    @DebugLog
    public static boolean checkSelfGetAccountsPermission(@NonNull Context context) {
        return checkSelfPermission(context, PERMISSION_GET_ACCOUNTS);
    }

    @DebugLog
    public static boolean checkSelfAuthenticateAccountsPermission(@NonNull Context context) {
        return checkSelfPermission(context, PERMISSION_AUTHENTICATE_ACCOUNTS);
    }

    @DebugLog
    public static boolean checkSelfManageAccountsPermission(@NonNull Context context) {
        return checkSelfPermission(context, PERMISSION_MANAGE_ACCOUNTS);
    }

    @DebugLog
    public static boolean checkSelfUserCredentialsPermission(@NonNull Context context) {
        return checkSelfPermission(context, PERMISSION_USE_CREDENTIALS);
    }

    private static boolean checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @DebugLog
    public static boolean isRequestedGetAccountsPermission(@NonNull Context context) {
        return isRequestedPermission(context, PERMISSION_GET_ACCOUNTS);
    }

    @DebugLog
    public static boolean isRequestedAuthenticateAccountsPermission(@NonNull Context context) {
        return isRequestedPermission(context, PERMISSION_AUTHENTICATE_ACCOUNTS);
    }

    @DebugLog
    public static boolean isRequestedManageAccountsPermission(@NonNull Context context) {
        return isRequestedPermission(context, PERMISSION_MANAGE_ACCOUNTS);
    }

    @DebugLog
    public static boolean isRequestedUseCredentialsPermission(@NonNull Context context) {
        return isRequestedPermission(context, PERMISSION_USE_CREDENTIALS);
    }

    private static boolean isRequestedPermission(@NonNull Context context, @NonNull String checkPermission) {
        String[] requestedPermissions = getRequestedAppPermissions(context);
        for (String requestedPermission : requestedPermissions) {
            if (checkPermission.equals(requestedPermission)) return true;
        }
        return false;
    }

    private static String[] getRequestedAppPermissions(@NonNull Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            return info.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            return new String[0];
        }
    }
}
