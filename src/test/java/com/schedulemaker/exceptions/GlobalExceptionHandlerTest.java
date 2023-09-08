package com.schedulemaker.exceptions;

import com.schedulemaker.dtos.MessageDTO;
import com.schedulemaker.exceptions.handlers.GlobalExceptionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GlobalExceptionHandlerTest {

    GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void userNotFoundException_WhenGiveUserName_ReturnGivenUserNameAndMessage() {
        String username = "fakeUser";
        MessageDTO errorDTO = new MessageDTO("error", "User with name " + username + " is not found");
        MessageDTO resultErrorDTO = (MessageDTO) globalExceptionHandler.handleUserNotFoundException(new UserNotFoundException(username)).getBody();
        Assertions.assertEquals(errorDTO.getMessage(), resultErrorDTO.getMessage());
    }

    @Test
    void invalidTokenException_WithToken_ReturnGivenTokenAndMessage() {
        String token = "[THIS IS TOKEN]";
        MessageDTO messageDTO = new MessageDTO("error", "Invalid token: " + token);
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleInvalidTokenException(new InvalidTokenException(token)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }
}
