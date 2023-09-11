package com.simpleregisterlogin.controllers;

import com.simpleregisterlogin.dtos.AuthenticationRequestDTO;
import com.simpleregisterlogin.dtos.AuthenticationResponseDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.services.UserService;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addNewUser(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequestDTO authenticationRequest) {
        userService.authenticate(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new AuthenticationResponseDTO("ok", userService.createAuthenticationToken(authenticationRequest)));
    }
}
