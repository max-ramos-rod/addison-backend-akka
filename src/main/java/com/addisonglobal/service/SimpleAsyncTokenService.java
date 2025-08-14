package com.addisonglobal.service;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.addisonglobal.messages.Credentials;
import com.addisonglobal.messages.IssueTokenCommand;
import com.addisonglobal.messages.IssueTokenResponse;
import com.addisonglobal.messages.TokenResponse;
import com.addisonglobal.messages.UserToken;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class SimpleAsyncTokenService implements AsyncTokenService {
    private final ActorSystem<?> actorSystem;
    private final ActorRef<IssueTokenCommand> tokenOrchestrator;

    public SimpleAsyncTokenService(ActorSystem<?> actorSystem, ActorRef<IssueTokenCommand> tokenOrchestrator) {
        this.actorSystem = actorSystem;
        this.tokenOrchestrator = tokenOrchestrator;
    }

    public CompletableFuture<UserToken> requestToken(Credentials credentials) {
        return AskPattern.<IssueTokenCommand, TokenResponse>ask(
                tokenOrchestrator,
                replyTo -> new IssueTokenCommand(credentials.username, credentials.password, replyTo),
                Duration.ofSeconds(12),
                actorSystem.scheduler()
            )
            .thenApply(response -> {
                TokenResponse tokenResponse = (TokenResponse) response;
                return tokenResponse.success ? tokenResponse.token : null;
            })
            .toCompletableFuture();
    }
}