package com.simpleregisterlogin.exceptions.handlers;

import com.simpleregisterlogin.dtos.MessageDTO;
import com.simpleregisterlogin.exceptions.*;
import com.simpleregisterlogin.utils.GeneralUtility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RestControllerAdvice
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        String message;
        if (GeneralUtility.isEmptyOrNull(exception.getMessage())) {
            message = "Username or password is incorrect.";
        } else if (exception.isMessage()) {
            message = "User with name " + exception.getMessage() + " is not found";
        } else {
            message = "User with id " + exception.getMessage() + " is not found";
        }
        return new ResponseEntity<>(new MessageDTO("error", message), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException exception) {
        return new ResponseEntity<>(new MessageDTO("error", "Invalid token: " + exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = InvalidParameterException.class)
    public ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage() + " is required"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = LowPasswordLengthException.class)
    public ResponseEntity<Object> handleLowPasswordLengthException(LowPasswordLengthException exception) {
        return new ResponseEntity<>(new MessageDTO("error", "Password must be " + exception.getMessage() + " characters."), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = ParameterTakenException.class)
    public ResponseEntity<Object> handleParameterTakenException(ParameterTakenException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage() + " is already taken"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = WrongEmailFormatException.class)
    public ResponseEntity<Object> handleWrongEmailFormatException(WrongEmailFormatException exception) {
        return new ResponseEntity<>(new MessageDTO("error", "Wrong email format"), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = WrongDateFormatException.class)
    public ResponseEntity<Object> handleWrongDateFormatException(WrongDateFormatException exception) {
        return new ResponseEntity<>(new MessageDTO("error", "Accepted date format: dd-mm-yyyy"), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = CustomAccessDeniedException.class)
    public ResponseEntity<Object> handleCustomAccessDeniedException(CustomAccessDeniedException exception) {
        String message = "Access Denied";
        if (!GeneralUtility.isEmptyOrNull(exception.getMessage())) {
            message += ": " + exception.getMessage();
        }
        return new ResponseEntity<>(new MessageDTO("error", message), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ParameterMatchException.class)
    public ResponseEntity<Object> handleParameterMatchException(ParameterMatchException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage() + " parameter is already same"), HttpStatus.CONFLICT);
    }
}
