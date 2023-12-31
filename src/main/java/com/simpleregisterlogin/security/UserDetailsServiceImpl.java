package com.simpleregisterlogin.security;

import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.exceptions.InvalidTokenException;
import com.simpleregisterlogin.exceptions.UserNotActivatedException;
import com.simpleregisterlogin.exceptions.UserNotEnabledException;
import com.simpleregisterlogin.exceptions.UserNotFoundException;
import com.simpleregisterlogin.repositories.UserRepository;
import com.simpleregisterlogin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        User checkValidUser = user.orElseThrow(UserNotFoundException::new);
        if (checkValidUser.getId() == 1L) {
            checkValidUser.setAdmin(true);
            checkValidUser.setVerified(true);
            checkValidUser.setEnabled(true);
            userRepository.save(checkValidUser);
        }
        if (!checkValidUser.getVerified()) {
            throw new UserNotActivatedException();
        }
        if (!checkValidUser.getEnabled()) {
            throw new UserNotEnabledException();
        }
        return user.map(UserDetailsImpl::new).get();
    }

    public UserDetailsImpl extractUserDetailsFromRequest(HttpServletRequest request) {
        String jwt = jwtUtil.extractTokenFromHeaderAuthorization(request.getHeader("Authorization"));
        String username = null;
        if (jwt != null) {
            username = jwtUtil.extractUsername(jwt);
        }
        UserDetailsImpl userDetails = null;
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userDetails = loadUserByUsername(username);
            if (!jwtUtil.validateToken(jwt, userDetails)) {
                throw new InvalidTokenException(jwt);
            }
        }
        return userDetails;
    }

    public Long extractIdFromRequest(HttpServletRequest request) {
        return jwtUtil.extractId(request.getHeader("Authorization"));
    }

    public boolean extractAdminFromRequest(HttpServletRequest request) {
        return jwtUtil.extractAdmin(request.getHeader("Authorization"));
    }
}