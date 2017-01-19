package com.github.sho5nn.tasting.accountmanager.broadcast;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.sho5nn.tasting.accountmanager.R;
import com.github.sho5nn.tasting.accountmanager.authenticator.LoginAccountProperties;
import com.github.sho5nn.tasting.accountmanager.permission.AppPermissions;

import java.io.IOException;

import hugo.weaving.DebugLog;


public class PackageRemovedBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = PackageRemovedBroadcastReceiver.class.getSimpleName();

    @DebugLog
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (!Intent.ACTION_PACKAGE_REMOVED.equals(action)) return;

        if (!AppPermissions.checkSelfGetAccountsPermission(context)) return;

        Log.d(TAG, "REMOVE_ACCOUNT_WITH_UNINSTALLING_APPLICATION_MODE : " + LoginAccountProperties.REMOVE_ACCOUNT_WITH_UNINSTALLING_APPLICATION_MODE);
        if (!LoginAccountProperties.REMOVE_ACCOUNT_WITH_UNINSTALLING_APPLICATION_MODE) return;

        AccountManager manager = AccountManager.get(context);
        AuthenticatorDescription[] descriptions = manager.getAuthenticatorTypes();
        if (descriptions.length <= 0) return;

        String accountType = context.getString(R.string.account_type);
        AuthenticatorDescription target = null;
        for (AuthenticatorDescription desc : descriptions) {
            if (desc.type.equals(accountType)) {
                target = desc;
                break;
            }
        }
        if (target == null) return;

        Log.d(TAG, "authenticator      package:" + target.packageName);
        Log.d(TAG, "broadcast receiver package:" + context.getPackageName());

        if (!target.packageName.equals(context.getPackageName())) return;

        Account[] accounts = manager.getAccountsByType(accountType);

        for (final Account account : accounts) {
            Log.d(TAG, "try remove account:" + account);
            manager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> future) {
                    try {
                        boolean result = future.getResult();
                        Log.d(TAG, "remove account result:" + result + " account:" + account);
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        }
    }
}
