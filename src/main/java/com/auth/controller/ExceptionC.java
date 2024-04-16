package com.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionC {

    //exception threw when client sent bad credentials, maybe wrong password.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> BadCredentialsExceptionHandler(BadCredentialsException e){
        return ResponseEntity.badRequest().body("Wrong credentials");
    }
}
