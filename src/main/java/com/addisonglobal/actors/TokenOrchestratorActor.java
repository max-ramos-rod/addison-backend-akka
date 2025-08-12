package com.addisonglobal.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.StashBuffer;
import com.addisonglobal.messages.*;

public class TokenOrchestratorActor extends AbstractBehavior<Object> {

    // Mensagens internas para gerenciar o fluxo
    private static class WrappedAuthResult {
        final AuthResult result;
        final ActorRef<TokenResponse> client;

        WrappedAuthResult(AuthResult result, ActorRef<TokenResponse> client) {
            this.result = result;
            this.client = client;
        }
    }

    private static class WrappedTokenResult {
        final TokenResult result;
        final ActorRef<TokenResponse> client;

        WrappedTokenResult(TokenResult result, ActorRef<TokenResponse> client) {
            this.result = result;
            this.client = client;
        }
    }

    private final ActorRef<AuthRequest> authActor;
    private final ActorRef<TokenRequest> tokenActor;
    private final StashBuffer<Object> stash;

    public static Behavior<Object> create() {
        return Behaviors.setup(context -> {
            ActorRef<TokenRequest> tokenActor = context.spawn(TokenActor.create(), "token-actor");
            ActorRef<AuthRequest> authActor = context.spawn(AuthActor.create(), "auth-actor");
            return Behaviors.withStash(100, stash ->
                new TokenOrchestratorActor(context, authActor, tokenActor, stash)
            );
        });
    }

    private TokenOrchestratorActor(
        ActorContext<Object> context,
        ActorRef<AuthRequest> authActor,
        ActorRef<TokenRequest> tokenActor,
        StashBuffer<Object> stash
    ) {
        super(context);
        this.authActor = authActor;
        this.tokenActor = tokenActor;
        this.stash = stash;
    }

    @Override
    public Receive<Object> createReceive() {
        return newReceiveBuilder()
            .onMessage(IssueTokenCommand.class, this::onIssueToken)
            .onMessage(WrappedAuthResult.class, this::onAuthResult)
            .onMessage(WrappedTokenResult.class, this::onTokenResult)
            .build();
    }

    private Behavior<Object> onIssueToken(IssueTokenCommand command) {
        ActorRef<AuthResult> authResultHandler = getContext().spawnAnonymous(
            Behaviors.receiveMessage(authResult -> {
                getContext().getSelf().tell(new WrappedAuthResult(authResult, command.replyTo));
                return Behaviors.stopped();
            })
        );
        authActor.tell(new AuthRequest(command.username, command.password, authResultHandler));
        return Behaviors.same();
    }

    private Behavior<Object> onAuthResult(WrappedAuthResult wrapped) {
        getContext().getLog().info("Resultado da autenticação: {}", wrapped.result.success);
        if (wrapped.result.success) {
            ActorRef<TokenResult> tokenResultHandler = getContext().spawnAnonymous(
                Behaviors.receiveMessage(tokenResult -> {
                    getContext().getSelf().tell(new WrappedTokenResult(tokenResult, wrapped.client));
                    return Behaviors.stopped();
                })
            );
            tokenActor.tell(new TokenRequest(wrapped.result.user, tokenResultHandler));
        } else {
            getContext().getLog().info("Autenticação falhou, retornando falha ao cliente.");
            wrapped.client.tell(new TokenResponse(null, false));
        }
        return Behaviors.same();
    }

    private Behavior<Object> onTokenResult(WrappedTokenResult wrapped) {
        getContext().getLog().info("Resultado da geração de token: {}", wrapped.result.success);
        wrapped.client.tell(new TokenResponse(wrapped.result.token, wrapped.result.success));
        return Behaviors.same();
    }
}