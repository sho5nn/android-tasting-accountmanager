package com.github.sho5nn.tasting.accountmanager.authenticator;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.github.sho5nn.tasting.accountmanager.authenticator.LoginWebService.ResponseParam;
import com.github.sho5nn.tasting.accountmanager.authenticator.LoginWebService.RequestParam;
import com.github.sho5nn.tasting.accountmanager.keystore.AppKeyRepository;

class LoginAsyncTask extends AsyncTask<Void, Void, ResponseParam> {

    private Context context;
    private Callbacks callbacks;
    private String name;
    private String pass;

    static LoginAsyncTask login(@NonNull Context context,
                                @NonNull String name,
                                @NonNull String pass,
                                @NonNull Callbacks callbacks) {
        return new LoginAsyncTask(context, name, pass, callbacks);
    }

    private LoginAsyncTask(@NonNull Context context,
                           @NonNull String name,
                           @NonNull String pass,
                           @NonNull Callbacks callbacks) {
        super();

        this.context = context;
        this.name = name;
        this.pass = pass;
        this.callbacks = callbacks;
    }

    @Override
    protected ResponseParam doInBackground(Void... params) {
        RequestParam requestParam = new RequestParam();
        requestParam.name = name;
        requestParam.pass = pass;
        requestParam.publicKey = new AppKeyRepository(context, LoginAccountProperties.KEY_ALIAS).getPublicKey();

        return new LoginWebService().login(requestParam);
    }

    @Override
    protected void onPostExecute(ResponseParam param) {
        callbacks.callback(param);
    }

    interface Callbacks {
        void callback(@NonNull ResponseParam response);
    }
}
