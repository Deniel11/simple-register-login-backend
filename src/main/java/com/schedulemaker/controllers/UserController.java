package com.schedulemaker.controllers;

import com.schedulemaker.dtos.MessageDTO;
import com.schedulemaker.dtos.UserDTO;
import com.schedulemaker.services.UserService;
import com.schedulemaker.utils.GeneralUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody UserDTO userDTO) {
        String parameter = "";
        if (GeneralUtility.isEmptyOrNull(userDTO.getUsername())) {
            parameter = "Username";
        } else if (GeneralUtility.isEmptyOrNull(userDTO.getEmail())) {
            parameter = "Email";
        } else if (GeneralUtility.isEmptyOrNull(userDTO.getPassword())) {
            parameter = "Password";
        } else if (GeneralUtility.isEmptyOrNull(String.valueOf(userDTO.getBirthdate()))) {
            parameter = "Kingdom";
        }

        if (parameter.length() > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("error", parameter + " is required."));
        }

        if (!GeneralUtility.hasLessCharactersThan(userDTO.getPassword(), 8)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new MessageDTO("error", "Password must be 8 characters."));
        }

        if (userService.isUsernameTaken(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageDTO("error", "Username is already taken"));
        }

        if (userService.isEmailTaken(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageDTO("error", "Email is already taken"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addNewUser(userDTO));
    }
}
