package com.simpleregisterlogin.exceptions;

import com.simpleregisterlogin.dtos.MessageDTO;
import com.simpleregisterlogin.exceptions.handlers.GlobalExceptionHandler;
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
    void userNotFoundException_WithoutParameter_ReturnExceptedMessage() {
        MessageDTO messageDTO = new MessageDTO("error", "Username or password is incorrect.");
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleUserNotFoundException(new UserNotFoundException()).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void userNotFoundException_WhenGiveUserName_ReturnGivenUserNameAndMessage() {
        String username = "fakeUser";
        MessageDTO errorDTO = new MessageDTO("error", "User with name " + username + " is not found");
        MessageDTO resultErrorDTO = (MessageDTO) globalExceptionHandler.handleUserNotFoundException(new UserNotFoundException(username)).getBody();
        Assertions.assertEquals(errorDTO.getMessage(), resultErrorDTO.getMessage());
    }

    @Test
    void userNotFoundException_WhenGiveId_ReturnGivenIdAndMessage() {
        Long id = 1L;
        MessageDTO errorDTO = new MessageDTO("error", "User with id " + id + " is not found");
        MessageDTO resultErrorDTO = (MessageDTO) globalExceptionHandler.handleUserNotFoundException(new UserNotFoundException(id)).getBody();
        Assertions.assertEquals(errorDTO.getMessage(), resultErrorDTO.getMessage());
    }

    @Test
    void invalidTokenException_WithToken_ReturnGivenTokenAndMessage() {
        String token = "[THIS IS TOKEN]";
        MessageDTO messageDTO = new MessageDTO("error", "Invalid token: " + token);
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleInvalidTokenException(new InvalidTokenException(token)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void invalidParameterException_WithParameter_ReturnGivenParameterAndMessage() {
        String parameter = "[THIS IS PARAMETER]";
        MessageDTO messageDTO = new MessageDTO("error", parameter + " is required");
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleInvalidParameterException(new InvalidParameterException(parameter)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void lowPasswordLengthException_WithLength_ReturnGivenLengthAndMessage() {
        int length = 8;
        MessageDTO messageDTO = new MessageDTO("error", "Password must be " + length + " characters.");
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleLowPasswordLengthException(new LowPasswordLengthException(length)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void parameterTakenException_WithParameter_ReturnGivenParameterAndMessage() {
        String parameter = "[THIS IS PARAMETER]";
        MessageDTO messageDTO = new MessageDTO("error", parameter + " is already taken");
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleParameterTakenException(new ParameterTakenException(parameter)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void wrongEmailFormatException_WithoutParameter_ReturnExceptedMessage() {
        MessageDTO messageDTO = new MessageDTO("error", "Wrong email format");
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleWrongEmailFormatException(new WrongEmailFormatException()).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void wrongDateFormatException_WithoutParameter_ReturnExceptedMessage() {
        MessageDTO messageDTO = new MessageDTO("error", "Accepted date format: dd-mm-yyyy");
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleWrongDateFormatException(new WrongDateFormatException()).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void customAccessDeniedException_WithoutParameter_ReturnExceptedMessage() {
        MessageDTO messageDTO = new MessageDTO("error", "Access Denied");
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleCustomAccessDeniedException(new CustomAccessDeniedException()).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void customAccessDeniedException_WithParameter_ReturnGivenParameterAndMessage() {
        String parameter = "[THIS IS PARAMETER]";
        MessageDTO messageDTO = new MessageDTO("error", "Access Denied: " + parameter);
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleCustomAccessDeniedException(new CustomAccessDeniedException(parameter)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }

    @Test
    void parameterMatchException_WithParameter_ReturnGivenParameterAndMessage() {
        String parameter = "[THIS IS PARAMETER]";
        MessageDTO messageDTO = new MessageDTO("error", parameter + " parameter is already same");
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleParameterMatchException(new ParameterMatchException(parameter)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO.getMessage());
    }
}
