package com.addisonglobal.actors;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import com.addisonglobal.messages.TokenRequest;
import com.addisonglobal.messages.TokenResult;
import com.addisonglobal.messages.UserToken;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class TokenActor extends AbstractBehavior<TokenRequest> {
    
    private final Random random = new Random();

    /**
     * Método fábrica: cria o comportamento do TokenActor
     */
    public static Behavior<TokenRequest> create(){
        return Behaviors.setup(TokenActor::new);
    }

    /** 
     * Construtor privado (padrão Akka)
     */
    private TokenActor(ActorContext<TokenRequest> context) {
        super(context);
    }

    /**
     * Define como o ator responde às mensagens
     */
    @Override
    public Receive<TokenRequest> createReceive(){
        return newReceiveBuilder()
        .onMessage(TokenRequest.class, this::onTokenRequest)
        .build();
    }

    /**
     * Lida com o pedido de geração de token
     */
    private Behavior<TokenRequest> onTokenRequest(TokenRequest request){
        // O remetente que espera a resposta
        ActorRef<TokenResult> replyTo = request.replyTo;
        var log = getContext().getLog();
        
        int maxDelay = getContext().getSystem().settings().config().getInt("app.token-delay-max");
        long delay = random.nextInt(maxDelay+1);

        getContext().getSystem().scheduler().scheduleOnce(
            java.time.Duration.ofMillis(delay),
            () -> {
                // 5% de chance de falha
                boolean success = !request.user.userId.toUpperCase().startsWith("A");
                String token = success ? request.user.userId+"_"+ZonedDateTime.now(ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")) : null;
                UserToken userToken = token != null ? new UserToken(token) : null;
                log.info("Geração de token para userId {}:{} (delay {}ms)", request.user.userId, success? "Sucesso":"Falha", delay);
                replyTo.tell(new TokenResult(userToken, success));
            }, 
            getContext().getSystem().executionContext()
        );
        return this;
    }
}
