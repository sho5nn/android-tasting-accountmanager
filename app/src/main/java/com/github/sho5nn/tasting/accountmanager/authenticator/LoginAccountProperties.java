package com.github.sho5nn.tasting.accountmanager.authenticator;


public class LoginAccountProperties {
    static final boolean SINGLE_ACCOUNT_MODE = false;
    static final boolean STRICT_LOGIN_MODE = true;
    static final String KEY_ALIAS = "com.github.sho5nn.tasting.accountmanager.key";

    public static final String AUTH_TOKEN_TYPE = "com.github.sho5nn.tasting.accountmanager.token";
    /** Remove the account at the same time as uninstalling the application. **/
    public static final boolean REMOVE_ACCOUNT_WITH_UNINSTALLING_APPLICATION_MODE = false;
}
