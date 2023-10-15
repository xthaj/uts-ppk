package com.ses.ppk.controller;

import com.ses.ppk.entity.CustomApiResponse;
import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.AuthenticationRequest;
import com.ses.ppk.templates.AuthenticationResponse;
import com.ses.ppk.templates.RegisterRequest;
import com.ses.ppk.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "200", description = "User registration successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "409", description = "Username is not unique",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @PostMapping("/register")
    public ResponseEntity<?> register (
            @RequestBody RegisterRequest request
    ) {
        if (userService.userExists(request.getUsername())) {
            return ResponseEntity.ok(service.register(request));
        } else {
            CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.CONFLICT.value(), "Username is not unique");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @Operation(summary = "Authenticate a user")
    @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = CustomApiResponse.class)))
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            AuthenticationResponse authenticationResponse = service.authenticate(request);

            return ResponseEntity.ok(authenticationResponse);
        } catch (BadCredentialsException e) {

            CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
