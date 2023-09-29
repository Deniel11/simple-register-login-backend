package com.simpleregisterlogin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.dtos.UpdateUserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccessIntegrationTest {

    MockMvc mockMvc;

    ResultTextsConfiguration texts;

    BeanFactory beanFactory;

    ObjectMapper mapper;

    @Autowired
    public AccessIntegrationTest(MockMvc mockMvc, ResultTextsConfiguration texts, BeanFactory beanFactory) {
        this.mockMvc = mockMvc;
        this.texts = texts;
        this.beanFactory = beanFactory;
        mapper = new ObjectMapper();
    }

    @Test
    void getUser_WithoutToken_ThrowsAccessDeniedException() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/{id}", 2);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getAccessDeniedText()));
    }

    @Test
    void getOwnUser_WithoutToken_ThrowsAccessDeniedException() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getAccessDeniedText()));
    }

    @Test
    void getUsers_WithoutToken_ThrowsAccessDeniedException() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/users");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getAccessDeniedText()));
    }

    @Test
    void updateUser_WithoutToken_ThrowsAccessDeniedException() throws Exception {
        UpdateUserDTO updateUserDTO = beanFactory.getBean("fakeUpdateUserDTO", UpdateUserDTO.class);
        String updateUserDTOJSON = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getAccessDeniedText()));
    }
}
