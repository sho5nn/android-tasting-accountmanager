package com.github.sho5nn.tasting.accountmanager.authenticator;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.sho5nn.tasting.accountmanager.R;
import com.github.sho5nn.tasting.accountmanager.utils.Utils;

import hugo.weaving.DebugLog;


public class LoginValidateActivity extends AppCompatActivity {

    private static final String TAG = LoginValidateActivity.class.getSimpleName();

    private AccountAuthenticatorResponse authenticatorResponse;
    private int resultErrorCode = AccountManager.ERROR_CODE_CANCELED;
    private Bundle resultToAuthenticator;


    @DebugLog
    static Intent createIntent(@NonNull Context packageContext,
                               @NonNull AccountAuthenticatorResponse response,
                               @Nullable String callerPackage) {
        return new Intent(packageContext, LoginValidateActivity.class)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                .putExtra(AccountManager.KEY_ANDROID_PACKAGE_NAME, callerPackage);
    }

    @DebugLog
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accountType = getString(R.string.account_type);

        Intent intent = getIntent();
        authenticatorResponse = intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        String callerPackage = intent.getStringExtra(AccountManager.KEY_ANDROID_PACKAGE_NAME);

        boolean valid = Utils.validateCaller(this, callerPackage);

        Log.d(TAG, "STRICT_LOGIN_MODE:" + LoginAccountProperties.STRICT_LOGIN_MODE + " caller:" + callerPackage + " valid:" + valid);

        if (LoginAccountProperties.STRICT_LOGIN_MODE && !valid) {
            resultToAuthenticator = new Bundle();
            resultToAuthenticator.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_AUTHENTICATION);
            resultToAuthenticator.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid package");
            finish();
            return;
        }

        Log.d(TAG, "SINGLE_ACCOUNT_MODE:" + LoginAccountProperties.SINGLE_ACCOUNT_MODE);

        if (LoginAccountProperties.SINGLE_ACCOUNT_MODE) {

            Account[] accounts = AccountManager.get(this).getAccountsByType(accountType);

            Log.d(TAG, "account type:" + accountType + " count:" + accounts.length);

            if (accounts.length > 0) {
                Account account = accounts[0];
                resultToAuthenticator = new Bundle();
                resultToAuthenticator.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                resultToAuthenticator.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                finish();
                return;
            }
        }

        startActivity(LoginActivity.createIntent(this, authenticatorResponse, callerPackage));
        authenticatorResponse = null;
        finish();
    }


    @DebugLog
    @Override
    public void finish() {
        Log.d(TAG, "authenticatorResponse:" + authenticatorResponse + " resultToAuthenticator:" + resultToAuthenticator);

        if (authenticatorResponse != null) {
            authenticatorResponse.onRequestContinued();
            if (resultToAuthenticator != null) {
                authenticatorResponse.onResult(resultToAuthenticator);
            } else {
                authenticatorResponse.onError(resultErrorCode, getErrorMessage(resultErrorCode));
            }
            authenticatorResponse = null;
        }
        super.finish();
    }

    @DebugLog
    private String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case AccountManager.ERROR_CODE_CANCELED:
                return "canceled while logged in";
            case AccountManager.ERROR_CODE_NETWORK_ERROR:
                return "network error while logged in";
            case AccountManager.ERROR_CODE_BAD_AUTHENTICATION:
                return "bad authentication";

            /* something case */

            default:
                return "error while logged in";
        }
    }
}
