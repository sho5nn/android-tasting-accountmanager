package com.github.sho5nn.tasting.accountmanager;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

public class AuthenticatorStatusViewModel {

    public final ObservableField<String> authenticatorPackage = new ObservableField<>();
    public final ObservableInt accountNumber = new ObservableInt();
    public final ObservableField<String> accountType = new ObservableField<>();
}
