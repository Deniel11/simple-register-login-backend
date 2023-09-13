package com.simpleregisterlogin.conrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.UpdateUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
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
        fakeUserDTO.setDateOfBirth("01-01-2000000");
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

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void getUser_WithValidId_ReturnExceptedUser() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/{id}", 1)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(fakeUser.getUsername()))
                .andExpect(jsonPath("$.email").value(fakeUser.getEmail()))
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(fakeUser.getDateOfBirth())))
                .andExpect(jsonPath("$.admin").value(fakeUser.getAdmin()))
                .andExpect(jsonPath("$.valid").value(fakeUser.getValid()));
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
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User with id " + id + " is not found"));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void getOwnUser_WithValidId_ReturnExceptedUser() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setId(1L);
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
                .andExpect(jsonPath("$.valid").value(fakeUser.getValid()));
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
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User with id " + id + " is not found"));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void getUsers_WithValidUsers_ReturnExceptedRegisteredUserDTOList() throws Exception {
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setId(id);
        MockHttpServletRequestBuilder requestBuilder = get("/api/user/users")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users.size()").value(2))
                .andExpect(jsonPath("$.users[0].id").value(id))
                .andExpect(jsonPath("$.users[0].username").value(fakeUser.getUsername()))
                .andExpect(jsonPath("$.users[0].email").value(fakeUser.getEmail()))
                .andExpect(jsonPath("$.users[0].dateOfBirth").value(String.valueOf(fakeUser.getDateOfBirth())))
                .andExpect(jsonPath("$.users[0].admin").value(fakeUser.getAdmin()))
                .andExpect(jsonPath("$.users[0].valid").value(fakeUser.getValid()));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUser_ReturnExceptedRegisteredUserDTO() throws Exception {
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("OtherUsername");
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value(updateUserDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(fakeUser.getEmail()))
                .andExpect(jsonPath("$.dateOfBirth").value(String.valueOf(fakeUser.getDateOfBirth())))
                .andExpect(jsonPath("$.admin").value(fakeUser.getAdmin()))
                .andExpect(jsonPath("$.valid").value(fakeUser.getValid()));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Username parameter is already same"));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Email parameter is already same"));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Password parameter is already same"));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Date of Birth parameter is already same"));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("isAdmin parameter is already same"));
    }

    @Test
    @Sql({"/db/test/clear_tables.sql", "/db/test/insert_fakeUser.sql"})
    void updateUser_WithValidUserAndSameValid_ThrowExceptedErrorMessage() throws Exception {
        Long id = 2L;
        User fakeUser = beanFactory.getBean("fakeAdminUser", User.class);
        fakeUser.setId(id);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setValid(fakeUser.getValid());
        String updateUserDTOJson = mapper.writeValueAsString(updateUserDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/api/user/{id}", id)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("valid parameter is already same"));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Username is already taken"));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Email is already taken"));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Wrong email format"));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Password must be 8 characters."));
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
                .content(updateUserDTOJson);;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Accepted date format: dd-mm-yyyy"));
    }
}
