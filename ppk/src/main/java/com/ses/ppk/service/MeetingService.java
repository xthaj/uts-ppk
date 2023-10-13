package com.ses.ppk.service;

import com.ses.ppk.entity.Meeting;
import com.ses.ppk.entity.MeetingAttendee;
import com.ses.ppk.entity.User;
import com.ses.ppk.exception.MeetingNotFoundException;
import com.ses.ppk.repository.MeetingAttendeeRepository;
import com.ses.ppk.repository.MeetingRepository;
import com.ses.ppk.repository.UserRepository;
import com.ses.ppk.templates.CreateMeetingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingAttendeeRepository meetingAttendeeRepository;

    private final UserRepository userRepository;

    public Meeting createMeeting(CreateMeetingRequest request) {
        Meeting meeting = new Meeting();
        meeting.setMeetingDate(LocalDate.now());
        meeting.setRuang(request.getRuang());
        meeting.setMeetingName(request.getMeetingName());
        meeting.setMeetingSummary(request.getMeetingSummary());
        Meeting createdMeeting = meetingRepository.save(meeting);
        return createdMeeting;
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public Meeting getMeeting(int id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + id));
    }

    public void deleteMeeting(int id) {
        meetingRepository.deleteById(id);
    }

    public List<User> getMeetingAttendees(int id) {
//        Meeting meeting = meetingRepository.findById(id)
//                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + id));
//
//        // Retrieve the attendees from the MeetingAttendee entities
//        List<MeetingAttendee> meetingAttendees = meetingAttendeeRepository.findByMeeting(meeting);
//        List<User> attendees = meetingAttendees.stream()
//                .map(MeetingAttendee::getUser)
//                .collect(Collectors.toList());
//
//        return attendees;
//        return;
    }

    public void attendMeeting(int id, Principal connectedUser) {
//        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
//
//        Meeting meeting = meetingRepository.findById(id)
//                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + id));
//
//        meeting.getAttendees().add(user); // Use getAttendees() to access the attendees
//        meetingRepository.save(meeting);
        return;
    }


//    public Meeting

}
