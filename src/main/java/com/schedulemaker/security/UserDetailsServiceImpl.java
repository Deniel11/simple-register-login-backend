package com.schedulemaker.security;

import com.schedulemaker.entities.User;
import com.schedulemaker.exceptions.InvalidTokenException;
import com.schedulemaker.exceptions.UserNotFoundException;
import com.schedulemaker.repositories.UserRepository;
import com.schedulemaker.utils.JwtUtil;
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

        user.orElseThrow(UserNotFoundException::new);

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

    public User getUserByUsernameFromRequest(String authorizationHeader) {
        String username = jwtUtil.extractUsernameFromHeaderAuthorization(authorizationHeader);
        Optional<User> user = userRepository.findUserByUsername(username);
        return user.orElseThrow(() -> new UserNotFoundException(username));
    }
}