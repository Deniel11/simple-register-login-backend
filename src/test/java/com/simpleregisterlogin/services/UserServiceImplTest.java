package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.RegisteredUserDTO;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

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
        UserDTO userDTO = new UserDTO(fakeUser.getUsername(), fakeUser.getEmail(), fakeUser.getPassword(), fakeUser.getBirthdate());
        RegisteredUserDTO registeredUserDTO = new RegisteredUserDTO(1L, fakeUser.getUsername(), fakeUser.getEmail(), fakeUser.getPassword(), fakeUser.getBirthdate(), fakeUser.getAdmin(), fakeUser.getValid());
        Mockito.when(mapperService.convertUserDTOtoUser(userDTO)).thenReturn(fakeUser);
        Mockito.when(mapperService.convertUserToRegisteredUserDTO(fakeUser)).thenReturn(registeredUserDTO);
        Mockito.when(encoder.encode(fakeUser.getPassword())).thenReturn(fakeUser.getPassword());

        Assertions.assertEquals(registeredUserDTO.getUsername(), userService.addNewUser(userDTO).getUsername());
        Assertions.assertEquals(registeredUserDTO.getEmail(), userService.addNewUser(userDTO).getEmail());
        Assertions.assertEquals(registeredUserDTO.getPassword(), userService.addNewUser(userDTO).getPassword());
        Assertions.assertEquals(registeredUserDTO.getBirthdate(), userService.addNewUser(userDTO).getBirthdate());
        Assertions.assertEquals(registeredUserDTO.getAdmin(), userService.addNewUser(userDTO).getAdmin());
        Assertions.assertEquals(registeredUserDTO.getValid(), userService.addNewUser(userDTO).getValid());
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
}
