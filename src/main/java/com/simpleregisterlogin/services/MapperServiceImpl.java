package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapperServiceImpl implements MapperService {

    private final ModelMapper modelMapper;

    @Autowired
    public MapperServiceImpl() {
        modelMapper = new ModelMapper();
    }

    @Override
    public UserDTO convertUserToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public RegisteredUserDTO convertUserToRegisteredUserDTO(User user) {
        return modelMapper.map(user, RegisteredUserDTO.class);
    }

    @Override
    public User convertUserDTOtoUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
