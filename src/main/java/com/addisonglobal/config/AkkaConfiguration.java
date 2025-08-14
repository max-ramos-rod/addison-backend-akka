package com.addisonglobal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.addisonglobal.actors.TokenOrchestratorActor;
import com.addisonglobal.messages.IssueTokenCommand;
import com.addisonglobal.service.AsyncTokenService;
import com.addisonglobal.service.SimpleAsyncTokenService;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;

@Configuration
public class AkkaConfiguration {
    @Bean
    public ActorSystem<Object> actorSystem() {
        return ActorSystem.create(TokenOrchestratorActor.create(), "OrchestratorSystem");
    }

    @Bean
    public ActorRef<IssueTokenCommand> tokenOrchestrator(ActorSystem<Object> actorSystem) {
        // Como o guardian é o TokenOrchestratorActor, o próprio actorSystem é o ActorRef dele
        return actorSystem.narrow();
    }

    @Bean
    public AsyncTokenService tokenService(ActorSystem<Object> actorSystem, ActorRef<IssueTokenCommand> tokenOrchestrator) {
        return new SimpleAsyncTokenService(actorSystem, tokenOrchestrator);
    }
}