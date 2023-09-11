package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.entities.User;

public interface MapperService {

    public UserDTO convertUserToUserDTO(User user);

    RegisteredUserDTO convertUserToRegisteredUserDTO(User user);

    public User convertUserDTOtoUser(UserDTO userDTO);
}
