package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    RegisteredUserDTO addNewUser(UserDTO userDTO);

    boolean isUsernameTaken(String username);

    boolean isEmailTaken(String email);

    String createAuthenticationToken(AuthenticationRequestDTO authenticationRequest);

    void authenticate(AuthenticationRequestDTO authenticationRequest);

    String checkUserParameters(UserDTO userDTO);
}
