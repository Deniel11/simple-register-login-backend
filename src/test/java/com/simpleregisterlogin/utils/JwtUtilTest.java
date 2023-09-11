package com.simpleregisterlogin.utils;

import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.exceptions.InvalidTokenException;
import com.simpleregisterlogin.security.UserDetailsImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

    JwtUtil jwtUtil;

    BeanFactory beanFactory;

    @Autowired
    JwtUtilTest(JwtUtil jwtUtil, BeanFactory beanFactory) {
        this.jwtUtil = jwtUtil;
        this.beanFactory = beanFactory;
    }

    @Test
    void extractAllClaims_WithInvalidToken_ThrowsInvalidTokenException() {
        Assertions.assertThrows(InvalidTokenException.class, () ->  jwtUtil.extractAllClaims("to.ke.nn"));
    }

    @Test
    void extractAllClaims_WithValidToken_ReturnsCorrectClaims() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        String validToken = jwtUtil.generateToken(new UserDetailsImpl(fakeUser));
        Assertions.assertEquals(fakeUser.getUsername(), jwtUtil.extractAllClaims(validToken).getSubject());
    }

    @Test
    void validateToken_WithInvalidToken_ThrowsInvalidTokenException() {
        Assertions.assertThrows(InvalidTokenException.class, () ->  jwtUtil.validateToken("to.ke.nn", new UserDetailsImpl(new User())));
    }

    @Test
    void validateToken_WithValidToken_ReturnTrue() {
        UserDetailsImpl userDetails = new UserDetailsImpl(beanFactory.getBean("fakeUser", User.class));
        String validToken = jwtUtil.generateToken(userDetails);
        Assertions.assertTrue(jwtUtil.validateToken(validToken, userDetails));
    }

    @Test
    void extractTokenFromHeaderAuthorization_WithValidHeaderToken_ReturnsToken() {
        String token = "to.ke.nn";
        Assertions.assertEquals(token, jwtUtil.extractTokenFromHeaderAuthorization("Bearer " + token));
    }

    @Test
    void extractTokenFromHeaderAuthorization_WithoutHeaderToken_ReturnsNull() {
        Assertions.assertNull(jwtUtil.extractTokenFromHeaderAuthorization(null));
    }
}
