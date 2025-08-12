package com.addisonglobal.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.addisonglobal.api.AuthRequestBody;
import com.addisonglobal.api.AuthResponseBody;
import com.addisonglobal.messages.Credentials;
import com.addisonglobal.messages.UserToken;
import com.addisonglobal.service.SimpleAsyncTokenService;

import akka.actor.typed.ActorSystem;

@RestController
public class AuthController {
    private final SimpleAsyncTokenService tokenService;
    @Autowired
    public AuthController(ActorSystem<Object> actorSystem) {
        this.tokenService = new SimpleAsyncTokenService(actorSystem);
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponseBody> authenticate(@RequestBody AuthRequestBody requestBody){
        try{
            Credentials credentials = new Credentials(requestBody.getUsername(), requestBody.getPassword());
            CompletableFuture<UserToken> future = tokenService.requestToken(credentials);
            UserToken userToken = future.get(10, TimeUnit.SECONDS);
            if(userToken != null){
                return ResponseEntity.ok(new AuthResponseBody(true, userToken.token, null));
            }else{
                return ResponseEntity.ok(new AuthResponseBody(false,null, "Authentication or token generation failed."));
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new AuthResponseBody(false, null, "Invalid request: "+ e.getMessage()));
        }
    }
}
