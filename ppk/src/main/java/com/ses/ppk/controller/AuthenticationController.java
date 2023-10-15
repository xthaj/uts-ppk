package com.ses.ppk.controller;

import com.ses.ppk.entity.CustomApiResponse;
import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.AuthenticationRequest;
import com.ses.ppk.templates.AuthenticationResponse;
import com.ses.ppk.templates.RegisterRequest;
import com.ses.ppk.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> register (
            @RequestBody RegisterRequest request
    ) {
        if (!userService.userExists(request.getUsername())) {
            return ResponseEntity.ok(service.register(request));
        } else {
            CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.CONFLICT.value(), "Username is not unique");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
