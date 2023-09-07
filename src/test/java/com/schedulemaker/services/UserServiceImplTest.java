package com.schedulemaker.services;

import com.schedulemaker.dtos.AuthenticationRequestDTO;
import com.schedulemaker.dtos.RegisteredUserDTO;
import com.schedulemaker.dtos.UserDTO;
import com.schedulemaker.entities.User;
import com.schedulemaker.repositories.UserRepository;
import com.schedulemaker.security.UserDetailsImpl;
import com.schedulemaker.security.UserDetailsServiceImpl;
import com.schedulemaker.utils.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
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

    BeanFactory beanFactory;

    @Autowired
    public UserServiceImplTest(BeanFactory beanFactory) {
        userRepository = Mockito.mock(UserRepository.class);
        encoder = Mockito.mock(BCryptPasswordEncoder.class);
        userDetailsService = Mockito.mock(UserDetailsServiceImpl.class);
        jwtUtil = Mockito.mock(JwtUtil.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userService = new UserServiceImpl(userRepository, encoder, userDetailsService, jwtUtil, authenticationManager);
        this.beanFactory = beanFactory;
    }

    @Test
    void addNewUser_withValidUserDTO_returnCorrectRegisteredUserDTO() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setValid(false);
        UserDTO userDTO = new UserDTO(fakeUser.getUsername(), fakeUser.getEmail(), fakeUser.getPassword(), fakeUser.getBirthdate());
        RegisteredUserDTO registeredUserDTO = new RegisteredUserDTO(1L, fakeUser.getUsername(), fakeUser.getEmail(), fakeUser.getPassword(), fakeUser.getBirthdate(), fakeUser.getAdmin(), fakeUser.getValid());
        Mockito.when(encoder.encode(fakeUser.getPassword())).thenReturn(fakeUser.getPassword());

        Assertions.assertEquals(registeredUserDTO.getUsername(), userService.addNewUser(userDTO).getUsername());
        Assertions.assertEquals(registeredUserDTO.getEmail(), userService.addNewUser(userDTO).getEmail());
        Assertions.assertEquals(registeredUserDTO.getPassword(), userService.addNewUser(userDTO).getPassword());
        Assertions.assertEquals(registeredUserDTO.getBirthdate(), userService.addNewUser(userDTO).getBirthdate());
        Assertions.assertEquals(registeredUserDTO.isAdmin(), userService.addNewUser(userDTO).isAdmin());
        Assertions.assertEquals(registeredUserDTO.isValid(), userService.addNewUser(userDTO).isValid());
    }

    @Test
    void isUsernameTaken_withUniqueUsername_returnFalse() {
        String fakeUsername = "fakeUser";
        Mockito.when(userRepository.findUserByUsername(fakeUsername)).thenReturn(Optional.empty());
        Assertions.assertFalse(userService.isUsernameTaken(fakeUsername));
    }

    @Test
    void isUsernameTaken_withTakenUsername_returnTrue() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(userRepository.findUserByUsername(fakeUser.getUsername())).thenReturn(Optional.of(fakeUser));
        Assertions.assertTrue(userService.isUsernameTaken(fakeUser.getUsername()));
    }

    @Test
    void isEmailTaken_withUniqueEmail_returnFalse() {
        String fakeEmail = "fake@mail.com";
        Mockito.when(userRepository.findUserByEmail(fakeEmail)).thenReturn(Optional.empty());
        Assertions.assertFalse(userService.isEmailTaken(fakeEmail));
    }

    @Test
    void isEmailTaken_withTakenEmail_returnTrue() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(userRepository.findUserByEmail(fakeUser.getEmail())).thenReturn(Optional.of(fakeUser));
        Assertions.assertTrue(userService.isEmailTaken(fakeUser.getEmail()));
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
}
