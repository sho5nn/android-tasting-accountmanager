package com.github.sho5nn.tasting.accountmanager;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.sho5nn.tasting.accountmanager.authenticator.LoginAccountProperties;
import com.github.sho5nn.tasting.accountmanager.permission.AppPermissions;
import com.github.sho5nn.tasting.accountmanager.utils.Utils;

import java.io.IOException;

import hugo.weaving.DebugLog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MainActivity
        extends AppCompatActivity
        implements PermissionsStatusViewModel.OnViewEventListener, UserInfoViewModel.OnViewEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_CHOOSE_ACCOUNT = 10;

    private UserInfoViewModel userInfo;
    private PermissionsStatusViewModel permissionsStatus;
    private AuthenticatorStatusViewModel authenticatorStatus;

    private AccountManager accountManager;
    private String accountType;


    @DebugLog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountManager = AccountManager.get(this);
        accountType = getString(R.string.account_type);

        userInfo = new UserInfoViewModel(this);
        permissionsStatus = new PermissionsStatusViewModel(this);
        authenticatorStatus = new AuthenticatorStatusViewModel();

        MainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setUserInfo(userInfo);
        binding.setPermissionsStatus(permissionsStatus);
        binding.setAuthenticatorStatus(authenticatorStatus);

        permissionsStatus.isRequestedGetAccountsPermission
                .set(AppPermissions.isRequestedGetAccountsPermission(this));
        permissionsStatus.isRequestedAuthenticateAccountsPermission
                .set(AppPermissions.isRequestedAuthenticateAccountsPermission(this));
        permissionsStatus.isRequestedManageAccountsPermission
                .set(AppPermissions.isRequestedManageAccountsPermission(this));
        permissionsStatus.isRequestedUseCredentialsPermission
                .set(AppPermissions.isRequestedUseCredentialsPermission(this));
    }

    @DebugLog
    @Override
    protected void onResume() {
        super.onResume();

        updateAccountStatus();
        updatePermissionsStatus();
    }

    @DebugLog
    @Override
    protected void onDestroy() {
        super.onDestroy();
        userInfo.onDestroy();
        permissionsStatus.onDestroy();
    }

    void updateAccountStatus() {
        //noinspection MissingPermission
        Account[] accounts = accountManager.getAccountsByType(accountType);
        authenticatorStatus.accountNumber.set(accounts.length);
        authenticatorStatus.authenticatorPackage.set(Utils.getAuthenticatorPackage(this, accountType));
        authenticatorStatus.accountType.set(accountType);
    }

    void updatePermissionsStatus() {
        updateGetAccountsPermissionStatusView();
        updateAuthenticateAccountsPermissionStatusView();
        updateManageAccountsPermissionStatusView();
        updateUseCredentialsPermissionStatusView();
    }

    @NeedsPermission(
            value = AppPermissions.PERMISSION_GET_ACCOUNTS
    )
    void updateGetAccountsPermissionStatusView() {
        permissionsStatus.isGrantedGetAccountsPermission
                .set(AppPermissions.checkSelfGetAccountsPermission(this));
    }

    @NeedsPermission(
            value = AppPermissions.PERMISSION_AUTHENTICATE_ACCOUNTS,
            maxSdkVersion = Build.VERSION_CODES.LOLLIPOP_MR1
    )
    void updateAuthenticateAccountsPermissionStatusView() {
        permissionsStatus.isGrantedAuthenticateAccountsPermission
                .set(AppPermissions.checkSelfAuthenticateAccountsPermission(this));
    }

    @NeedsPermission(
            value = AppPermissions.PERMISSION_MANAGE_ACCOUNTS,
            maxSdkVersion = Build.VERSION_CODES.LOLLIPOP_MR1
    )
    void updateManageAccountsPermissionStatusView() {
        permissionsStatus.isGrantedManageAccountsPermission
                .set(AppPermissions.checkSelfManageAccountsPermission(this));
    }

    @NeedsPermission(
            value = AppPermissions.PERMISSION_USE_CREDENTIALS,
            maxSdkVersion = Build.VERSION_CODES.LOLLIPOP_MR1
    )
    void updateUseCredentialsPermissionStatusView() {
        permissionsStatus.isGrantedUseCredentialsPermission
                .set(AppPermissions.checkSelfUserCredentialsPermission(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied(Manifest.permission.GET_ACCOUNTS)
    void onGetAccountsPermissionDenied() {
        updateGetAccountsPermissionStatusView();
    }

    @OnNeverAskAgain(Manifest.permission.GET_ACCOUNTS)
    void onGetAccountsPermissionNeverAskAgain() {
        Toast.makeText(this, "never ask again GET_ACCOUNTS permission", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickCheckGetAccountsPermissionButton() {
        MainActivityPermissionsDispatcher.updateGetAccountsPermissionStatusViewWithCheck(this);
    }

    @Override
    public void onClickCheckAuthenticateAccountsPermissionButton() {
        MainActivityPermissionsDispatcher.updateAuthenticateAccountsPermissionStatusViewWithCheck(this);
    }

    @Override
    public void onClickCheckManageAccountsPermissionButton() {
        MainActivityPermissionsDispatcher.updateManageAccountsPermissionStatusViewWithCheck(this);
    }

    @Override
    public void onClickCheckUseCredentialsPermissionButton() {
        MainActivityPermissionsDispatcher.updateUseCredentialsPermissionStatusViewWithCheck(this);
    }

    @Override
    public void onClickSignUpButton() {
        if (!Utils.checkAuthenticator(this, accountType)) {
            Toast.makeText(this, "processing was interrupted because it is an illegal authenticator", Toast.LENGTH_SHORT).show();
            return;
        }

        accountManager.addAccount(accountType, null, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                Bundle result;

                try {
                    result = future.getResult();
                } catch (OperationCanceledException e) {
                    Toast.makeText(MainActivity.this, "canceled while logged in", Toast.LENGTH_SHORT).show();
                    return;

                } catch (IOException | AuthenticatorException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                String type = result.getString(AccountManager.KEY_ACCOUNT_TYPE);

                requestAuthToken(new Account(name, type));
            }
        }, null);
    }

    @Override
    public void onClickSignInButton() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent = AccountManager.newChooseAccountIntent(null, null, new String[]{accountType}, null, null, null, null);
        } else {
            intent = AccountManager.newChooseAccountIntent(null, null, new String[]{accountType}, true, null, null, null, null);
        }
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_ACCOUNT);
    }

    @DebugLog
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_CODE_CHOOSE_ACCOUNT == requestCode) {
            if (RESULT_OK == resultCode) {

                String name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                String type = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);

                requestAuthToken(new Account(name, type));

            } else {
                Toast.makeText(this, "canceled choose to account.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @DebugLog
    private void requestAuthToken(Account account) {
        if (!Utils.checkAuthenticator(this, accountType)) {
            Toast.makeText(this, "processing was interrupted because it is an illegal authenticator", Toast.LENGTH_SHORT).show();
            return;
        }

        accountManager.getAuthToken(account, LoginAccountProperties.AUTH_TOKEN_TYPE, null, false, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                Bundle result;

                try {
                    result = future.getResult();
                } catch (OperationCanceledException e) {
                    Toast.makeText(MainActivity.this, "canceled while logged in", Toast.LENGTH_SHORT).show();
                    return;

                } catch (IOException | AuthenticatorException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                String token = result.getString(AccountManager.KEY_AUTHTOKEN);

                userInfo.name.set(name);
                userInfo.token.set(token);
            }
        }, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (R.id.menu_show_all_account == id) {
            showAllAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showAllAccount() {
        //noinspection MissingPermission
        Account[] accounts = accountManager.getAccounts();
        AllAccountListDialogFragment.createInstance(accounts)
                .show(getSupportFragmentManager(), AllAccountListDialogFragment.TAG);
    }
}
