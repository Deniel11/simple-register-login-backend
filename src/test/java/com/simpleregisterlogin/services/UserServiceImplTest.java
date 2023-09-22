package com.simpleregisterlogin.services;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.dtos.*;
import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.exceptions.*;
import com.simpleregisterlogin.repositories.UserRepository;
import com.simpleregisterlogin.security.UserDetailsImpl;
import com.simpleregisterlogin.security.UserDetailsServiceImpl;
import com.simpleregisterlogin.utils.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceImplTest {

    UserRepository userRepository;

    BCryptPasswordEncoder encoder;

    UserDetailsServiceImpl userDetailsService;

    JwtUtil jwtUtil;

    AuthenticationManager authenticationManager;

    UserServiceImpl userService;

    MapperService mapperService;

    BeanFactory beanFactory;

    ResultTextsConfiguration texts;

    @Autowired
    public UserServiceImplTest(BeanFactory beanFactory, ResultTextsConfiguration texts) {
        userRepository = Mockito.mock(UserRepository.class);
        encoder = Mockito.mock(BCryptPasswordEncoder.class);
        userDetailsService = Mockito.mock(UserDetailsServiceImpl.class);
        jwtUtil = Mockito.mock(JwtUtil.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        mapperService = Mockito.mock(MapperService.class);
        this.texts = texts;
        userService = new UserServiceImpl(userRepository, encoder, userDetailsService, jwtUtil, authenticationManager, mapperService, texts);
        this.beanFactory = beanFactory;
    }

    @Test
    void addNewUser_WithValidUserDTO_ReturnCorrectRegisteredUserDTO() {
        String verificationToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setVerified(false);
        UserDTO userDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        RegisteredUserDTO fakeRegisteredUserDTO = beanFactory.getBean("fakeRegisteredUserDTO", RegisteredUserDTO.class);
        Mockito.when(mapperService.convertUserDTOtoUser(userDTO)).thenReturn(fakeUser);
        Mockito.when(mapperService.convertUserToRegisteredUserDTO(fakeUser)).thenReturn(fakeRegisteredUserDTO);
        Mockito.when(encoder.encode(fakeUser.getPassword())).thenReturn(fakeUser.getPassword());

        Assertions.assertEquals(fakeRegisteredUserDTO.getUsername(), userService.addNewUser(userDTO, verificationToken).getUsername());
        Assertions.assertEquals(fakeRegisteredUserDTO.getEmail(), userService.addNewUser(userDTO, verificationToken).getEmail());
        Assertions.assertEquals(fakeRegisteredUserDTO.getDateOfBirth(), userService.addNewUser(userDTO, verificationToken).getDateOfBirth());
        Assertions.assertEquals(fakeRegisteredUserDTO.getAdmin(), userService.addNewUser(userDTO, verificationToken).getAdmin());
        Assertions.assertEquals(fakeRegisteredUserDTO.getVerified(), userService.addNewUser(userDTO, verificationToken).getVerified());
    }

    @Test
    void addNewUser_WithMissingParameter_ThrowsInvalidParameterException() {
        String verificationToken = "token";
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setUsername("");

        Assertions.assertThrows(InvalidParameterException.class, () -> userService.addNewUser(fakeUserDTO, verificationToken));
    }

    @Test
    void addNewUser_WithLowPasswordLength_ThrowsLowPasswordLengthException() {
        String verificationToken = "token";
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setPassword("pass");

        Assertions.assertThrows(LowPasswordLengthException.class, () -> userService.addNewUser(fakeUserDTO, verificationToken));
    }

    @Test
    void addNewUser_WithTakenUsername_ThrowsParameterTakenException() {
        String verificationToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        Mockito.when(userRepository.findUserByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.of(fakeUser));

        Assertions.assertThrows(ParameterTakenException.class, () -> userService.addNewUser(fakeUserDTO, verificationToken));
    }

    @Test
    void addNewUser_WithTakenEmail_ThrowsParameterTakenException() {
        String verificationToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setUsername("otherFakeUser");
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        Mockito.when(userRepository.findUserByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.of(fakeUser));

        Assertions.assertThrows(ParameterTakenException.class, () -> userService.addNewUser(fakeUserDTO, verificationToken));
    }

    @Test
    void addNewUser_WithWrongEmailFormat_ThrowsWrongEmailFormatException() {
        String verificationToken = "token";
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setEmail("email@email");
        Mockito.when(userRepository.findUserByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(WrongEmailFormatException.class, () -> userService.addNewUser(fakeUserDTO, verificationToken));
    }

    @Test
    void addNewUser_WithWrongDateFormat_ThrowsWrongDateFormatException() {
        String verificationToken = "token";
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setDateOfBirth("10.10.2000000");
        Mockito.when(userRepository.findUserByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(WrongDateFormatException.class, () -> userService.addNewUser(fakeUserDTO, verificationToken));
    }

    @Test
    void createAuthenticationToken_WithValidRequest_ReturnsValidString() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO(fakeUser.getUsername(), fakeUser.getPassword());
        UserDetailsImpl userDetails = new UserDetailsImpl(fakeUser);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aSIsImlzQWRtaW4iOmZhbHNlLCJleHAiOjE2OTQxMjYwMDEsImlhdCI6MTY5NDA5MDAwMX0.K_8Bh0DvZvwH2USMfb2kTfhkymdFBK9MIwsLiHqpg_A";
        Mockito.when(userDetailsService.loadUserByUsername(fakeUser.getUsername())).thenReturn(userDetails);
        Mockito.when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        Assertions.assertEquals(token, userService.createAuthenticationToken(authenticationRequest));
    }

    @Test
    void authenticate_WithWrongCredentials_ThrowsUserNotFoundException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO(fakeUser.getUsername(), fakeUser.getPassword());
        Mockito.when(authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())))
                .thenThrow(BadCredentialsException.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.authenticate(authenticationRequest));
    }

    @Test
    void getUser_WithValidIdAndUser_ReturnExpectedRegisteredUserDTO() {
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        RegisteredUserDTO fakeRegisteredUserDTO = beanFactory.getBean("fakeRegisteredUserDTO", RegisteredUserDTO.class);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(fakeUser));
        Mockito.when(mapperService.convertUserToRegisteredUserDTO(fakeUser)).thenReturn(fakeRegisteredUserDTO);

        Assertions.assertEquals(fakeRegisteredUserDTO, userService.getUser(id));
    }

    @Test
    void getUser_WithIdNull_ThrowsInvalidParameterException() {
        Assertions.assertThrows(InvalidParameterException.class, () -> userService.getUser(null));
    }

    @Test
    void getUser_WithNotExistedId_ThrowsUserNotFoundException() {
        Long id = 99L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUser(id));
    }

    @Test
    void getOwnUser_WithExistedUser_ReturnExpectedRegisteredUserDTO() {
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        RegisteredUserDTO fakeRegisteredUserDTO = beanFactory.getBean("fakeRegisteredUserDTO", RegisteredUserDTO.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(fakeUser));
        Mockito.when(mapperService.convertUserToRegisteredUserDTO(fakeUser)).thenReturn(fakeRegisteredUserDTO);

        Assertions.assertEquals(fakeRegisteredUserDTO, userService.getOwnUser(request));
    }

    @Test
    void getOwnUser_WithNotExistedId_ThrowsUserNotFoundException() {
        Long id = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getOwnUser(request));
    }

    @Test
    void getUsers_WithCorrectUserList_ReturnsExceptedRegisteredUserDTOList() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        List<User> userList = new ArrayList<>();
        userList.add(fakeUser);
        RegisteredUserDTO fakeRegisteredUserDTO = beanFactory.getBean("fakeRegisteredUserDTO", RegisteredUserDTO.class);
        RegisteredUserDTOList registeredUserDTOList = new RegisteredUserDTOList();
        registeredUserDTOList.getUsers().add(fakeRegisteredUserDTO);
        Mockito.when(userRepository.findAll()).thenReturn(userList);
        Mockito.when(mapperService.convertUserToRegisteredUserDTO(fakeUser)).thenReturn(fakeRegisteredUserDTO);

        Assertions.assertEquals(registeredUserDTOList.getUsers(), userService.getUsers().getUsers());
    }

    @Test
    void updateUser_WithAdminRole_ReturnExceptedRegisteredUserDTO() {
        String email = "new@email.com";
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        User editedFakeUser = beanFactory.getBean("fakeUser", User.class);
        editedFakeUser.setEmail(email);
        RegisteredUserDTO fakeRegisteredUserDTO = beanFactory.getBean("fakeRegisteredUserDTO", RegisteredUserDTO.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail(email);

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.save(editedFakeUser)).thenReturn(editedFakeUser);
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedFakeUser.getPassword())).thenReturn(false);
        Mockito.when(mapperService.convertUserToRegisteredUserDTO(fakeUser)).thenReturn(fakeRegisteredUserDTO);

        Assertions.assertEquals(fakeRegisteredUserDTO, userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndWrongActualUserId_ThrowsUserNotFoundException() {
        Long id = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndWrongOtherUserId_ThrowsUserNotFoundException() {
        Long id = 1L;
        Long otherUserId = 2L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(otherUserId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(otherUserId, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndSameUsername_ThrowsParameterMatchException() {
        Long id = 1L;
        User adminUser = beanFactory.getBean("fakeUser", User.class);
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername(editedUser.getUsername());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(adminUser));
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndSameEmail_ThrowsParameterMatchException() {
        Long id = 1L;
        User adminUser = beanFactory.getBean("fakeUser", User.class);
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail(editedUser.getEmail());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(adminUser));
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndSamePassword_ThrowsParameterMatchException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword(editedUser.getPassword());
        editedUser.setPassword(beanFactory.getBean("fakeEncodedPassword", String.class));

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(true);

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndSameDateOfBirth_ThrowsParameterMatchException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setDateOfBirth(editedUser.getDateOfBirth());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndSameAdminParameter_ThrowsParameterMatchException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setAdmin(editedUser.getAdmin());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndSameValidParameter_ThrowsParameterMatchException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setVerified(editedUser.getVerified());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndTakenUsername_ThrowsParameterTakenException() {
        Long id = 1L;
        Long otherId = 2L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername(editedUser.getUsername());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(otherId);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);
        Mockito.when(userRepository.findUserByUsername(editedUser.getUsername())).thenReturn(Optional.of(editedUser));

        Assertions.assertThrows(ParameterTakenException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndTakenEmail_ThrowsParameterTakenException() {
        Long id = 1L;
        Long otherId = 2L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail(editedUser.getEmail());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(otherId);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);
        Mockito.when(userRepository.findUserByUsername(editedUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(editedUser.getEmail())).thenReturn(Optional.of(editedUser));

        Assertions.assertThrows(ParameterTakenException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndWrongEmailFormat_ThrowsWrongEmailFormatException() {
        Long id = 1L;
        Long otherId = 2L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail("wrong@email");

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(otherId);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);
        Mockito.when(userRepository.findUserByUsername(editedUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(editedUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(WrongEmailFormatException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndLowPasswordLength_ThrowsLowPasswordLengthException() {
        Long id = 1L;
        Long otherId = 2L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword("pass");

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(otherId);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);
        Mockito.when(userRepository.findUserByUsername(editedUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(editedUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(LowPasswordLengthException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithAdminRoleAndWrongDateFormat_ThrowsWrongDateFormatException() {
        Long id = 1L;
        Long otherId = 2L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setDateOfBirth("10-10-200000");

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(otherId);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(true);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);
        Mockito.when(userRepository.findUserByUsername(editedUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(editedUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(WrongDateFormatException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRole_ReturnExceptedRegisteredUserDTO() {
        String email = "new@email.com";
        Long id = 1L;
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        User editedFakeUser = beanFactory.getBean("fakeUser", User.class);
        editedFakeUser.setEmail(email);
        RegisteredUserDTO fakeRegisteredUserDTO = beanFactory.getBean("fakeRegisteredUserDTO", RegisteredUserDTO.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail(email);

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(false);
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(fakeUser));
        Mockito.when(userRepository.save(editedFakeUser)).thenReturn(fakeUser);
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedFakeUser.getPassword())).thenReturn(false);
        Mockito.when(mapperService.convertUserToRegisteredUserDTO(fakeUser)).thenReturn(fakeRegisteredUserDTO);

        Assertions.assertEquals(fakeRegisteredUserDTO, userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRoleAndWrongActualUserId_ThrowsUserNotFoundException() {
        Long id = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRoleAndSameUsername_ThrowsParameterMatchException() {
        Long id = 1L;
        User adminUser = beanFactory.getBean("fakeUser", User.class);
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername(editedUser.getUsername());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(adminUser));
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRoleAndSameEmail_ThrowsParameterMatchException() {
        Long id = 1L;
        User adminUser = beanFactory.getBean("fakeUser", User.class);
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail(editedUser.getEmail());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(adminUser));
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRoleAndSamePassword_ThrowsParameterMatchException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword(editedUser.getPassword());
        editedUser.setPassword(beanFactory.getBean("fakeEncodedPassword", String.class));

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(true);

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRoleAndSameDateOfBirth_ThrowsParameterMatchException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setDateOfBirth(editedUser.getDateOfBirth());

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);

        Assertions.assertThrows(ParameterMatchException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRoleAndWrongEmailFormat_ThrowsWrongEmailFormatException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail("wrong@email");

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);
        Mockito.when(userRepository.findUserByUsername(editedUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(editedUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(WrongEmailFormatException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRoleAndLowPasswordLength_ThrowsLowPasswordLengthException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPassword("pass");

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);
        Mockito.when(userRepository.findUserByUsername(editedUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(editedUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(LowPasswordLengthException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }

    @Test
    void updateUser_WithUserRoleAndWrongDateFormat_ThrowsWrongDateFormatException() {
        Long id = 1L;
        User editedUser = beanFactory.getBean("fakeUser", User.class);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setDateOfBirth("10-10-200000");

        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(userDetailsService.extractIdFromRequest(request)).thenReturn(id);
        Mockito.when(userDetailsService.extractAdminFromRequest(request)).thenReturn(false);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(editedUser));
        Mockito.when(encoder.matches(updateUserDTO.getPassword(), editedUser.getPassword())).thenReturn(false);
        Mockito.when(userRepository.findUserByUsername(editedUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(editedUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(WrongDateFormatException.class, () -> userService.updateUser(id, updateUserDTO, request));
    }
}
