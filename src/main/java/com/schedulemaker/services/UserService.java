package com.schedulemaker.services;

import com.schedulemaker.dtos.RegisteredUserDTO;
import com.schedulemaker.dtos.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    RegisteredUserDTO addNewUser(UserDTO userDTO);

    boolean isUsernameTaken(String username);

    boolean isEmailTaken(String email);
}