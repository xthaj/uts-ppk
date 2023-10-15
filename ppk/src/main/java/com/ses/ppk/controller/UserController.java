package com.ses.ppk.controller;

import com.ses.ppk.entity.CustomApiResponse;
import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.ChangePasswordRequest;
import com.ses.ppk.templates.UserFullRequest;
import com.ses.ppk.templates.UserResponse;
import com.ses.ppk.entity.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    //works
    @GetMapping
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

        CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.OK.value(), "Berhasil mengganti role menjadi admin");
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
            CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
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
            String errorMessage = null;

            if (!userService.checkDivisi(userRequest.getDivisi())) {
                errorMessage = "Invalid input for DIVISI. Refer to documentation for correct values.";
            } else if (!userService.checkRole(userRequest.getRole())) {
                errorMessage = "Invalid input for ROLE. Refer to documentation for correct values.";
            } else if (!userService.checkStatus(userRequest.getStatusKeanggotaan())) {
                errorMessage = "Invalid input for STATUS_KEANGGOTAAN. Refer to documentation for correct values.";
            } else if (!userService.checkKelas(userRequest.getKelas())) {
                errorMessage = "Invalid input for KELAS.";
            }

            if (errorMessage != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
            }

            if (!userService.userExists(userRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new CustomApiResponse(HttpStatus.CONFLICT.value(), "New username has been used by someone else"));
            }

            UserResponse userResponse = userService.editUser(userOptional.get(), userRequest);
            return ResponseEntity.ok(userResponse);

        } else {
            String errorMessage = "User not found with username: " + username;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse(HttpStatus.NOT_FOUND.value(), errorMessage));
        }
    }

    //works
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Optional<User> userOptional = userService.findUser(username);

        if (userOptional.isPresent()) {
            userService.deleteUser(username);
            CustomApiResponse mesage = new CustomApiResponse(HttpStatus.OK.value(), "Berhasil menghapus user");
            return ResponseEntity.status(HttpStatus.OK).body(mesage);

        } else {
            CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.NOT_FOUND.value(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PatchMapping
    public ResponseEntity<CustomApiResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        String message = userService.changePassword(request, connectedUser);

        if (message.equals("Password has been changed")) {
            return ResponseEntity.ok(new CustomApiResponse(HttpStatus.OK.value(), message));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), message));
        }
    }

}
