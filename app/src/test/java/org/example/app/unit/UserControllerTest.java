package org.example.app.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.component.JWTUtils;
import org.example.app.controller.UserController;
import org.example.app.dto.user.UserCreateDTO;
import org.example.app.dto.user.UserDTO;
import org.example.app.dto.user.UserUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.handler.GlobalExceptionHandler;
import org.example.app.model.Role;
import org.example.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private final String BASE_URL = "/api/users";
    private final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private UserDTO userDTO;
    private UserCreateDTO createDTO;
    private UserUpdateDTO updateDTO;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(jwtUtils, userService, authenticationManager))
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        userDTO = new UserDTO(
                1L,
                "Bob",
                "Terly",
                "asd@mail.ru",
                Role.USER,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                List.of(1L));
        createDTO = new UserCreateDTO("Bob", "Terly", "asd@mail.ru", "password", Role.USER);
        updateDTO = new UserUpdateDTO("Boby", "Terly", "asd@mail.ru", "pass");
    }

    @Test
    @DisplayName("GET /api/users show all users")
    void getAllUsersTest() throws Exception {
        List<UserDTO> userDTOList = List.of(userDTO);

        given(userService.findAll()).willReturn(userDTOList);

        mockMvc.perform(get(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/users/{id} show users")
    void getUserByIdTest() throws Exception {
        given(userService.findById(anyLong())).willReturn(userDTO);

        mockMvc.perform(get(BASE_URL + "/" + userDTO.getId())
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()));
    }

    @Test
    @DisplayName("POST /api/users create user")
    void createUserTest() throws Exception {
        given(userService.create(any(UserCreateDTO.class)))
                .willReturn(userDTO);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(userDTO.getFirstName()));
    }

    @Test
    @DisplayName("POST /api/users/{id} update user")
    void updateUserTest() throws Exception {
        given(userService.update(anyLong(), any(UserUpdateDTO.class)))
                .willReturn(userDTO);

        mockMvc.perform(post(BASE_URL + "/" + userDTO.getId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userDTO.getFirstName()));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} delete user")
    void deleteUserTest() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/" + userDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/users/{id} show not found user")
    void getUserNotFoundTest() throws Exception {
        given(userService.findById(anyLong()))
                .willThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get(BASE_URL + "/" + userDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/users create user with invalid data")
    void createUserWithInvalidDataTest() throws Exception {
        UserCreateDTO userCreateDTO = new UserCreateDTO(
                "firstName", "lastName", "email", null, Role.USER);

        given(userService.create(any(UserCreateDTO.class)))
                .willThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users/{id} update user with invalid data")
    void updateUserWithInvalidDataTest() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO("firstName", "", "", "");

        given(userService.update(anyLong(), any(UserUpdateDTO.class)))
                .willThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL + "/" + userDTO.getId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} delete user not found")
    void deleteUserNotFoundTest() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/" + userDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }
}
