package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.UpdateUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.entities.User;

public interface MapperService {

    UserDTO convertUserToUserDTO(User user);

    RegisteredUserDTO convertUserToRegisteredUserDTO(User user);

    User convertUserDTOtoUser(UserDTO userDTO);

    UpdateUserDTO convertUserToUpdateUserDTO(User user);

    User convertUpdateUserDTOToUser(UpdateUserDTO updateUserDTO);
}
