package com.schedulemaker.services;

import com.schedulemaker.dtos.AuthenticationRequestDTO;
import com.schedulemaker.dtos.RegisteredUserDTO;
import com.schedulemaker.dtos.UserDTO;
import com.schedulemaker.entities.User;
import com.schedulemaker.exceptions.UserNotFoundException;
import com.schedulemaker.repositories.UserRepository;
import com.schedulemaker.security.UserDetailsImpl;
import com.schedulemaker.security.UserDetailsServiceImpl;
import com.schedulemaker.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder;

    private UserDetailsServiceImpl userDetailsService;

    private JwtUtil jwtUtil;

    private AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public RegisteredUserDTO addNewUser(UserDTO userDTO) {
        User user = new User(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getBirthdate());
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return new RegisteredUserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getBirthdate(), user.isAdmin(), user.isValid());
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return !userRepository.findUserByUsername(username).isEmpty();
    }

    @Override
    public boolean isEmailTaken(String email) {
        return !userRepository.findUserByEmail(email).isEmpty();
    }

    @Override
    public String createAuthenticationToken(AuthenticationRequestDTO authenticationRequest) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        return jwtUtil.generateToken(userDetails);
    }

    @Override
    public void authenticate(AuthenticationRequestDTO authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            throw new UserNotFoundException();
        }
    }
}