package com.github.sho5nn.tasting.accountmanager;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.view.View;

public class PermissionsStatusViewModel extends BaseObservable {

    interface OnViewEventListener {
        void onClickCheckGetAccountsPermissionButton();
        void onClickCheckAuthenticateAccountsPermissionButton();
        void onClickCheckManageAccountsPermissionButton();
        void onClickCheckUseCredentialsPermissionButton();
    }

    public final ObservableBoolean isRequestedGetAccountsPermission = new ObservableBoolean();
    public final ObservableBoolean isRequestedAuthenticateAccountsPermission = new ObservableBoolean();
    public final ObservableBoolean isRequestedManageAccountsPermission = new ObservableBoolean();
    public final ObservableBoolean isRequestedUseCredentialsPermission = new ObservableBoolean();

    public final ObservableBoolean isGrantedGetAccountsPermission = new ObservableBoolean();
    public final ObservableBoolean isGrantedAuthenticateAccountsPermission = new ObservableBoolean();
    public final ObservableBoolean isGrantedManageAccountsPermission = new ObservableBoolean();
    public final ObservableBoolean isGrantedUseCredentialsPermission = new ObservableBoolean();

    private OnViewEventListener onViewEventListener;


    public PermissionsStatusViewModel(OnViewEventListener onViewEventListener) {
        this.onViewEventListener = onViewEventListener;
    }

    public void onDestroy() {
        onViewEventListener = null;
    }

    public void onClickCheckGetAccountsPermissionButton(View view) {
        if (onViewEventListener == null) return;
        onViewEventListener.onClickCheckGetAccountsPermissionButton();
    }

    public void onClickCheckAuthenticateAccountsPermissionButton(View view) {
        if (onViewEventListener == null) return;
        onViewEventListener.onClickCheckAuthenticateAccountsPermissionButton();
    }

    public void onClickCheckManageAccountsPermissionButton(View view) {
        if (onViewEventListener == null) return;
        onViewEventListener.onClickCheckManageAccountsPermissionButton();
    }

    public void onClickCheckUseCredentialsPermissionButton(View view) {
        if (onViewEventListener == null) return;
        onViewEventListener.onClickCheckUseCredentialsPermissionButton();
    }
}
