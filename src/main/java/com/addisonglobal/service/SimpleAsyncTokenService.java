package com.addisonglobal.service;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.addisonglobal.messages.Credentials;
import com.addisonglobal.messages.IssueTokenCommand;
import com.addisonglobal.messages.TokenResponse;
import com.addisonglobal.messages.UserToken;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class SimpleAsyncTokenService {
    private final ActorSystem<Object> actorSystem;

    public SimpleAsyncTokenService(ActorSystem<Object> actorSystem) {
        this.actorSystem = actorSystem;
    }

    public CompletableFuture<UserToken> requestToken(Credentials credentials) {
        return AskPattern.ask(
                actorSystem,
                (ActorRef<TokenResponse> replyTo) -> new IssueTokenCommand(credentials.username, credentials.password, replyTo),
                Duration.ofSeconds(5),
                actorSystem.scheduler()
            )
            .thenApply(response -> {
                TokenResponse tokenResponse = (TokenResponse) response;
                return tokenResponse.success ? tokenResponse.token : null;
            })
            .toCompletableFuture();
    }
}