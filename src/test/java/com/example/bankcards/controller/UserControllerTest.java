package com.example.bankcards.controller;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.dto.request.UserFilterRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends AbstractTest {

    @Autowired
    private UserController userController;

    @BeforeEach
    public void clearBefore() {
        authentificateAsAdmin();
        clearAfter();
    }

    @AfterEach
    public void clearAfter() {
        userRepository.deleteAll();
    }

    @Test
    public void delete_WithNotExistsUUID_ReturnsBadRequest() throws Exception {
        int count = 3;
        List<UUID> someUsersInDB = createSomeUsersInDB(count);
        UUID idToDelete = UUID.randomUUID();
        while (someUsersInDB.contains(idToDelete)) {
            idToDelete = UUID.randomUUID();
        }

        mvc.perform(delete("/api/v1/user/" + idToDelete))
                .andExpect(status().isNotFound());

        assertEquals(userRepository.count(), count);
    }

    @Test
    public void delete_WithExistsUUID_ReturnsOkAndDeleteUser() throws Exception {
        int count = 3;
        List<UUID> someUsersInDB = createSomeUsersInDB(count);
        UUID idToDelete = someUsersInDB.get(0);

        mvc.perform(delete("/api/v1/user/" + idToDelete))
                .andExpect(status().isOk());

        assertEquals(userRepository.count(), count - 1);
        assertFalse(userRepository.findById(idToDelete).isPresent());
    }

    @Test
    public void delete_WithUserRole_ReturnsForbidden() throws Exception {
        authentificateAsUser();
        int count = 3;
        List<UUID> someUsersInDB = createSomeUsersInDB(count);
        UUID idToDelete = someUsersInDB.get(0);

        mvc.perform(delete("/api/v1/user/" + idToDelete))
                .andExpect(status().isForbidden());

        assertEquals(userRepository.count(), count);
    }

    @Test
    public void getUsers_WithUserRole_ReturnsForbidden() throws Exception {
        authentificateAsUser();
        mvc.perform(get("/api/v1/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUsers_ReturnsAllUsers() {
        authentificateAsAdmin();
        int count = 5;
        List<UUID> someUsersInDB = createSomeUsersInDB(count);

        Page<UserResponseDto> users = userController.getUsers(0, 5, null);

        assertEquals(count ,users.getContent().size());
        for (UserResponseDto user : users.getContent()) {
            assertTrue(someUsersInDB.contains(user.id()));
        }
    }

    @Test
    public void getUsers_WithUsernameFilter_ReturnsNeededUser() {
        authentificateAsAdmin();
        int count = 5;
        createSomeUsersInDB(count);
        UUID individual = saveUserToDB("Vlad", "1234");

        Page<UserResponseDto> users =
                userController.getUsers(0, 5, "Vla");

        assertEquals(1, users.getContent().size());
        assertEquals(individual, users.getContent().get(0).id());
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
