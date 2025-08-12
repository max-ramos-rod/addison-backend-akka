package com.addisonglobal.service;

import java.util.concurrent.CompletableFuture;

import com.addisonglobal.messages.Credentials;
import com.addisonglobal.messages.User;
import com.addisonglobal.messages.UserToken;

public interface AsyncTokenService {
    CompletableFuture<User> authenticate(Credentials credentials);
    CompletableFuture<UserToken> issueToken (User user);
    default CompletableFuture<UserToken> requestToken (Credentials credentials) {
        return authenticate(credentials)
            .thenCompose(user -> user != null? issueToken(user):CompletableFuture.completedFuture(null));
    }
}
