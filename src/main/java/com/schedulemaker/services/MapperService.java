package com.schedulemaker.services;

import com.schedulemaker.dtos.RegisteredUserDTO;
import com.schedulemaker.dtos.UserDTO;
import com.schedulemaker.entities.User;

public interface MapperService {

    public UserDTO convertUserToUserDTO(User user);

    RegisteredUserDTO convertUserToRegisteredUserDTO(User user);

    public User convertUserDTOtoUser(UserDTO userDTO);
}
