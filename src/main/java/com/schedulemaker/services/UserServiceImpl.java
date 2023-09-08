package com.schedulemaker.services;

import com.schedulemaker.dtos.AuthenticationRequestDTO;
import com.schedulemaker.dtos.RegisteredUserDTO;
import com.schedulemaker.dtos.UserDTO;
import com.schedulemaker.entities.User;
import com.schedulemaker.exceptions.UserNotFoundException;
import com.schedulemaker.repositories.UserRepository;
import com.schedulemaker.security.UserDetailsImpl;
import com.schedulemaker.security.UserDetailsServiceImpl;
import com.schedulemaker.utils.GeneralUtility;
import com.schedulemaker.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    private final MapperService mapperService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager, MapperService mapperService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.mapperService = mapperService;
    }

    @Override
    public RegisteredUserDTO addNewUser(UserDTO userDTO) {
        User user = mapperService.convertUserDTOtoUser(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        return mapperService.convertUserToRegisteredUserDTO(user);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.findUserByEmail(email).isPresent();
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

    @Override
    public String checkUserParameters(UserDTO userDTO) {
        String parameter = "";
        if (GeneralUtility.isEmptyOrNull(userDTO.getUsername())) {
            String username = "Username";
            if (parameter.length() > 0) {
                parameter += ", " + username;
            } else {
                parameter = username;
            }
        }

        if (GeneralUtility.isEmptyOrNull(userDTO.getEmail())) {
            String email = "Email";
            if (parameter.length() > 0) {
                parameter += ", " + email;
            } else {
                parameter = email;
            }
        }

        if (GeneralUtility.isEmptyOrNull(userDTO.getPassword())) {
            String password = "Password";
            if (parameter.length() > 0) {
                parameter += ", " + password;
            } else {
                parameter = password;
            }
        }

        if (GeneralUtility.isEmptyOrNull(String.valueOf(userDTO.getBirthdate()))) {
            String birthdate = "Birthday date";
            if (parameter.length() > 0) {
                parameter += ", " + birthdate;
            } else {
                parameter = birthdate;
            }
        }
        return parameter;
    }
}
