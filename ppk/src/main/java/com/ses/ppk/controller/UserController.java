package com.ses.ppk.controller;

import com.ses.ppk.entity.ApiResponse;
import com.ses.ppk.entity.JsonMessage;
import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.ChangePasswordRequest;
import com.ses.ppk.templates.UserFullRequest;
import com.ses.ppk.templates.UserResponse;
import com.ses.ppk.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //works
    @GetMapping("/")
    public ResponseEntity<?> findAllUsers() {
        List<UserResponse> userResponses = userService.findAllUsers();
        return ResponseEntity.ok(userResponses);
    }

    //works
    @GetMapping("to-admin")
    public ResponseEntity<?> toAdmin(
            Principal connectedUser
    ) {
        userService.toAdmin(connectedUser);

        ApiResponse errorResponse = new ApiResponse(HttpStatus.OK.value(), "Berhasil mengganti role menjadi admin");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    //works
    @GetMapping("/{username}")
    public ResponseEntity<?> findUser(@PathVariable String username) {
        Optional<UserResponse> userResponse = userService.findUserResponse(username);

        if (userResponse.isPresent()) {
            return ResponseEntity.ok(userResponse.get());
        } else {
            String errorMessage = "User not found with username: " + username;
            ApiResponse errorResponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    //works
    @PutMapping("/{username}")
    public ResponseEntity<?> editUser(
            @PathVariable String username,
            @RequestBody UserFullRequest userRequest
    ) {
        Optional<User> userOptional = userService.findUser(username);

        if (userOptional.isPresent()) {
            if (userService.isUserRequestValid(userRequest)) {
                if (userService.uniqueUsername(username)) {
                    UserResponse userResponse = userService.editUser(userOptional.get(), userRequest);
                    return ResponseEntity.ok(userResponse);
                } else {
                    ApiResponse errorResponse = new ApiResponse(HttpStatus.CONFLICT.value(), "Username is not unique");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                }
            } else {
                String errorMessage = "Invalid input on ROLE, STATUS_KEANGGOTAAN, or DIVISI. Refer to documentation for correct values.";
                ApiResponse errorResponse = new ApiResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        } else {
            String errorMessage = "User not found with username: " + username;
            ApiResponse errorResponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }
    //works
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Optional<User> userOptional = userService.findUser(username);

        if (userOptional.isPresent()) {
            userService.deleteUser(username);
            ApiResponse mesage = new ApiResponse(HttpStatus.OK.value(), "Berhasil menghapus user");
            return ResponseEntity.status(HttpStatus.OK).body(mesage);

        } else {
            ApiResponse errorResponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PatchMapping
    public ResponseEntity<ApiResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        String message = userService.changePassword(request, connectedUser);

        if (message.equals("Password has been changed")) {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), message));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), message));
        }
    }

}
