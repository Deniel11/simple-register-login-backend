package com.simpleregisterlogin.controllers;

import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.AuthenticationResponseDTO;
import com.simpleregisterlogin.dtos.MessageDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.services.UserService;
import com.simpleregisterlogin.utils.GeneralUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody UserDTO userDTO) {
        String parameter = userService.checkUserParameters(userDTO);

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

        if (!GeneralUtility.isValidEmail(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new MessageDTO("error", "Wrong email format"));
        }

        if (!GeneralUtility.isValidDate(userDTO.getBirthdate())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new MessageDTO("error", "Accepted date format: dd-mm-yyyy"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addNewUser(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequestDTO authenticationRequest) {
        String invalidParameter = "";
        if (GeneralUtility.isEmptyOrNull(authenticationRequest.getUsername())) {
            String username = "Username";
            if (invalidParameter.length() > 0) {
                invalidParameter += ", " + username;
            } else {
                invalidParameter = username;
            }
        }
        if (GeneralUtility.isEmptyOrNull(authenticationRequest.getPassword())) {
            String password = "Password";
            if (invalidParameter.length() > 0) {
                invalidParameter += ", " + password;
            } else {
                invalidParameter = password;
            }
        }
        if (!invalidParameter.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("error", invalidParameter + " is required."));
        }
        userService.authenticate(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new AuthenticationResponseDTO("ok", userService.createAuthenticationToken(authenticationRequest)));
    }
}
