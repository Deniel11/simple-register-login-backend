package com.simpleregisterlogin.conrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.repositories.UserRepository;
import com.simpleregisterlogin.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {
    MockMvc mockMvc;

    UserRepository userRepository;

    JwtUtil jwtUtil;

    ObjectMapper mapper;

    BeanFactory beanFactory;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, UserRepository userRepository, JwtUtil jwtUtil, BeanFactory beanFactory) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        mapper = new ObjectMapper();
        this.beanFactory = beanFactory;
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void registration_AddNewUserWithGivenData_ReturnsNewUser() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(fakeUserDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(fakeUserDTO.getEmail()))
                .andExpect(jsonPath("$.birthdate").value(String.valueOf(fakeUserDTO.getBirthdate())));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void registration_WithLessThanEightCharacterPassword_ReturnsExpectedErrorMessage() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setPassword("1234567");
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Password must be 8 characters."));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void registration_WithExistingUsername_ReturnsExpectedErrorMessage() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void registration_WithExistingEmail_ReturnsExpectedErrorMessage() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setUsername("otherFakeUser");
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Email is already taken"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void registration_WithWrongEmailFormat_ReturnsExpectedErrorMessage() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setEmail("wrong email format");
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Wrong email format"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void registration_WithWrongDateFormat_ReturnsExpectedErrorMessage() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setBirthdate("01-01-2000000");
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Accepted date format: dd-mm-yyyy"));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void createAuthenticationToken_LoginWithCorrectCredentials_ReturnsOkAndToken() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO(fakeUser.getUsername(), fakeUser.getPassword());
        String authenticationRequestDTOJson = mapper.writeValueAsString(authenticationRequestDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authenticationRequestDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void createAuthenticationToken_LoginWithIncorrectCredentials_ReturnsExceptedError() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setUsername("otherFakeUser");
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO(fakeUser.getUsername(), fakeUser.getPassword());
        String authenticationRequestDTOJson = mapper.writeValueAsString(authenticationRequestDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authenticationRequestDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Username or password is incorrect."));
    }
}
