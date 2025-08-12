package com.addisonglobal.messages;

public final class AuthResult {
    public final boolean success;
    public final User user;

    public AuthResult(boolean success, User user) {
        this.success = success;
        this.user = user;
    }
}