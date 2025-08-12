package com.addisonglobal.messages;

import akka.actor.typed.ActorRef;

public final class IssueTokenCommand {
    public final String username;
    public final String password;
    public final ActorRef<TokenResponse> replyTo;

    public IssueTokenCommand(String username, String password, ActorRef<TokenResponse> replyTo) {
        this.username = username;
        this.password = password;
        this.replyTo = replyTo;
    }
}