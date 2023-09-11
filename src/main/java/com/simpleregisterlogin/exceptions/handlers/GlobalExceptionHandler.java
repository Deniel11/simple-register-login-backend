package com.simpleregisterlogin.exceptions.handlers;

import com.simpleregisterlogin.dtos.MessageDTO;
import com.simpleregisterlogin.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RestControllerAdvice
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException exception) {
        return new ResponseEntity<>(new MessageDTO("error", "Invalid token: " + exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = InvalidParameterException.class)
    public ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = LowPasswordLengthException.class)
    public ResponseEntity<Object> handleLowPasswordLengthException(LowPasswordLengthException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = ParameterTakenException.class)
    public ResponseEntity<Object> handleParameterTakenException(ParameterTakenException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = WrongEmailFormatException.class)
    public ResponseEntity<Object> handleWrongEmailFormatException(WrongEmailFormatException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = WrongDateFormatException.class)
    public ResponseEntity<Object> handleWrongDateFormatException(WrongDateFormatException exception) {
        return new ResponseEntity<>(new MessageDTO("error", exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }
}
