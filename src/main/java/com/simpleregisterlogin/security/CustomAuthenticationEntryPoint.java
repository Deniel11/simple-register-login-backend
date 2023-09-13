package com.simpleregisterlogin.security;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.exceptions.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ResultTextsConfiguration texts;

    @Autowired
    public CustomAuthenticationEntryPoint(ResultTextsConfiguration texts) {
        this.texts = texts;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        throw new InvalidTokenException(texts.getEmptyText());
    }
}
