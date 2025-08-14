package com.addisonglobal.controller;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.addisonglobal.api.AuthRequestBody;
import com.addisonglobal.api.AuthResponseBody;
import com.addisonglobal.messages.Credentials;
import com.addisonglobal.messages.UserToken;
import com.addisonglobal.service.AsyncTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AsyncTokenService tokenService;

    @Autowired
    public AuthController(AsyncTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponseBody> authenticate(@RequestBody AuthRequestBody requestBody){
        log.info("Received authentication request for user: {}", requestBody.getUsername());
        try{
            Credentials credentials = new Credentials(requestBody.getUsername(), requestBody.getPassword());
            UserToken userToken = tokenService.requestToken(credentials).get(12, TimeUnit.SECONDS);

            if(userToken != null){
                log.info("Authentication successful for user: {}", requestBody.getUsername());
                return ResponseEntity.ok(new AuthResponseBody(true, userToken.token, null));
            }else{
                log.warn("Authentication or token generation failed for user: {}", requestBody.getUsername());
                return ResponseEntity.ok(new AuthResponseBody(false,null, "Authentication or token generation failed."));
            }
        } catch (TimeoutException e) {
            log.warn("Timeout during token request for user: {}", requestBody.getUsername(), e);
            return ResponseEntity.status(504).body(new AuthResponseBody(
                false, null, "Authentication service timed out. Please try again."
            ));
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: {}", requestBody.getUsername(), e);
            return ResponseEntity.status(500).body(new AuthResponseBody(
                false, null, "Internal server error."
            ));
        }
    }
}
