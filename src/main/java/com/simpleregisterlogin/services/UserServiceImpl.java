package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.exceptions.*;
import com.simpleregisterlogin.repositories.UserRepository;
import com.simpleregisterlogin.security.UserDetailsImpl;
import com.simpleregisterlogin.security.UserDetailsServiceImpl;
import com.simpleregisterlogin.utils.GeneralUtility;
import com.simpleregisterlogin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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
        checkRegistrationUser(userDTO);
        User user = mapperService.convertUserDTOtoUser(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        return mapperService.convertUserToRegisteredUserDTO(user);
    }

    private boolean isUsernameTaken(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }


    private boolean isEmailTaken(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    @Override
    public String createAuthenticationToken(AuthenticationRequestDTO authenticationRequest) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        return jwtUtil.generateToken(userDetails);
    }

    @Override
    public void authenticate(AuthenticationRequestDTO authenticationRequest) {

        String parameter = checkLoginUserParameters(authenticationRequest);
        if (!parameter.isBlank()) {
            throw new InvalidParameterException(parameter);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            throw new UserNotFoundException();
        }
    }

    private void checkRegistrationUser(UserDTO userDTO) {
        String parameter = checkRegistrationUserParameters(userDTO);
        if (parameter.length() > 0) {
            throw new InvalidParameterException(parameter);
        }

        if (!GeneralUtility.hasLessCharactersThan(userDTO.getPassword(), 8)) {
            throw new LowPasswordLengthException(8);
        }

        if (isUsernameTaken(userDTO.getUsername())) {
            throw new ParameterTakenException("Username");
        }

        if (isEmailTaken(userDTO.getEmail())) {
            throw new ParameterTakenException("Email");
        }

        if (!GeneralUtility.isValidEmail(userDTO.getEmail())) {
            throw new WrongEmailFormatException();
        }

        if (!GeneralUtility.isValidDate(userDTO.getBirthdate())) {
            throw new WrongDateFormatException();
        }
    }

    private String checkRegistrationUserParameters(UserDTO userDTO) {
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

        if (GeneralUtility.isEmptyOrNull(userDTO.getBirthdate())) {
            String birthdate = "Birthday date";
            if (parameter.length() > 0) {
                parameter += ", " + birthdate;
            } else {
                parameter = birthdate;
            }
        }
        return parameter;
    }

    private String checkLoginUserParameters(AuthenticationRequestDTO authenticationRequest) {
        String parameter = "";
        if (GeneralUtility.isEmptyOrNull(authenticationRequest.getUsername())) {
            String username = "Username";
            if (parameter.length() > 0) {
                parameter += ", " + username;
            } else {
                parameter = username;
            }
        }
        if (GeneralUtility.isEmptyOrNull(authenticationRequest.getPassword())) {
            String password = "Password";
            if (parameter.length() > 0) {
                parameter += ", " + password;
            } else {
                parameter = password;
            }
        }
        return parameter;
    }

    @Override
    public RegisteredUserDTO getUser(Long id) {

        if (id == null) {
            throw new InvalidParameterException("id");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return mapperService.convertUserToRegisteredUserDTO(user);
    }

    @Override
    public RegisteredUserDTO getOwnUser(HttpServletRequest request) {
        Long id = userDetailsService.extractIdFromRequest(request);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return mapperService.convertUserToRegisteredUserDTO(user);
    }
}
