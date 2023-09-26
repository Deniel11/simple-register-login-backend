package com.simpleregisterlogin.security;

import com.simpleregisterlogin.exceptions.CustomAccessDeniedException;
import com.simpleregisterlogin.exceptions.UserNotEnabledException;
import com.simpleregisterlogin.exceptions.UserNotFoundException;
import com.simpleregisterlogin.utils.GeneralUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    private UserDetailsService userDetailsService;

    @Autowired
    public CustomAuthenticationEntryPoint(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        String authorizationHeader = request.getHeader("Authorization");
        if (GeneralUtility.isEmptyOrNull(authorizationHeader)) {
            resolver.resolveException(request, response, null, new CustomAccessDeniedException());
        } else {
            try {
                UserDetails userDetails =  userDetailsService.loadUserByUsername("Sanyi");
                if (!userDetails.isEnabled()) {
                    resolver.resolveException(request, response, null, new UserNotEnabledException());
                }
            } catch (UserNotFoundException exception) {
                resolver.resolveException(request, response, null, new CustomAccessDeniedException());
            }
        }
    }
}
