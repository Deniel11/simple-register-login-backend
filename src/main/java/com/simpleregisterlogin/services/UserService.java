package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface UserService {

    RegisteredUserDTO addNewUser(UserDTO userDTO);

    String createAuthenticationToken(AuthenticationRequestDTO authenticationRequest);

    void authenticate(AuthenticationRequestDTO authenticationRequest);

    RegisteredUserDTO getUser(Long id);

    RegisteredUserDTO getOwnUser(HttpServletRequest request);
}
