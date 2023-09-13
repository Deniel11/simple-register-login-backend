package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface UserService {

    RegisteredUserDTO addNewUser(UserDTO userDTO);

    String createAuthenticationToken(AuthenticationRequestDTO authenticationRequest);

    void authenticate(AuthenticationRequestDTO authenticationRequest);

    RegisteredUserDTO getUser(Long id);

    RegisteredUserDTO getOwnUser(HttpServletRequest request);

    RegisteredUserDTOList getUsers();

    RegisteredUserDTO updateUser(Long id, UpdateUserDTO updateUserDTO, HttpServletRequest request);
}
