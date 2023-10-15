package com.ses.ppk.controller;

import com.ses.ppk.entity.CustomApiResponse;
import com.ses.ppk.entity.Meeting;
import com.ses.ppk.entity.MeetingAttendee;
import com.ses.ppk.entity.User;
import com.ses.ppk.service.MeetingService;
import com.ses.ppk.service.UserService;
import com.ses.ppk.templates.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MeetingController {
    private final MeetingService meetingService;
    private final UserService userService;
    //works
    @Operation(summary = "Create a meeting")
    @ApiResponse(responseCode = "200", description = "Meeting created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Role not sufficient",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
    )
    @PostMapping
    public ResponseEntity<?> createMeeting (
            @RequestBody CreateMeetingRequest request
    ) {
        if (meetingService.checkRuang(request.getRuang())) {
            return ResponseEntity.ok(meetingService.createMeeting(request));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), "Ruangan harus berada antara gedung 2 atau 3, lantai yang sesuai, dan nomor ruang yang sesuai"));
    }

    //works
    @Operation(summary = "Edit a meeting by ID")
    @ApiResponse(responseCode = "200", description = "Meeting edited successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Meeting not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Role not sufficient",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> editMeeting (
            @PathVariable int id,
            @RequestBody EditMeetingRequest request
    ) {
        if (meetingService.checkMeetingExists(id)) {
            String errorMessage = null;

            if (meetingService.checkRuang(request.getRuang())) {
                if (request.getMeetingDate()==null) {
                    errorMessage = "meetingDate should not be NULL. Please input original value if you don't want to change it.";
                } else if (request.getMeetingName()==null) {
                    errorMessage = "meetingName should not be NULL. Please input original value if you don't want to change it.";
                } else if ((request.getMeetingSummary()==null)) {
                    errorMessage = "meetingSummary should not be NULL. Please input original value if you don't want to change it.";
                } else if ((request.getRuang()==null)) {
                    errorMessage = "ruang should not be NULL. Please input original value if you don't want to change it.";
                } else if (!request.getMeetingDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
                    errorMessage = "meetingDate format is incorrect. Use YYYY-MM-DD";
                }

                if (errorMessage != null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
                }

                Meeting meeting = meetingService.getMeeting(id);
                return ResponseEntity.ok(meetingService.editMeeting(meeting, request));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), "Ruang is incorrect."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST.value(), "Meeting not found."));
        }

    }

    //normal works
    //sort works
    //filter works yippeee
    @Operation(summary = "Get meetings")
    @ApiResponse(responseCode = "200", description = "List of meetings",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingResponseWithId.class))
    )
    @GetMapping
    public ResponseEntity<?> getMeetings(
            @RequestParam(name = "sort", defaultValue = "desc") String sortOrder,
            @RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date
    ) {
        List<MeetingResponseWithId> meetings = new ArrayList<>();

        if (start_date != null && end_date != null) {
            meetings = meetingService.getMeetingsByDateRange(start_date, end_date);
        } else if ("asc".equalsIgnoreCase(sortOrder)) {
            meetings = meetingService.findAllByOrderByMeetingDateAsc();
        } else {
            meetings = meetingService.findAllByOrderByMeetingDateDesc();
        }

        return ResponseEntity.ok(meetings);
    }


    //works
    @Operation(summary = "Get a meeting by ID")
    @ApiResponse(responseCode = "200", description = "Meeting information",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Meeting not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getMeeting(
            @PathVariable int id
    ) {
        System.out.println(id);
        System.out.println(meetingService.checkMeetingExists(id));

        if (meetingService.checkMeetingExists(id)) {
            MeetingResponse foundMeeting = meetingService.getMeetingResponse(id);
            return ResponseEntity.ok(foundMeeting);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
        }
    }

    //works
    @Operation(summary = "Get members of a meeting by ID")
    @ApiResponse(responseCode = "200", description = "List of meeting attendees",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberResponse.class))
    )
    @GetMapping("/{id}/members")
    public ResponseEntity<?> getMeetingAttendees(
            @PathVariable int id
    ) {
        List<MemberResponse> memberResponses = meetingService.getMeetingAttendees(id);
        return ResponseEntity.ok(memberResponses);
    }

    @Operation(summary = "Delete a meeting by ID")
    @ApiResponse(responseCode = "200", description = "Meeting deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Meeting not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "403", description = "Role not sufficient",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
    )
    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteMeeting(
            @PathVariable int id
    ) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.ok(new CustomApiResponse(HttpStatus.OK.value(), "Meeting deleted successfully"));
    }

    //works
    @Operation(summary = "Attend a meeting by ID")
    @ApiResponse(responseCode = "200", description = "Successfully attended the meeting",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Meeting not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @PostMapping("/{id}/members")
    public ResponseEntity<?> attendMeeting(
            @PathVariable int id,
            Principal connectedUser
    ) {
        if (meetingService.checkMeetingExists(id)) {
            String message = meetingService.attendMeeting(id, connectedUser);
            return ResponseEntity.ok(new CustomApiResponse(HttpStatus.OK.value(), message));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
        }
    }


    //works
    @Operation(summary = "Delete meeting attendee")
    @ApiResponse(responseCode = "200", description = "Meeting attendee deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @ApiResponse(responseCode = "404", description = "Meeting or user not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiResponse.class))
    )
    @DeleteMapping ("/{meeting_id}/members/{username}")
    public ResponseEntity<?> deleteMeetingAttendee(
            @PathVariable int meeting_id,
            @PathVariable String username
    ) {
        Optional<Meeting> meeting = meetingService.findByMeetingId(meeting_id);
        Optional<User> user = userService.findUser(username);
        Optional<MeetingAttendee> meetingAttendee = Optional.ofNullable(meetingService.getMeetingAttendee(meeting.get(), user.get()));

        if (meeting.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
        } else if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } else if (meetingService.checkMeetingAttendeeExists(meeting.get(), user.get())) {
            meetingService.deleteMeetingAttendee(meetingAttendee.get());
            return ResponseEntity.ok(new CustomApiResponse(HttpStatus.OK.value(), "Absensi berhasil dihapus"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Absensi tidak ditemukan");
        }
    }
}
