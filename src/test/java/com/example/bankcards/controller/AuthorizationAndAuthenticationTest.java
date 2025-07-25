package com.example.bankcards.controller;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.WorkWithJwt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthorizationAndAuthenticationTest extends AbstractTest {

    @Autowired
    private WorkWithJwt workWithJwt;

    @BeforeEach
    public void clearBefore() {
        clearAfter();
    }

    @AfterEach
    public void clearAfter() {
        userRepository.deleteAll();
    }

    @Test
    public void registration_WithCorrectData_ReturnsCreatedAndSavedToDB() throws Exception {
        UserRequestDto userRequestDto =
                new UserRequestDto(
                        "admin228",
                        "password",
                        "Vlad",
                        "Last",
                        "Pat"
                );
        String result = mvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto))
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString().replaceAll("\"", "");
        UUID returnsUUID = UUID.fromString(result);
        User user = userRepository.findById(returnsUUID).orElseThrow();
        assertEquals(userRequestDto.username(), user.getUsername());
        assertTrue(passwordEncoder.matches(userRequestDto.password(), user.getPassword()));
        assertEquals(userRequestDto.firstName(), user.getFirstName());
        assertEquals(userRequestDto.lastName(), user.getLastName());
        assertEquals(userRequestDto.username(), user.getUsername());
    }

    @Test
    public void registration_WithEmptyCorrectDate_ReturnsBadRequestAndNotSavedToDB() throws Exception {
        UserRequestDto userRequestDto =
                new UserRequestDto(
                        "admin228",
                        "password",
                        "",
                        "Last",
                        "Pat"
                );
       mvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
                ).andExpect(status().isBadRequest());
       assertEquals(0, userRepository.findAll().size());
    }

    @Test
    public void registration_WithExistsInSystemUsername_ReturnsBadRequest() throws Exception {
        UserRequestDto userRequestDto =
                new UserRequestDto(
                        "admin228",
                        "password",
                        "First",
                        "Last",
                        "Pat"
                );
        saveUserToDB("admin228", "password123");
        mvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void login_WithNotExistsUser_returnsNotFound() throws Exception {
        LoginRequestDto login = new LoginRequestDto("admin228", "password123");
        mvc.perform(post("/api/v1/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void login_WithExistsUserButNotValidPassword_returnsBadRequest() throws Exception {
        saveUserToDB("admin228", "notValidPassword123");
        LoginRequestDto login = new LoginRequestDto("admin228", "password123");
        mvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void login_WithValidData_returnsJwtToken() throws Exception {
        String login = "admin228";
        String password = "pass";
        UUID uuid = saveUserToDB(login, password);
        LoginRequestDto loginRequest = new LoginRequestDto(login, password);
        String result = mvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JwtResponseDto jwtResponseDto = objectMapper.readValue(result, JwtResponseDto.class);
        assertTrue(workWithJwt.validateToken(jwtResponseDto.accessToken()));
        assertEquals(uuid, jwtResponseDto.userId());
    }

    @Test
    public void login_WithEmptyData_returnsBadRequest() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("", "");
        mvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    private UUID saveUserToDB(String username, String password) {
        User build = User.builder()
                .username(username)
                .patronymicName("lala")
                .firstName("lafa")
                .lastName("afsad")
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(build);
        return build.getId();
    }
}
