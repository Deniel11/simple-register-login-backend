package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.RegisteredUserDTOList;
import com.simpleregisterlogin.dtos.UserDTO;
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

    @Autowired
    public UserServiceImplTest(BeanFactory beanFactory) {
        userRepository = Mockito.mock(UserRepository.class);
        encoder = Mockito.mock(BCryptPasswordEncoder.class);
        userDetailsService = Mockito.mock(UserDetailsServiceImpl.class);
        jwtUtil = Mockito.mock(JwtUtil.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        mapperService = Mockito.mock(MapperService.class);
        userService = new UserServiceImpl(userRepository, encoder, userDetailsService, jwtUtil, authenticationManager, mapperService);
        this.beanFactory = beanFactory;
    }

    @Test
    void addNewUser_WithValidUserDTO_ReturnCorrectRegisteredUserDTO() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setValid(false);
        UserDTO userDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        RegisteredUserDTO fakeRegisteredUserDTO = beanFactory.getBean("fakeRegisteredUserDTO", RegisteredUserDTO.class);
        Mockito.when(mapperService.convertUserDTOtoUser(userDTO)).thenReturn(fakeUser);
        Mockito.when(mapperService.convertUserToRegisteredUserDTO(fakeUser)).thenReturn(fakeRegisteredUserDTO);
        Mockito.when(encoder.encode(fakeUser.getPassword())).thenReturn(fakeUser.getPassword());

        Assertions.assertEquals(fakeRegisteredUserDTO.getUsername(), userService.addNewUser(userDTO).getUsername());
        Assertions.assertEquals(fakeRegisteredUserDTO.getEmail(), userService.addNewUser(userDTO).getEmail());
        Assertions.assertEquals(fakeRegisteredUserDTO.getBirthdate(), userService.addNewUser(userDTO).getBirthdate());
        Assertions.assertEquals(fakeRegisteredUserDTO.getAdmin(), userService.addNewUser(userDTO).getAdmin());
        Assertions.assertEquals(fakeRegisteredUserDTO.getValid(), userService.addNewUser(userDTO).getValid());
    }

    @Test
    void addNewUser_WithMissingParameter_ThrowsInvalidParameterException() {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setUsername("");

        Assertions.assertThrows(InvalidParameterException.class, () -> userService.addNewUser(fakeUserDTO));
    }

    @Test
    void addNewUser_WithLowPasswordLength_ThrowsLowPasswordLengthException() {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setPassword("pass");

        Assertions.assertThrows(LowPasswordLengthException.class, () -> userService.addNewUser(fakeUserDTO));
    }

    @Test
    void addNewUser_WithTakenUsername_ThrowsParameterTakenException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        Mockito.when(userRepository.findUserByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.of(fakeUser));

        Assertions.assertThrows(ParameterTakenException.class, () -> userService.addNewUser(fakeUserDTO));
    }

    @Test
    void addNewUser_WithTakenEmail_ThrowsParameterTakenException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setUsername("otherFakeUser");
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        Mockito.when(userRepository.findUserByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.of(fakeUser));

        Assertions.assertThrows(ParameterTakenException.class, () -> userService.addNewUser(fakeUserDTO));
    }

    @Test
    void addNewUser_WithWrongEmailFormat_ThrowsWrongEmailFormatException() {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setEmail("email@email");
        Mockito.when(userRepository.findUserByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(WrongEmailFormatException.class, () -> userService.addNewUser(fakeUserDTO));
    }

    @Test
    void addNewUser_WithWrongDateFormat_ThrowsWrongDateFormatException() {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setBirthdate("10.10.2000000");
        Mockito.when(userRepository.findUserByUsername(fakeUserDTO.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findUserByEmail(fakeUserDTO.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(WrongDateFormatException.class, () -> userService.addNewUser(fakeUserDTO));
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
}
