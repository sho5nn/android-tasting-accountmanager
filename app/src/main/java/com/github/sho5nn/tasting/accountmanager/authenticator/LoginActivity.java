package com.github.sho5nn.tasting.accountmanager.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.sho5nn.tasting.accountmanager.R;

import hugo.weaving.DebugLog;


public class LoginActivity extends AppCompatActivity implements LoginViewModel.OnViewEventListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private AccountManager accountManager;
    private AccountAuthenticatorResponse authenticatorResponse;
    private Bundle result;
    private int resultErrorCode = AccountManager.ERROR_CODE_CANCELED;
    private String accountType;
    private LoginViewModel login;

    @DebugLog
    static Intent createIntent(@NonNull Context packageContext,
                               @NonNull AccountAuthenticatorResponse authenticatorResponse,
                               @Nullable String callerPackage) {
        return new Intent(packageContext, LoginActivity.class)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, authenticatorResponse)
                .putExtra(AccountManager.KEY_ANDROID_PACKAGE_NAME, callerPackage);
    }

    @DebugLog
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.app_name) + " - Login");

        login = new LoginViewModel(this);

        LoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLogin(login);

        accountManager = AccountManager.get(this);
        accountType = getString(R.string.account_type);

        Intent intent = getIntent();
        authenticatorResponse = intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        if (authenticatorResponse != null) {
            authenticatorResponse.onRequestContinued();
        }

        String callerPackage = intent.getStringExtra(AccountManager.KEY_ANDROID_PACKAGE_NAME);
        login.callerPackage.set(callerPackage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        login.destroy();
    }

    @Override
    public void onClickLoginButton(String name, String pass) {
        login(name, pass);
    }

    @DebugLog
    private void login(String name, String pass) {

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "must input name and pass", Toast.LENGTH_SHORT).show();
            return;
        }

        if (existAccountName(name)) {
            Toast.makeText(this, "already logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        login.loggingIn.set(true);

        LoginAsyncTask.login(getApplicationContext(), name, pass, new LoginAsyncTask.Callbacks() {
            @Override
            public void callback(@NonNull LoginWebService.ResponseParam response) {
                if (LoginActivity.this.isFinishing()) return;

                login.loggingIn.set(false);

                if (addAccount(response.name, response.token)) {
                    result = new Bundle();
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, response.name);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    LoginActivity.this.finish();

                } else {
                    Toast.makeText(LoginActivity.this, "failure login", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    @DebugLog
    private boolean addAccount(@NonNull String name, @NonNull String token) {
        Account newAccount = new Account(name, accountType);
        boolean success = accountManager.addAccountExplicitly(newAccount, null, null);

        if (success) {
            // TODO encrypt token
            accountManager.setAuthToken(newAccount, LoginAccountProperties.AUTH_TOKEN_TYPE, token);
        }
        return success;
    }

    @DebugLog
    @Override
    public void finish() {
        Log.d(TAG, "authenticatorResponse:" + authenticatorResponse + " result:" + result);

        if (authenticatorResponse != null) {
            if (result != null) {
                authenticatorResponse.onResult(result);
            } else {
                authenticatorResponse.onError(resultErrorCode, getErrorMessage(resultErrorCode));
            }
            authenticatorResponse = null;
        }
        super.finish();
    }

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

    @DebugLog
    private boolean existAccountName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        Account[] accounts = accountManager.getAccountsByType(accountType);
        Log.d(TAG, "account type:" + accountType + " count:" + accounts.length);

        for (Account account : accounts) {
            if (name.equals(account.name)) {
                return true;
            }
        }
        return false;
    }
}
