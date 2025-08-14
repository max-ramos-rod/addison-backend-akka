package com.addisonglobal.messages;

public class IssueTokenResponse {
    public final UserToken token;
    public final boolean success;

    public IssueTokenResponse(UserToken token, boolean success) {
        this.token = token;
        this.success = success;
    }
}
