package com.boram.look.controller;

import com.boram.look.SecurityConfigTestBean;
import com.boram.look.api.controller.UserController;
import com.boram.look.api.dto.user.UserDto;
import com.boram.look.global.config.SecurityConfig;
import com.boram.look.global.security.JwtProvider;
import com.boram.look.service.auth.EmailVerificationService;
import com.boram.look.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import({SecurityConfig.class, UserControllerBeanConfig.class, SecurityConfigTestBean.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Test
    void joinUser_200OK() throws Exception {
        // given
        UserDto.Save dto = UserDto.Save.builder()
                .email("test@example.com")
                .password("password123!")
                .nickname("testuse")
                .build();

        String userId = "user-123";
        given(userService.joinUser(any(UserDto.Save.class))).willReturn(userId);

        // when & then
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/user/" + userId))
                .andExpect(content().string("회원 가입 완료"));

        then(emailVerificationService).should().isVerifiedEmail("test@example.com");
        then(userService).should().joinUser(any(UserDto.Save.class));
    }
}

