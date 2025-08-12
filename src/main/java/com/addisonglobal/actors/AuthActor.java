package com.addisonglobal.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import com.addisonglobal.messages.AuthRequest;
import com.addisonglobal.messages.AuthResult;
import com.addisonglobal.messages.User;

import java.util.Random;

public class AuthActor extends AbstractBehavior<AuthRequest> {

    // Gerador de números aleatórios para simular as falhas.
    private final Random random = new Random();

    /**
     * Método fábrica: cria o comportamento do AuthActor
     */
    public static Behavior<AuthRequest> create(){
        return Behaviors.setup(AuthActor::new);
    }

    /**
     * Contrutor privado (padrão Akka)
     */
    private AuthActor(ActorContext<AuthRequest> context) {
        super(context);
    }
    
    /**
     * Define como o ator responde às mensagens
     */

    @Override
    public Receive<AuthRequest> createReceive() {
        return newReceiveBuilder()
            .onMessage(AuthRequest.class, this::onAuthRequest)
            .build();
    }
    
    /**
     * Lida com o pedido de autenticação
     */
    private Behavior<AuthRequest> onAuthRequest(AuthRequest request) {
        // O remetente da mensagem (quem enviou o AuthRequest)
        ActorRef<AuthResult> replyTo = request.replyTo;
        var log = getContext().getLog();
        int maxDelay = getContext().getSystem().settings().config().getInt("app.auth-delay-max");
        long delay = random.nextInt(maxDelay+1); // Atraso aleatório de 0 a 5000ms
        // Simula um processamento assíncrono com atraso
        getContext().getSystem().scheduler().scheduleOnce(
            java.time.Duration.ofMillis(delay),
            () -> {
                // Valida crendeciais: só "user"/"pass" funciona
                //boolean authSuccess = "user".equals(request.username) && "pass".equals(request.password);
                boolean success = request.password.equals(request.username.toUpperCase());

                User user = success? new User(request.username) : null;

                log.info("Autenticação para {}:{} (delay: {}ms)", request.username, success? "Sucesso":"Falha", delay);
                
                replyTo.tell(new AuthResult(success, user));

            },
            getContext().getSystem().executionContext()
        );
        return this;
    }    
}
