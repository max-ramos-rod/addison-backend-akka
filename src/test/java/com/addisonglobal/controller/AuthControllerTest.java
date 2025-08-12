package com.addisonglobal.controller;

import com.addisonglobal.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testValidCredentials() throws Exception {
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"house\",\"password\":\"HOUSE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    public void testInvalidUserIdStartingWithA() throws Exception {
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"ALICE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.token").doesNotExist())
            .andExpect(jsonPath("$.error").value("Authentication or token generation failed"));
    }

    @Test
    public void testInvalidCredentials() throws Exception {
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"house\",\"password\":\"house\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.token").doesNotExist())
            .andExpect(jsonPath("$.error").value("Authentication or token generation failed"));
    }
}