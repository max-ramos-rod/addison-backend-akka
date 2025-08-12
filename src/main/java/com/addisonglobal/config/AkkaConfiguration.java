package com.addisonglobal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.addisonglobal.actors.TokenOrchestratorActor;

import akka.actor.typed.ActorSystem;

@Configuration
public class AkkaConfiguration {
    @Bean
    public ActorSystem<Object> actorSystem() {
        return ActorSystem.create(TokenOrchestratorActor.create(), "OrchestratorSystem");
    }
}