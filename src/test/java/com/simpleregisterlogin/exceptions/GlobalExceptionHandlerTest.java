package com.simpleregisterlogin.exceptions;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.dtos.MessageDTO;
import com.simpleregisterlogin.exceptions.handlers.GlobalExceptionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {

    GlobalExceptionHandler globalExceptionHandler;

    ResultTextsConfiguration texts;

    @Autowired
    public GlobalExceptionHandlerTest(ResultTextsConfiguration texts) {
        this.texts = texts;
        this.globalExceptionHandler = new GlobalExceptionHandler(texts);
    }

    @Test
    void userNotFoundException_WithoutParameter_ReturnExceptedMessage() {
        MessageDTO messageDTO = new MessageDTO(texts.getError(), texts.getUserNotFoundText());
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleUserNotFoundException(new UserNotFoundException()).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void userNotFoundException_WhenGiveUserName_ReturnGivenUserNameAndMessage() {
        String username = "fakeUser";
        MessageDTO errorDTO = new MessageDTO(texts.getError(), texts.getUserNotFoundTextWithMessagePartOne() + " " + username + " " + texts.getUserNotFoundTextWithMessagePartTwo());
        MessageDTO resultErrorDTO = (MessageDTO) globalExceptionHandler.handleUserNotFoundException(new UserNotFoundException(username)).getBody();
        Assertions.assertEquals(errorDTO.getMessage(), resultErrorDTO != null ? resultErrorDTO.getMessage() : null);
    }

    @Test
    void userNotFoundException_WhenGiveId_ReturnGivenIdAndMessage() {
        Long id = 1L;
        MessageDTO errorDTO = new MessageDTO(texts.getError(), texts.getUserNotFoundTextWithLongPartOne() + " " + id + " " + texts.getUserNotFoundTextWithLongPartTwo());
        MessageDTO resultErrorDTO = (MessageDTO) globalExceptionHandler.handleUserNotFoundException(new UserNotFoundException(id)).getBody();
        Assertions.assertEquals(errorDTO.getMessage(), resultErrorDTO != null ? resultErrorDTO.getMessage() : null);
    }

    @Test
    void invalidTokenException_WithToken_ReturnGivenTokenAndMessage() {
        String token = "[THIS IS TOKEN]";
        MessageDTO messageDTO = new MessageDTO(texts.getError(), texts.getInvalidTokenText() + " " + token);
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleInvalidTokenException(new InvalidTokenException(token)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void invalidParameterException_WithParameter_ReturnGivenParameterAndMessage() {
        String parameter = "[THIS IS PARAMETER]";
        MessageDTO messageDTO = new MessageDTO(texts.getError(), parameter + " " + texts.getInvalidParameterText());
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleInvalidParameterException(new InvalidParameterException(parameter)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void lowPasswordLengthException_WithLength_ReturnGivenLengthAndMessage() {
        int length = 8;
        MessageDTO messageDTO = new MessageDTO(texts.getError(), texts.getLowPasswordLengthTextPartOne() + " " + length + " " + texts.getLowPasswordLengthTextPartTwo());
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleLowPasswordLengthException(new LowPasswordLengthException(length)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void parameterTakenException_WithParameter_ReturnGivenParameterAndMessage() {
        String parameter = "[THIS IS PARAMETER]";
        MessageDTO messageDTO = new MessageDTO(texts.getError(), parameter + " " + texts.getParameterTakenText());
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleParameterTakenException(new ParameterTakenException(parameter)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void wrongEmailFormatException_WithoutParameter_ReturnExceptedMessage() {
        MessageDTO messageDTO = new MessageDTO(texts.getError(), texts.getWrongEmailFormatText());
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleWrongEmailFormatException(new WrongEmailFormatException()).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void wrongDateFormatException_WithoutParameter_ReturnExceptedMessage() {
        MessageDTO messageDTO = new MessageDTO(texts.getError(), texts.getWrongDateFormatText());
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleWrongDateFormatException(new WrongDateFormatException()).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void customAccessDeniedException_WithoutParameter_ReturnExceptedMessage() {
        MessageDTO messageDTO = new MessageDTO(texts.getError(), texts.getAccessDeniedText());
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleCustomAccessDeniedException(new CustomAccessDeniedException()).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void customAccessDeniedException_WithParameter_ReturnGivenParameterAndMessage() {
        String parameter = "[THIS IS PARAMETER]";
        MessageDTO messageDTO = new MessageDTO(texts.getError(), texts.getAccessDeniedText() + ": " + parameter);
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleCustomAccessDeniedException(new CustomAccessDeniedException(parameter)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }

    @Test
    void parameterMatchException_WithParameter_ReturnGivenParameterAndMessage() {
        String parameter = "[THIS IS PARAMETER]";
        MessageDTO messageDTO = new MessageDTO(texts.getError(), parameter + " " + texts.getParameterMatchText());
        MessageDTO resultMessageDTO = (MessageDTO) globalExceptionHandler.handleParameterMatchException(new ParameterMatchException(parameter)).getBody();
        Assertions.assertEquals(messageDTO.getMessage(), resultMessageDTO != null ? resultMessageDTO.getMessage() : null);
    }
}
