package com.addisonglobal.service;

import com.addisonglobal.messages.Credentials;
import com.addisonglobal.messages.User;
import com.addisonglobal.messages.UserToken;

public interface SyncTokenService {
    User authenticate(Credentials credentials);
    UserToken issueToken(User user);
    default UserToken requestToken(Credentials credentials) {
        User user = authenticate(credentials);
        if (user == null) {
            return null;
        }
        return issueToken(user);
    }    
}
