package com.github.sho5nn.tasting.accountmanager.authenticator;

import android.support.annotation.NonNull;

import com.github.sho5nn.tasting.accountmanager.utils.Utils;

import java.security.PublicKey;


class LoginWebService {

    @NonNull
    ResponseParam login(RequestParam requestParam) {
        ResponseParam successResponseParam = new ResponseParam();
        successResponseParam.name = requestParam.name;
        successResponseParam.token = generateToken(requestParam);

        try {
            // Pretend to be accessing the network.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return successResponseParam;
    }

    private String generateToken(RequestParam requestParam) {
        byte[] data = Utils.computeSha256((requestParam.name + requestParam.publicKey.toString()/* XXX */).getBytes());
        return Utils.byte2hex(data);
    }

    static class RequestParam {
        String name;
        String pass;
        PublicKey publicKey;
    }

    static class ResponseParam {
        String name;
        String token;
    }
}
