package com.addisonglobal.actors;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import com.addisonglobal.messages.TokenRequest;
import com.addisonglobal.messages.TokenResult;
import com.addisonglobal.messages.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TokenActorTest {

    private ActorTestKit testKit;

    @BeforeEach
    public void setUp() {
        testKit = ActorTestKit.create();
    }

    @AfterEach
    public void tearDown() {
        testKit.shutdownTestKit();
    }

    @Test
    public void testTokenGenerationForValidUser() {
        TestProbe<TokenResult> probe = testKit.createTestProbe();
        User user = new User("house");
        ActorRef<TokenRequest> actor = testKit.spawn(TokenActor.create());
        actor.tell(new TokenRequest(user, probe.getRef()));
        TokenResult result = probe.receiveMessage(Duration.ofSeconds(6));
        assertTrue(result.success);
        assertNotNull(result.token);
        assertTrue(result.token.token.startsWith("house_"));
    }

    @Test
    public void testTokenGenerationFailsForUserStartingWithA() {
        TestProbe<TokenResult> probe = testKit.createTestProbe();
        User user = new User("alice");
        ActorRef<TokenRequest> actor = testKit.spawn(TokenActor.create());
        actor.tell(new TokenRequest(user, probe.getRef()));
        TokenResult result = probe.receiveMessage(Duration.ofSeconds(6));
        assertFalse(result.success);
        assertNull(result.token);
    }
}