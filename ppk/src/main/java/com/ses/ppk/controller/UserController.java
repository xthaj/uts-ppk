package com.ses.ppk.controller;


import com.ses.ppk.entity.CustomApiResponse;

import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.ChangePasswordRequest;
import com.ses.ppk.templates.UserFullRequest;
import com.ses.ppk.templates.UserResponse;
import com.ses.ppk.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))})
    public ResponseEntity<?> findAllUsers() {

        List<UserResponse> userResponses = userService.findAllUsers();
        return ResponseEntity.ok(userResponses);
    }

    //works
    @Operation(summary = "Change user role to admin")
    @ApiResponse(responseCode = "200", description = "Successfully changed user role to admin",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @GetMapping("to-admin")
    public ResponseEntity<CustomApiResponse> toAdmin(
            Principal connectedUser
    ) {
        userService.toAdmin(connectedUser);

        CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.OK.value(), "Berhasil mengganti role menjadi admin");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    //works
    @Operation(summary = "Find a user by username")
    @ApiResponse(responseCode = "200", description = "User information",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
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
    @Operation(summary = "Edit user information")
    @ApiResponse(responseCode = "200", description = "User information after editing",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "409", description = "Username already in use",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Role not sufficient",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
    )

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

            if ((userRequest.getUsername() != userOptional.get().getUsername()) &&  (userService.userExists(userRequest.getUsername()))) {
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
    @Operation(summary = "Delete a user by username")
    @ApiResponse(responseCode = "200", description = "User successfully deleted",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Role not sufficient",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
    )

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

    @Operation(summary = "Change user password")
    @ApiResponse(responseCode = "200", description = "Password has been changed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input or password change failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
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
