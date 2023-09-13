package com.simpleregisterlogin.controllers;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.dtos.*;
import com.simpleregisterlogin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    private final ResultTextsConfiguration texts;

    @Autowired
    public UserController(UserService userService, ResultTextsConfiguration texts) {
        this.userService = userService;
        this.texts = texts;
    }

    @PostMapping("/registration")
    public ResponseEntity<RegisteredUserDTO> registration(@RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addNewUser(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> createAuthenticationToken(@RequestBody AuthenticationRequestDTO authenticationRequest) {
        userService.authenticate(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new AuthenticationResponseDTO(texts.getOk(), userService.createAuthenticationToken(authenticationRequest)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisteredUserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    @GetMapping("/")
    public ResponseEntity<RegisteredUserDTO> getOwnUser(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getOwnUser(request));
    }

    @GetMapping("/users")
    public ResponseEntity<RegisteredUserDTOList> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegisteredUserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, updateUserDTO, request));
    }
}
