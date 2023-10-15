package com.ses.ppk.controller;

import com.ses.ppk.templates.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<CustomApiResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest httpServletRequest) {
        CustomApiResponse apiResponse = new CustomApiResponse(404, "Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(apiResponse);
    }
}
