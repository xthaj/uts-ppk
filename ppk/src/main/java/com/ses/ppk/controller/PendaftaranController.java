package com.ses.ppk.controller;

import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.ApplyRequest;
import com.ses.ppk.templates.ChangePasswordRequest;
import com.ses.ppk.templates.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/applicants")
@RequiredArgsConstructor
public class PendaftaranController {
    private final UserService pendaftaranService;

    @PostMapping
    public ResponseEntity<?> applyBeingMember(
            @RequestBody ApplyRequest request,
            Principal connectedUser
    ) {
        // Call the service and get the response message
        String message = pendaftaranService.apply(request, connectedUser);

        if (message.contains("refused")) {
            return ResponseEntity.badRequest().body(message);
        }
        // Return an OK response with the message
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/")
    public ResponseEntity<List<UserResponse>> seeApplicants() {
        List<UserResponse> userResponses = pendaftaranService.findAllApplicants();
        return ResponseEntity.ok(userResponses);
    }

    @PatchMapping("/{username}")
    public ResponseEntity<UserResponse> acceptApplicant(@PathVariable String username) {
        Optional<UserResponse> userResponse = pendaftaranService.acceptApplicant(username);

        return userResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<UserResponse> declineApplicant(@PathVariable String username) {
        Optional<UserResponse> userResponse = pendaftaranService.declineApplicant(username);

        return userResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
