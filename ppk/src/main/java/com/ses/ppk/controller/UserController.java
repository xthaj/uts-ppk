package com.ses.ppk.controller;

import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.ChangePasswordRequest;
import com.ses.ppk.templates.UserFullRequest;
import com.ses.ppk.templates.UserResponse;
import com.ses.ppk.entity.User;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> findAllUsers() {
        List<UserResponse> userResponses = userService.findAllUsers();
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("to-admin")
    public ResponseEntity<?> toAdmin(
            Principal connectedUser
    ) {
        userService.toAdmin(connectedUser);
        return ResponseEntity.ok("Berhasil mengganti role menjadi admin.");
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> findUser(@PathVariable String username) {
        Optional<UserResponse> userResponse = userService.findUserResponse(username);

        return userResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserResponse> editUser(
            @PathVariable String username,
            @RequestBody UserFullRequest userRequest
    ) {
        Optional<User> userOptional = userService.findUser(username);
        Optional<UserResponse> userResponse = Optional.empty();

        if (userOptional.isPresent()) {
            User user = userOptional.get(); // Convert Optional<User> to User
            userResponse = userService.editUser(user, userRequest);
        }

        return userResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        Optional<User> userOptional = userService.findUser(username);

        if (userOptional.isPresent()) {
            userService.deleteUser(username);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }


    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }


}
