package com.simpleregisterlogin.controllers;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.dtos.*;
import com.simpleregisterlogin.services.EmailService;
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

    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, ResultTextsConfiguration texts, EmailService emailService) {
        this.userService = userService;
        this.texts = texts;
        this.emailService = emailService;
    }

    @PostMapping("/registration")
    public ResponseEntity<RegisteredUserDTO> registration(@RequestBody UserDTO userDTO) {
        String token = emailService.getToken();
        RegisteredUserDTO registeredUserDTO = userService.addNewUser(userDTO, token);
        emailService.sendVerificationRequest(userDTO.getUsername(), userDTO.getEmail(), token);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUserDTO);
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

    @GetMapping("/verify-email")
    public ResponseEntity<MessageDTO> verifyEmailAddress(@RequestParam String token) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new MessageDTO(texts.getOk(), userService.verifyUser(token)));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<MessageDTO> changePassword(@RequestParam String token, @RequestBody PasswordDTO passwordDTO) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new MessageDTO(texts.getOk(), userService.changePassword(token, passwordDTO)));
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<MessageDTO> forgotPassword(@RequestBody EmailDTO emailDTO) {
        String token = emailService.getToken();
        String name = userService.saveToken(emailDTO.getEmail(), token);;
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new MessageDTO(texts.getOk(), emailService.sendChangePasswordRequest(name, emailDTO.getEmail(), token)));
    }
}
