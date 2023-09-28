package com.simpleregisterlogin.conrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.dtos.*;
import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.repositories.UserRepository;
import com.simpleregisterlogin.security.UserDetailsImpl;
import com.simpleregisterlogin.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    ResultTextsConfiguration texts;

    BCryptPasswordEncoder encoder;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, UserRepository userRepository, JwtUtil jwtUtil, BeanFactory beanFactory, ResultTextsConfiguration texts, BCryptPasswordEncoder encoder) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        mapper = new ObjectMapper();
        this.beanFactory = beanFactory;
        this.texts = texts;
        this.encoder = encoder;
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
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(fakeUserDTO.getDateOfBirth())));
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
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getLowPasswordLengthTextPartOne() + " " + 8 + " " + texts.getLowPasswordLengthTextPartTwo()));
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
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getUsernameText() + " " + texts.getParameterTakenText()));
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
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getEmailText() + " " + texts.getParameterTakenText()));
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
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getWrongEmailFormatText()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void registration_WithWrongDateFormat_ReturnsExpectedErrorMessage() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setDateOfBirth("01-01-2000000");
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getWrongDateFormatText()));
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
                .andExpect(jsonPath("$.status").value(texts.getOk()))
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
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getUserNotFoundText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void getUser_WithValidId_ReturnExceptedUser() throws Exception {
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/{id}", 2)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(fakeUser.getUsername()))
                .andExpect(jsonPath("$.email").value(fakeUser.getEmail()))
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(fakeUser.getDateOfBirth())))
                .andExpect(jsonPath("$.admin").value(fakeUser.getAdmin()))
                .andExpect(jsonPath("$.verified").value(fakeUser.getVerified()))
                .andExpect(jsonPath("$.enabled").value(fakeUser.getEnabled()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void getUser_WithInvalidId_ThrowsUserNotFoundException() throws Exception {
        Long id = 99L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getUserNotFoundTextWithLongPartOne() + " " + id + " " + texts.getUserNotFoundTextWithLongPartTwo()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void getOwnUser_WithValidId_ReturnExceptedUser() throws Exception {
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(2L);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(fakeUser.getUsername()))
                .andExpect(jsonPath("$.email").value(fakeUser.getEmail()))
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(fakeUser.getDateOfBirth())))
                .andExpect(jsonPath("$.admin").value(fakeUser.getAdmin()))
                .andExpect(jsonPath("$.verified").value(fakeUser.getVerified()))
                .andExpect(jsonPath("$.enabled").value(fakeUser.getEnabled()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void getOwnUser_WithInvalidId_ThrowsUserNotFoundException() throws Exception {
        Long id = 1000L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setId(id);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getUserNotFoundTextWithLongPartOne() + " " + id + " " + texts.getUserNotFoundTextWithLongPartTwo()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void getUsers_WithValidUsers_ReturnExceptedRegisteredUserDTOList() throws Exception {
        Long id = 2L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/users")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users.size()").value(2))
                .andExpect(jsonPath("$.users[1].id").value(id))
                .andExpect(jsonPath("$.users[1].username").value(fakeUser.getUsername()))
                .andExpect(jsonPath("$.users[1].email").value(fakeUser.getEmail()))
                .andExpect(jsonPath("$.users[1].dateOfBirth").value(String.valueOf(fakeUser.getDateOfBirth())))
                .andExpect(jsonPath("$.users[1].admin").value(fakeUser.getAdmin()))
                .andExpect(jsonPath("$.users[1].verified").value(fakeUser.getVerified()))
                .andExpect(jsonPath("$.users[1].enabled").value(fakeUser.getEnabled()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUser_ReturnExceptedRegisteredUserDTO() throws Exception {
        Long id = 2L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("OtherUsername");
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value(updateUserDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(fakeUser.getEmail()))
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(fakeUser.getDateOfBirth())))
                .andExpect(jsonPath("$.admin").value(fakeUser.getAdmin()))
                .andExpect(jsonPath("$.verified").value(fakeUser.getVerified()))
                .andExpect(jsonPath("$.enabled").value(fakeUser.getEnabled()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUserAndSameUsername_ThrowExceptedErrorMessage() throws Exception {
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername(fakeUser.getUsername());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getUsernameText() + " " + texts.getParameterMatchText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUserAndSameEmail_ThrowExceptedErrorMessage() throws Exception {
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail(fakeUser.getEmail());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getEmailText() + " " + texts.getParameterMatchText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUserAndSamePassword_ThrowExceptedErrorMessage() throws Exception {
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword(fakeUser.getPassword());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getPasswordText() + " " + texts.getParameterMatchText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUserAndSameDateOfBirth_ThrowExceptedErrorMessage() throws Exception {
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setDateOfBirth(fakeUser.getDateOfBirth());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getDateOfBirthText() + " " + texts.getParameterMatchText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUserAndSameAdmin_ThrowExceptedErrorMessage() throws Exception {
        Long id = 2L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setAdmin(fakeUser.getAdmin());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getIsAdminText() + " " + texts.getParameterMatchText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUserAndSameValid_ThrowExceptedErrorMessage() throws Exception {
        Long id = 2L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setVerified(fakeUser.getVerified());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getValidText() + " " + texts.getParameterMatchText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithAdminUserAndEditingOtherUserWithSameUsername_ThrowExceptedErrorMessage() throws Exception {
        Long id = 2L;
        Long otherUserId = 1L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername(fakeUser.getUsername());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", otherUserId)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getUsernameText() + " " + texts.getParameterTakenText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithAdminUserAndEditingOtherUserWithSameEmail_ThrowExceptedErrorMessage() throws Exception {
        Long id = 2L;
        Long otherUserId = 1L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail(fakeUser.getEmail());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", otherUserId)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getEmailText() + " " + texts.getParameterTakenText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithAdminUserAndEditingOtherUserWithWrongEmailFormat_ThrowExceptedErrorMessage() throws Exception {
        Long id = 2L;
        Long otherUserId = 1L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail("wrong@email");
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", otherUserId)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getWrongEmailFormatText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithAdminUserAndEditingOtherUserWithLowPasswordLength_ThrowExceptedErrorMessage() throws Exception {
        Long id = 2L;
        Long otherUserId = 1L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword("pass");
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", otherUserId)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getLowPasswordLengthTextPartOne() + " " + 8 + " " + texts.getLowPasswordLengthTextPartTwo()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithAdminUserAndEditingOtherUserWithWrongDateFormat_ThrowExceptedErrorMessage() throws Exception {
        Long id = 2L;
        Long otherUserId = 1L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setDateOfBirth("10.10.20000");
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", otherUserId)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getWrongDateFormatText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_users.sql"})
    void verifyEmailAddress_WithValidToken_ReturnOkStatusAndVerifyMessage() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/verify-email?token={token}", "token");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getOk()))
                .andExpect(jsonPath("$.message").value(texts.getBeenVerifyText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void verifyEmailAddress_WithAlreadyVerifiedUser_ThrowExceptedErrorMessage() throws Exception {
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/verify-email?token={token}", fakeUser.getVerificationToken());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getUserAlreadyVerifiedText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void verifyEmailAddress_WithInvalidToken_ThrowExceptedErrorMessage() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/verify-email?token={token}", "invalidToken");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getInvalidTokenText() + "."));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void forgotPassword_WithValidUserAndEmail_ReturnsExceptedMessage() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail(fakeUser.getEmail());
        String emailDTOJSON = mapper.writeValueAsString(emailDTO);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emailDTOJSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getOk()))
                .andExpect(jsonPath("$.message").value(texts.getChangePasswordSentText()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void forgotPassword_WithInvalidEmail_ThrowsEmailAddressNotFoundException() throws Exception {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail("invalid@email.com");
        String emailDTOJSON = mapper.writeValueAsString(emailDTO);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emailDTOJSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getEmailAddressNotFoundPartOneText() + " " + emailDTO.getEmail() + " " + texts.getEmailAddressNotFoundPartTwoText()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void changePassword_WithValidUser_ReturnsExceptedMessage() throws Exception {
        String forgotPasswordToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        PasswordDTO fakePasswordDTO = beanFactory.getBean("fakePasswordDTO", PasswordDTO.class);
        fakePasswordDTO.setOldPassword(fakeUser.getPassword());
        String fakePasswordDTOJSON = mapper.writeValueAsString(fakePasswordDTO);
        fakeUser.setForgotPasswordToken(forgotPasswordToken);
        fakeUser.setPassword(encoder.encode(fakeUser.getPassword()));
        userRepository.save(fakeUser);
        MockHttpServletRequestBuilder requestBuilder = patch("/api/user/change-password?token={token}", forgotPasswordToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(fakePasswordDTOJSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getOk()))
                .andExpect(jsonPath("$.message").value(texts.getPasswordChangedText()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void changePassword_WithInvalidPasswordParameter_ThrowsInvalidParameterException() throws Exception {
        String forgotPasswordToken = "token";
        PasswordDTO fakePasswordDTO = beanFactory.getBean("fakePasswordDTO", PasswordDTO.class);
        fakePasswordDTO.setOldPassword(null);
        String fakePasswordDTOJSON = mapper.writeValueAsString(fakePasswordDTO);
        MockHttpServletRequestBuilder requestBuilder = patch("/api/user/change-password?token={token}", forgotPasswordToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(fakePasswordDTOJSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getOldPasswordText() + " " + texts.getInvalidParameterText()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void changePassword_WithLowPasswordLength_ThrowsLowPasswordLengthException() throws Exception {
        String forgotPasswordToken = "token";
        PasswordDTO fakePasswordDTO = beanFactory.getBean("fakePasswordDTO", PasswordDTO.class);
        fakePasswordDTO.setNewPassword("pass");
        String fakePasswordDTOJSON = mapper.writeValueAsString(fakePasswordDTO);
        MockHttpServletRequestBuilder requestBuilder = patch("/api/user/change-password?token={token}", forgotPasswordToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(fakePasswordDTOJSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getLowPasswordLengthTextPartOne() + " 8 " + texts.getLowPasswordLengthTextPartTwo()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void changePassword_WithInvalidToken_ThrowsInvalidTokenException() throws Exception {
        String forgotPasswordToken = "token";
        PasswordDTO fakePasswordDTO = beanFactory.getBean("fakePasswordDTO", PasswordDTO.class);
        String fakePasswordDTOJSON = mapper.writeValueAsString(fakePasswordDTO);
        MockHttpServletRequestBuilder requestBuilder = patch("/api/user/change-password?token={token}", forgotPasswordToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(fakePasswordDTOJSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getInvalidTokenText() + "."));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_users.sql"})
    void changePassword_WithIncorrectPassword_ThrowsPasswordIncorrectException() throws Exception {
        String forgotPasswordToken = "token";
        PasswordDTO fakePasswordDTO = beanFactory.getBean("fakePasswordDTO", PasswordDTO.class);
        String fakePasswordDTOJSON = mapper.writeValueAsString(fakePasswordDTO);
        MockHttpServletRequestBuilder requestBuilder = patch("/api/user/change-password?token={token}", forgotPasswordToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(fakePasswordDTOJSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(texts.getError()))
                .andExpect(jsonPath("$.message").value(texts.getOldPasswordText() + " " + texts.getPasswordIncorrectText()));
    }
}
