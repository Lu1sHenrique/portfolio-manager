package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dto.request.ExternalMemberRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.enums.MemberRole;
import com.portfolio.security.JwtService;
import com.portfolio.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.portfolio.config.TestSecurityConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExternalMemberController.class)
@Import(TestSecurityConfig.class)
class ExternalMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /api/external/members - Should create member")
    void shouldCreateMember() throws Exception {
        ExternalMemberRequest request = ExternalMemberRequest.builder()
                .name("New Employee")
                .role(MemberRole.EMPLOYEE)
                .build();

        MemberResponse response = MemberResponse.builder()
                .id(1L)
                .name("New Employee")
                .role(MemberRole.EMPLOYEE)
                .externalId("ext-123")
                .build();

        when(memberService.createFromExternal(any(ExternalMemberRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/external/members")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Employee"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    @DisplayName("POST /api/external/members - Should return 400 for invalid request")
    void shouldReturn400ForInvalidRequest() throws Exception {
        ExternalMemberRequest request = ExternalMemberRequest.builder().build();

        mockMvc.perform(post("/api/external/members")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/external/members - Should return all members")
    void shouldReturnAllMembers() throws Exception {
        MemberResponse member = MemberResponse.builder()
                .id(1L)
                .name("Employee")
                .role(MemberRole.EMPLOYEE)
                .build();

        when(memberService.findAll()).thenReturn(List.of(member));

        mockMvc.perform(get("/api/external/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Employee"));
    }

    @Test
    @DisplayName("GET /api/external/members/{id} - Should return member by id")
    void shouldReturnMemberById() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .id(1L)
                .name("Employee")
                .role(MemberRole.EMPLOYEE)
                .build();

        when(memberService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/external/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Employee"));
    }
}
