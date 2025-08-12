package com.addisonglobal.messages;

import akka.actor.typed.ActorRef;

public final class TokenRequest {
    public final User user;
    public final ActorRef<TokenResult> replyTo;

    public TokenRequest(User user, ActorRef<TokenResult> replyTo) {
        this.user = user;
        this.replyTo = replyTo;
    }
}