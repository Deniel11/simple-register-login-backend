package com.simpleregisterlogin.exceptions.handlers;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.dtos.MessageDTO;
import com.simpleregisterlogin.exceptions.*;
import com.simpleregisterlogin.utils.GeneralUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RestControllerAdvice
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {

    private final ResultTextsConfiguration texts;

    @Autowired
    public GlobalExceptionHandler(ResultTextsConfiguration texts) {
        this.texts = texts;
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        String message;
        if (GeneralUtility.isEmptyOrNull(exception.getMessage())) {
            message = texts.getUserNotFoundText();
        } else if (exception.isMessage()) {
            message = texts.getUserNotFoundTextWithMessagePartOne() + " " + exception.getMessage() + " " + texts.getUserNotFoundTextWithMessagePartTwo();
        } else {
            message = texts.getUserNotFoundTextWithLongPartOne() + " " + exception.getMessage() + " " + texts.getUserNotFoundTextWithLongPartTwo();
        }
        return new ResponseEntity<>(new MessageDTO(texts.getError(), message), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException exception) {
        String message = texts.getInvalidTokenText();
        if (GeneralUtility.isEmptyOrNull(exception.getMessage())) {
            message += ".";
        } else {
            message += ": " + exception.getMessage();
        }
        return new ResponseEntity<>(new MessageDTO(texts.getError(), message), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = InvalidParameterException.class)
    public ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), exception.getMessage() + " " + texts.getInvalidParameterText()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = LowPasswordLengthException.class)
    public ResponseEntity<Object> handleLowPasswordLengthException(LowPasswordLengthException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), texts.getLowPasswordLengthTextPartOne() + " " + exception.getMessage() + " " + texts.getLowPasswordLengthTextPartTwo()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = ParameterTakenException.class)
    public ResponseEntity<Object> handleParameterTakenException(ParameterTakenException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), exception.getMessage() + " " + texts.getParameterTakenText()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = WrongEmailFormatException.class)
    public ResponseEntity<Object> handleWrongEmailFormatException(WrongEmailFormatException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), texts.getWrongEmailFormatText()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = WrongDateFormatException.class)
    public ResponseEntity<Object> handleWrongDateFormatException(WrongDateFormatException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), texts.getWrongDateFormatText()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = CustomAccessDeniedException.class)
    public ResponseEntity<Object> handleCustomAccessDeniedException(CustomAccessDeniedException exception) {
        String message = texts.getAccessDeniedText();
        if (!GeneralUtility.isEmptyOrNull(exception.getMessage())) {
            message += ": " + exception.getMessage();
        }
        return new ResponseEntity<>(new MessageDTO(texts.getError(), message), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ParameterMatchException.class)
    public ResponseEntity<Object> handleParameterMatchException(ParameterMatchException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), exception.getMessage() + " " + texts.getParameterMatchText()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserNotActivatedException.class)
    public ResponseEntity<Object> handleUserNotActivatedException(UserNotActivatedException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), texts.getUserNotActivatedText()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = UserNotEnabledException.class)
    public ResponseEntity<Object> handleUserNotEnabledException(UserNotEnabledException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), texts.getUserNotEnabledText()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = BuildEmailMessageException.class)
    public ResponseEntity<Object> handleBuildEmailMessageException(BuildEmailMessageException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), texts.getBuildEmailMessageProblemText()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = SendEmailMessageException.class)
    public ResponseEntity<Object> handleSendEmailMessageException(SendEmailMessageException exception) {
        return new ResponseEntity<>(new MessageDTO(texts.getError(), texts.getSendEmailMessageProblemText()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = UserAlreadyVerifiedException.class)
    public ResponseEntity<Object> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException exception) {
        return new ResponseEntity<>(new MessageDTO("error", texts.getUserAlreadyVerifiedText()), HttpStatus.NOT_ACCEPTABLE);
    }
}
