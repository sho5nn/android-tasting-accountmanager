package com.github.sho5nn.tasting.accountmanager.keystore;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.NonNull;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;


class AppKeyGenerator {

    @NonNull
    private Context context;

    @NonNull
    private String keyStoreType;

    AppKeyGenerator(@NonNull Context context, @NonNull String keyStoreType) {
        this.context = context;
        this.keyStoreType = keyStoreType;
    }

    void generate(@NonNull String alias) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", keyStoreType);
            generator.initialize(createKeyPairGeneratorSpec(alias));
            generator.generateKeyPair();

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private KeyPairGeneratorSpec createKeyPairGeneratorSpec(@NonNull String alias) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 50);

        return new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(new X500Principal(String.format("CN=%s", alias)))
                .setSerialNumber(BigInteger.valueOf(1000000))
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
    }
}
