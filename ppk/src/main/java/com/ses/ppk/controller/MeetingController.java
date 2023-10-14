package com.ses.ppk.controller;

import com.ses.ppk.service.AuthenticationService;
import com.ses.ppk.service.MeetingService;
import com.ses.ppk.templates.AuthenticationResponse;
import com.ses.ppk.templates.ChangePasswordRequest;
import com.ses.ppk.templates.CreateMeetingRequest;
import com.ses.ppk.templates.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    @PostMapping("/")
    public ResponseEntity<?> createMeeting (
            @RequestBody CreateMeetingRequest request
    ) {
        return ResponseEntity.ok(meetingService.createMeeting(request));
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeeting(
            @PathVariable int id
    ) {
        return ResponseEntity.ok(meetingService.getMeeting(id));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteMeeting(
            @PathVariable int id
    ) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.ok("Meeting deleted successfully");
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<?> getMeetingAttendees(
            @PathVariable int id
    ) {
//        return ResponseEntity.ok(meetingService.getMeetingAttendees(id));
        return ResponseEntity.ok("User marked for having attended the meeting");

    }

    @PostMapping("/{id}/members")
    public ResponseEntity<?> attendMeeting(
            @PathVariable int id,
            Principal connectedUser
    ) {
//        meetingService.attendMeeting(id, connectedUser);
        return ResponseEntity.ok("User marked for having attended the meeting");
    }

    @DeleteMapping ("/{id}/members/{username}")
    public ResponseEntity<?> attendMeeting(
            @PathVariable int id,
            @PathVariable String username
    ) {
//        meetingService.deleteMeetingAttendee(id, username);
        return ResponseEntity.ok().build();
    }
}
