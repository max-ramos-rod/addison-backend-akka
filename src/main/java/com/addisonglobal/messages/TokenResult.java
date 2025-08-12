package com.addisonglobal.messages;

public final class TokenResult {
    public final UserToken token;
    public final boolean success;

    public TokenResult(UserToken token, boolean success) {
        this.token = token;
        this.success = success;
    }
}