package com.addisonglobal.messages;

public final class TokenResponse {
    public final UserToken token;
    public final boolean success;

    public TokenResponse(UserToken token, boolean success) {
        this.token = token;
        this.success = success;
    }
}