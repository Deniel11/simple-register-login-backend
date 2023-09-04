package com.schedulemaker.services;

import com.schedulemaker.dtos.RegisteredUserDTO;
import com.schedulemaker.dtos.UserDTO;
import com.schedulemaker.entities.User;
import com.schedulemaker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceIml implements UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceIml(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public RegisteredUserDTO addNewUser(UserDTO userDTO) {
        User user = new User(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getBirthdate());
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return new RegisteredUserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getBirthdate(), user.isAdmin(), user.isValid());
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return !userRepository.findUserByUsername(username).isEmpty();
    }

    @Override
    public boolean isEmailTaken(String email) {
        return !userRepository.findUserByEmail(email).isEmpty();
    }
}
