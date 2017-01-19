package com.github.sho5nn.tasting.accountmanager.utils;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;

import com.github.sho5nn.tasting.accountmanager.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class Utils {

    private Utils() {
    }

    public static boolean validateCaller(Context context, String callerPackage) {
        if (context == null || TextUtils.isEmpty(callerPackage)) return false;

        if ("android".equals(callerPackage)) {
            return true;
        }

        if ("com.android.settings".equals(callerPackage)) {
            return true;
        }

        String hash = getCertificateHash(context, callerPackage);
        return validateCertificateHash(context, hash);
    }

    public static boolean checkAuthenticator(Context context, String accountType) {
        if (context == null || TextUtils.isEmpty(accountType)) return false;

        String authenticatorPackage = getAuthenticatorPackage(context, accountType);

        if (TextUtils.isEmpty(authenticatorPackage)) return false;

        String hash = getCertificateHash(context, authenticatorPackage);
        return validateCertificateHash(context, hash);
    }

    public static String getAuthenticatorPackage(Context context, String accountType) {
        if (context == null || TextUtils.isEmpty(accountType)) return null;

        AuthenticatorDescription[] descriptions = AccountManager.get(context).getAuthenticatorTypes();

        if (descriptions == null || descriptions.length == 0) return null;

        String authenticatorPackage = null;
        for (AuthenticatorDescription description : descriptions) {
            if (accountType.equals(description.type)) {
                authenticatorPackage = description.packageName;
                break;
            }
        }
        return authenticatorPackage;
    }

    public static boolean validateCertificateHash(Context context, String hash) {
        if (context == null || TextUtils.isEmpty(hash)) return false;

        final String[] whiteList = context.getResources().getStringArray(R.array.white_hash_list);

        for (String h : whiteList) {
            if (h.equals(hash)) {
                return true;
            }
        }
        return false;
    }

    public static String getCertificateHash(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) return null;

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            if (packageInfo.signatures.length != 1) return null;

            Signature signature = packageInfo.signatures[0];

            byte[] sha256 = computeSha256(signature.toByteArray());
            return byte2hex(sha256);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static byte[] computeSha256(byte[] data) {
        if (data == null) return null;

        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byte2hex(byte[] data) {
        if (data == null) return null;

        final StringBuilder builder = new StringBuilder();
        for (byte b : data) {
                builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
}
