package com.github.sho5nn.tasting.accountmanager.authenticator;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

public class LoginViewModel {

    interface OnViewEventListener {
        void onClickLoginButton(String name, String pass);
    }

    public final ObservableField<String> callerPackage = new ObservableField<>();
    public final ObservableField<String> strictMode = new ObservableField<>(String.valueOf(LoginAccountProperties.STRICT_LOGIN_MODE));
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> pass = new ObservableField<>();
    public final ObservableBoolean loggingIn = new ObservableBoolean();

    private OnViewEventListener listener;

    public LoginViewModel(OnViewEventListener listener) {
        this.listener = listener;
    }

    void destroy() {
        listener = null;
    }

    public void onClickLoginButton(View view) {
        if (listener == null) return;
        listener.onClickLoginButton(name.get(), pass.get());
    }
}
