package ru.hard2code.gisdbapi.controller;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import ru.hard2code.gisdbapi.exception.EntityNotFoundException;
import ru.hard2code.gisdbapi.model.User;
import ru.hard2code.gisdbapi.model.UserType;
import ru.hard2code.gisdbapi.service.user.UserService;
import ru.hard2code.gisdbapi.service.userType.UserTypeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
class UserControllerTest extends AbstractControllerTest {

    private static final String API_PATH = "/api/users";
    private final UserType CITIZEN = new UserType(UserType.Type.CITIZEN.getValue());
    private final UserType EMPLOYEE = new UserType(UserType.Type.GOVERNMENT_EMPLOYEE.getValue());
    private final User TEST_USER = new User("123456789", "test@test.ru",
            "+79994446655",
            "username", "firstName", CITIZEN);

    @Autowired
    private UserService userService;

    @Autowired
    private UserTypeService userTypeService;

    @BeforeEach
    void beforeEach() {
        userTypeService.createRole(CITIZEN);
        userTypeService.createRole(EMPLOYEE);
    }

    @AfterEach
    void cleanup() {
        userService.deleteAllUsers();
        userTypeService.deleteAllRoles();
    }

    @Test
    void testFindById() throws Exception {
        userService.createUser(TEST_USER);
        mvc.perform(get(API_PATH + "/{id}", TEST_USER.getId()).accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().string(OBJECT_MAPPER.writeValueAsString(TEST_USER)));
    }

    @Test
    void testFindAll() throws Exception {
        var users = List.of(
                new User("123123123", "test@test1.ru", "+79994446651",
                        "username1", "firstName1", CITIZEN),
                new User("432432432", "test@test2.ru", "+79994446652",
                        "username2", "firstName2", EMPLOYEE)
        );

        userService.createUser(users.get(0));
        userService.createUser(users.get(1));

        mvc.perform(get(API_PATH).accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().string(OBJECT_MAPPER.writeValueAsString(users)));
    }

    @Test
    void testDeleteById() throws Exception {
        userService.createUser(TEST_USER);

        mvc.perform(delete(API_PATH + "/{id}", TEST_USER.getId()).accept(CONTENT_TYPE))
                .andExpect(status().isNoContent());

        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(TEST_USER.getId()));
    }

    @Test
    void testCreate() throws Exception {
        mvc.perform(post(API_PATH).contentType(CONTENT_TYPE)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_USER))
                .accept(CONTENT_TYPE)).andExpect(status().isOk());
    }

    @Test
    void testUpdate() throws Exception {
        var user = userService.createUser(TEST_USER);

        user.setChatId("999999999");
        user.setUserType(EMPLOYEE);
        user.setUserName("newUserName");
        user.setPhone("+79994443399");
        user.setEmail("newemail@test.com");
        user.setFirstName("firstNameNew");

        mvc.perform(put(API_PATH + "/{id}", user.getId())
                        .contentType(CONTENT_TYPE)
                        .content(OBJECT_MAPPER.writeValueAsString(user))
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatId").value(user.getChatId()))
                .andExpect(jsonPath("$.userType.name").value(user.getUserType()
                        .getName()))
                .andExpect(jsonPath("$.userName").value(user.getUserName()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

}
