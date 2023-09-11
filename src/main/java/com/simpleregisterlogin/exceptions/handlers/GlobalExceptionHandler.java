package com.simpleregisterlogin.exceptions.handlers;

import com.simpleregisterlogin.dtos.MessageDTO;
import com.simpleregisterlogin.exceptions.InvalidTokenException;
import com.simpleregisterlogin.exceptions.UserNotFoundException;
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
}
