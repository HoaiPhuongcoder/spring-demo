package com.hoaiphuong.demo.controller;

import com.hoaiphuong.demo.common.exception.BusinessException;
import com.hoaiphuong.demo.common.exception.ErrorCode;
import com.hoaiphuong.demo.common.i18n.Messages;
import com.hoaiphuong.demo.config.i18n.LocaleConfig;
import com.hoaiphuong.demo.dto.user.CreateUserRequest;
import com.hoaiphuong.demo.dto.user.UpdateUserRequest;
import com.hoaiphuong.demo.dto.user.UserResponse;
import com.hoaiphuong.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({LocaleConfig.class, Messages.class})
class UserControllerTest {

    private static final UUID ID = UUID.fromString("8be4df61-93ca-11d2-aa0d-00e098032b8c");
    private static final UserResponse USER = new UserResponse(ID, "hoaiphuong", "hoaiphuong@gmail.com");

    @Autowired
    MockMvc mvc;

    @MockitoBean
    UserService userService;

    @Test
    void getById_returnsWrappedUser() throws Exception {
        when(userService.getUserById(ID)).thenReturn(USER);

        mvc.perform(get("/api/v1/users/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ID.toString()))
                .andExpect(jsonPath("$.data.email").value("hoaiphuong@gmail.com"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(userService.getUserById(ID)).thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        mvc.perform(get("/api/v1/users/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void getById_notFound_messageFollowsAcceptLanguage() throws Exception {
        when(userService.getUserById(ID)).thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        mvc.perform(get("/api/v1/users/{id}", ID).header("Accept-Language", "vi"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy người dùng"));
    }

    @Test
    void getById_invalidUuid_returns400() throws Exception {
        mvc.perform(get("/api/v1/users/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void list_returnsPageWithMeta() throws Exception {
        when(userService.findAllUserByName(eq("phuong"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(USER), PageRequest.of(0, 20), 1));

        mvc.perform(get("/api/v1/users").param("name", "phuong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("hoaiphuong"))
                .andExpect(jsonPath("$.meta.totalElements").value(1))
                .andExpect(jsonPath("$.meta.hasNext").value(false));
    }

    @Test
    void list_sizeTooLarge_returns400() throws Exception {
        mvc.perform(get("/api/v1/users").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value("size"));
    }

    @Test
    void lookup_notFound_returns404() throws Exception {
        when(userService.getUserByNameAndEmail("ghost", "ghost@mail.com"))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        mvc.perform(get("/api/v1/users/lookup").param("name", "ghost").param("email", "ghost@mail.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void create_returns201() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(USER);

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"hoaiphuong\",\"email\":\"hoaiphuong@gmail.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ID.toString()));
    }

    @Test
    void create_missingFields_returns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[?(@.code == 'name')]").exists())
                .andExpect(jsonPath("$.errors[?(@.code == 'email')]").exists());
        verify(userService, never()).createUser(any());
    }

    @Test
    void create_duplicateEmail_returns409() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.EMAIL_EXISTED));

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"hoaiphuong\",\"email\":\"hoaiphuong@gmail.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_EXISTED"));
    }

    @Test
    void update_returnsUpdatedUser() throws Exception {
        UserResponse updated = new UserResponse(ID, "newname", "new@mail.com");
        when(userService.updateUser(eq(ID), any(UpdateUserRequest.class))).thenReturn(updated);

        mvc.perform(put("/api/v1/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"newname\",\"email\":\"new@mail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("newname"));
    }

    @Test
    void delete_returnsOk() throws Exception {
        mvc.perform(delete("/api/v1/users/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        verify(userService).deleteUser(ID);
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND)).when(userService).deleteUser(ID);

        mvc.perform(delete("/api/v1/users/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }
}
