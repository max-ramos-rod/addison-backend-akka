package com.addisonglobal.service;

import java.util.concurrent.CompletableFuture;

import com.addisonglobal.messages.Credentials;
import com.addisonglobal.messages.UserToken;

public interface AsyncTokenService {
    CompletableFuture<UserToken> requestToken (Credentials credentials);
}
