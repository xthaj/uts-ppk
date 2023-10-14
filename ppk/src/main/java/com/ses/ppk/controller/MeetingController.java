package com.ses.ppk.controller;

import com.ses.ppk.entity.ApiResponse;
import com.ses.ppk.entity.Meeting;
import com.ses.ppk.entity.User;
import com.ses.ppk.service.AuthenticationService;
import com.ses.ppk.service.MeetingService;
import com.ses.ppk.templates.*;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingService meetingService;

    @PostMapping
    public ResponseEntity<?> createMeeting (
            @RequestBody CreateMeetingRequest request
    ) {
        if (meetingService.checkRuang(request.getRuang())) {
            return ResponseEntity.ok(meetingService.createMeeting(request));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Ruangan harus berada antara gedung 2 atau 3, lantai yang sesuai, dan nomor ruang yang sesuai"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editMeeting (
            @PathVariable int id,
            @RequestBody CreateMeetingRequest request
    ) {
        if (meetingService.checkMeetingExists(id)) {
            if (meetingService.checkRuang(request.getRuang())) {
                Meeting meeting = meetingService.getMeeting(id);
                return ResponseEntity.ok(meetingService.editMeeting(meeting, request));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Ruang is incorrect."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Meeting not found."));
        }

    }

    @GetMapping
    public ResponseEntity<List<Meeting>> getMeetings(
            @RequestParam(name = "sort", defaultValue = "asc") String sortOrder,
            @RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date
    ) {
        List<Meeting> meetings = new ArrayList<>();

        if ("asc".equalsIgnoreCase(sortOrder)) {
            meetings = meetingService.findAllByOrderByMeetingDateAsc();
        } else if ("desc".equalsIgnoreCase(sortOrder)) {
            meetings = meetingService.findAllByOrderByMeetingDateDesc();
        }

        if (start_date != null && end_date != null) {
            // Filter meetings within the specified date range
            meetings = meetings.stream()
                    .filter(meeting -> meeting.getMeetingDate().isAfter(start_date) && meeting.getMeetingDate().isBefore(end_date.plusDays(1)))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(meetings);
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getMeeting(
            @PathVariable int id
    ) {
        if (meetingService.checkMeetingExists(id)) {
            Meeting foundMeeting = meetingService.getMeeting(id);
            return ResponseEntity.ok(foundMeeting);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
        }
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<?> getMeetingAttendees(
            @PathVariable int id
    ) {
        List<MemberResponse> memberResponses = meetingService.getMeetingAttendees(id);
        return ResponseEntity.ok(memberResponses);

    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteMeeting(
            @PathVariable int id
    ) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Meeting deleted successfully"));
    }


    @PostMapping("/{id}/members")
    public ResponseEntity<?> attendMeeting(
            @PathVariable int id,
            Principal connectedUser
    ) {
        if (meetingService.checkMeetingExists(id)) {
            String message = meetingService.attendMeeting(id, connectedUser);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), message));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
        }
    }

    @DeleteMapping ("/{id}/members/{username}")
    public ResponseEntity<?> deleteMeetingAttendee(
            @PathVariable int id,
            Principal connectedUser
    ) {
        if (meetingService.checkMeetingExists(id)) {
            meetingService.attendMeeting(id, connectedUser);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Meeting attendance removed from the meeting"));


        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
        }
    }
}
