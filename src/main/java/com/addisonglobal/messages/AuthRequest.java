package com.addisonglobal.messages;

import akka.actor.typed.ActorRef;

public final class AuthRequest {
    public final String username;
    public final String password;
    public final ActorRef<AuthResult> replyTo;

    public AuthRequest(String username, String password, ActorRef<AuthResult> replyTo) {
        this.username = username;
        this.password = password;
        this.replyTo = replyTo;
    }
}