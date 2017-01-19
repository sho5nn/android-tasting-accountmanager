package com.github.sho5nn.tasting.accountmanager.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.sho5nn.tasting.accountmanager.utils.Utils;

import hugo.weaving.DebugLog;


class AccountAuthenticator extends AbstractAccountAuthenticator {

    private static final String TAG = AccountAuthenticator.class.getSimpleName();

    private Context context;

    AccountAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @DebugLog
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType,
                             String authTokenType,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {

        String callerPackage = options.getString(AccountManager.KEY_ANDROID_PACKAGE_NAME);

        Intent intent = LoginValidateActivity.createIntent(context, response, callerPackage);

        Bundle result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, intent);
        return result;
    }

    @DebugLog
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account,
                               String authTokenType,
                               Bundle options) throws NetworkErrorException {

        String callerPackage = options.getString(AccountManager.KEY_ANDROID_PACKAGE_NAME);

        boolean valid = Utils.validateCaller(context, callerPackage);

        Bundle result = new Bundle();

        Log.d(TAG, "STRICT_LOGIN_MODE:" + LoginAccountProperties.STRICT_LOGIN_MODE + " caller:" + callerPackage + " valid:" + valid);

        if (LoginAccountProperties.STRICT_LOGIN_MODE && !valid) {
            result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_AUTHENTICATION);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid package");
            return result;
        }

        if (!LoginAccountProperties.AUTH_TOKEN_TYPE.equals(authTokenType)) {
            result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_ARGUMENTS);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid auth token type");
            return result;
        }

        // TODO decrypt token
        String token = AccountManager.get(context).peekAuthToken(account, authTokenType);
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, token);

        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
                                 String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account,
                                     Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account,
                                    String authTokenType,
                                    Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account,
                              String[] features) throws NetworkErrorException {
        return null;
    }
}
