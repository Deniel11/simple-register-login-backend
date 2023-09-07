package com.schedulemaker.utils;

import com.schedulemaker.entities.User;
import com.schedulemaker.exceptions.InvalidTokenException;
import com.schedulemaker.security.UserDetailsImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
public class JwtUtilTest {

    JwtUtil jwtUtil;

    BeanFactory beanFactory;

    @Autowired
    JwtUtilTest(JwtUtil jwtUtil, BeanFactory beanFactory) {
        this.jwtUtil = jwtUtil;
        this.beanFactory = beanFactory;
    }

    @Test
    void extractAllClaims_withInvalidToken_throwsInvalidTokenException() {
        Assertions.assertThrows(InvalidTokenException.class, () ->  jwtUtil.extractAllClaims("to.ke.nn"));
    }

    @Test
    void extractAllClaims_withValidToken_returnsCorrectClaims() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        String validToken = jwtUtil.generateToken(new UserDetailsImpl(fakeUser));
        Assertions.assertEquals(fakeUser.getUsername(), jwtUtil.extractAllClaims(validToken).getSubject());
    }

    @Test
    void validateToken_withInvalidToken_throwsInvalidTokenException() {
        Assertions.assertThrows(InvalidTokenException.class, () ->  jwtUtil.validateToken("to.ke.nn", new UserDetailsImpl(new User())));
    }

    @Test
    void validateToken_withValidToken_returnTrue() {
        UserDetailsImpl userDetails = new UserDetailsImpl(beanFactory.getBean("fakeUser", User.class));
        String validToken = jwtUtil.generateToken(userDetails);
        Assertions.assertTrue(jwtUtil.validateToken(validToken, userDetails));
    }

    @Test
    void extractTokenFromHeaderAuthorization_withValidHeaderToken_returnsToken() {
        String token = "to.ke.nn";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        Assertions.assertEquals(token, jwtUtil.extractTokenFromHeaderAuthorization(request));
    }

    @Test
    void extractTokenFromHeaderAuthorization_withoutHeaderToken_returnsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Assertions.assertNull(jwtUtil.extractTokenFromHeaderAuthorization(request));
    }
}
