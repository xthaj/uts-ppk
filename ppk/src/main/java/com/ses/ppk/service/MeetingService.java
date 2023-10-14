package com.ses.ppk.service;

import com.ses.ppk.entity.Meeting;
import com.ses.ppk.entity.MeetingAttendee;
import com.ses.ppk.entity.StatusKeanggotaan;
import com.ses.ppk.entity.User;
import com.ses.ppk.exception.MeetingNotFoundException;
import com.ses.ppk.exception.UserNotFoundException;
import com.ses.ppk.repository.MeetingAttendeeRepository;
import com.ses.ppk.repository.MeetingRepository;
import com.ses.ppk.repository.UserRepository;
import com.ses.ppk.templates.CreateMeetingRequest;
import com.ses.ppk.templates.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static boolean checkRuang(String kelas) {
        if (kelas == null) {
            return false;
        }
        String regexPattern = "^[2][2-6][1-8]$|^[3][2-4][1-8]$";
        return kelas.matches(regexPattern);
    }
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

    public boolean checkMeetingExists(int id) {
        Optional<Meeting> existingMeeting = meetingRepository.findById(id);
        return existingMeeting.isEmpty();
    }
    public Meeting getMeeting(int id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + id));
    }

    public void deleteMeeting(int id) {
        meetingRepository.deleteById(id);
    }

    public List<MemberResponse> getMeetingAttendees(int meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + meetingId));

        List<MemberResponse> attendeesInfo = new ArrayList<>();

        for (MeetingAttendee attendee : meeting.getAttendees()) {
            User user = userRepository.findById(attendee.getUser().getId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + attendee.getUser().getId()));

            MemberResponse memberResponse = new MemberResponse(user.getNama(), user.getDivisi());
            attendeesInfo.add(memberResponse);
        }

        return attendeesInfo;
    }

    public List<Meeting> findAllByOrderByMeetingDateAsc() {
        return meetingRepository.findTop10ByOrderByMeetingDateAsc();
    }

    public List<Meeting> findAllByOrderByMeetingDateDesc() {
        return meetingRepository.findTop10ByOrderByMeetingDateAsc();
    }

    public List<Meeting> getMeetingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return meetingRepository.findByMeetingDateBetween(startDate, endDate);
    }

    public String attendMeeting(int id, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (user.getStatusKeanggotaan() != StatusKeanggotaan.ANGGOTA) {
            return "User bukan member tidak dapat menghadiri meeting";
        }

        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + id));

        MeetingAttendee meetingAttendee = new MeetingAttendee();
        meetingAttendee.setUser(user);
        meetingAttendee.setMeeting(meeting);
        meetingAttendee.setTimeOfAttendance(LocalDateTime.now());

        meetingAttendeeRepository.save(meetingAttendee);
        return "Absensi telah dicatat";
    }

    public void deleteMeetingAttendee(int meetingId, User user) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + meetingId));

        MeetingAttendee meetingAttendee = meetingAttendeeRepository.findByMeetingAndUser(meeting, user);

        if (meetingAttendee != null) {
            meetingAttendeeRepository.delete(meetingAttendee);
        }
    }


    public Meeting editMeeting(Meeting meeting, CreateMeetingRequest request) {
        meeting.setRuang(request.getRuang());
        meeting.setMeetingName(request.getMeetingName());
        meeting.setMeetingSummary(request.getMeetingSummary());

        Meeting updatedMeeting = meetingRepository.save(meeting);

        return updatedMeeting;
    }
}
