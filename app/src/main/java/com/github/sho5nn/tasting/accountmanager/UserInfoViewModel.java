package com.github.sho5nn.tasting.accountmanager;

import android.databinding.ObservableField;
import android.view.View;


public class UserInfoViewModel {

    interface OnViewEventListener {
        void onClickSignInButton();
        void onClickSignUpButton();
    }

    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> token = new ObservableField<>();

    private OnViewEventListener listener;


    public UserInfoViewModel(OnViewEventListener listener) {
        this.listener = listener;
    }

    public void onDestroy() {
        listener = null;
    }

    public void onClickSignInButton(View view) {
        if (listener == null) return;
        listener.onClickSignInButton();
    }

    public void onClickSignUpButton(View view) {
        if (listener == null) return;
        listener.onClickSignUpButton();
    }
}
