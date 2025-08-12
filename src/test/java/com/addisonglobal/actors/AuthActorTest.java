package com.addisonglobal.actors;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import com.addisonglobal.messages.AuthRequest;
import com.addisonglobal.messages.AuthResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AuthActorTest {

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
    public void testValidCredentials() {
        TestProbe<AuthResult> probe = testKit.createTestProbe();
        ActorRef<AuthRequest> actor = testKit.spawn(AuthActor.create());
        actor.tell(new AuthRequest("house", "HOUSE", probe.getRef()));
        AuthResult result = probe.receiveMessage(Duration.ofSeconds(6));
        assertTrue(result.success);
        assertTrue("house".equals(result.user.userId));
    }

    @Test
    public void testInvalidCredentials() {
        TestProbe<AuthResult> probe = testKit.createTestProbe();
        ActorRef<AuthRequest> actor = testKit.spawn(AuthActor.create());
        actor.tell(new AuthRequest("house", "house", probe.getRef()));
        AuthResult result = probe.receiveMessage(Duration.ofSeconds(6));
        assertFalse(result.success);
        assertNull(result.user);
    }
}