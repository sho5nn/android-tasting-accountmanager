package com.github.sho5nn.tasting.accountmanager.keystore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


public class AppKeyRepository {

    private static final String KEY_STORE_TYPE = "AndroidKeyStore";

    @Nullable
    private AppKeyGenerator keyGenerator;

    @NonNull
    private Context context;

    @NonNull
    private String alias;

    private KeyStore keyStore;

    public AppKeyRepository(@NonNull Context context, @NonNull String alias) {
        this.context = context;
        this.alias = alias;
        init();
    }

    private void init() {
        try {
            keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(null);

            if (existKey()) return;

            keyGenerator = new AppKeyGenerator(context, KEY_STORE_TYPE);
            keyGenerator.generate(alias);

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean existKey() {
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate(alias).getPublicKey();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(alias, null);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
