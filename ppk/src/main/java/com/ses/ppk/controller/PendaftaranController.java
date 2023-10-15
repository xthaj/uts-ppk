package com.ses.ppk.controller;

import com.ses.ppk.templates.CustomApiResponse;
import com.ses.ppk.entity.StatusKeanggotaan;
import com.ses.ppk.entity.User;
import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.ApplyRequest;
import com.ses.ppk.templates.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/applicants")
@RequiredArgsConstructor
public class PendaftaranController {
    private final UserService pendaftaranService;

    //workd
    @Operation(summary = "Apply to become a member")
    @ApiResponse(responseCode = "200", description = "Application accepted",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @PostMapping
    public ResponseEntity<?> applyBeingMember(
            @RequestBody ApplyRequest userRequest,
            Principal connectedUser
    ) {

        String errorMessage = null;

        if (!pendaftaranService.checkDivisi(userRequest.getDivisi())) {
            errorMessage = "Invalid input for DIVISI. Refer to documentation for correct values.";
        } else if (!pendaftaranService.checkKelas(userRequest.getKelas())) {
            errorMessage = "Invalid input for KELAS.";
        }

        if (errorMessage != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
        }

        String applicationResult = pendaftaranService.apply(userRequest, connectedUser);

        if (applicationResult.equals("Application accepted.")) {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomApiResponse(HttpStatus.OK.value(), applicationResult));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), applicationResult));
        }
    }

    //works
    @Operation(summary = "See all applicants")
    @ApiResponse(responseCode = "200", description = "List of applicants",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
    )
    @GetMapping
    public ResponseEntity<List<UserResponse>> seeApplicants() {
        List<UserResponse> userResponses = pendaftaranService.findAllApplicants();
        return ResponseEntity.ok(userResponses);
    }

    //works
    @Operation(summary = "Accept an applicant by username")
    @ApiResponse(responseCode = "200", description = "Applicant accepted",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @PatchMapping("/{username}")
    public ResponseEntity<?> acceptApplicant(@PathVariable String username) {
        Optional<User> userOptional = pendaftaranService.findUser(username);

        if (userOptional.isPresent()) {
            //check that the person is actually an applicant
            User user = userOptional.get();
            if (user.getStatusKeanggotaan() != StatusKeanggotaan.PENDAFTAR) {
                String errorMessage = "User is not an applicant";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
            }

            UserResponse userResponse = pendaftaranService.acceptApplicant(user);
            return ResponseEntity.ok(userResponse);
        } else {
            String errorMessage = "User not found with username: " + username;
            CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    //works
    @Operation(summary = "Decline an applicant by username")
    @ApiResponse(responseCode = "200", description = "Applicant declined",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @DeleteMapping("/{username}")
    public ResponseEntity<?> declineApplicant(@PathVariable String username) {
        Optional<User> userOptional = pendaftaranService.findUser(username);

        if (userOptional.isPresent()) {
            //check that the person is actually an applicant
            User user = userOptional.get();
            if (user.getStatusKeanggotaan() != StatusKeanggotaan.PENDAFTAR) {
                String errorMessage = "User is not an applicant";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
            }

            UserResponse userResponse = pendaftaranService.declineApplicant(user);
            return ResponseEntity.ok(userResponse);
        } else {
            String errorMessage = "User not found with username: " + username;
            CustomApiResponse errorResponse = new CustomApiResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

}
