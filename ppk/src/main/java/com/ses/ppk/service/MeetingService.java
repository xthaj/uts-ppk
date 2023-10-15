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
import com.ses.ppk.templates.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public MeetingResponse createMeeting(CreateMeetingRequest request) {
        Meeting meeting = new Meeting();
        meeting.setMeetingDate(LocalDate.now());
        meeting.setRuang(request.getRuang());
        meeting.setMeetingName(request.getMeetingName());
        meeting.setMeetingSummary(request.getMeetingSummary());
        Meeting createdMeeting = meetingRepository.save(meeting);

        return buildMeetingResponse(createdMeeting);
    }

    public MeetingResponse buildMeetingResponse(Meeting meeting) {
        return MeetingResponse.builder()
                .meetingName(meeting.getMeetingName())
                .meetingDate(dateFormatter(meeting.getMeetingDate()))
                .ruang(meeting.getRuang())
                .meetingSummary(meeting.getMeetingSummary())
                .build();
    }

    public MeetingResponseWithId buildMeetingResponseWithId(Meeting meeting) {
        return MeetingResponseWithId.builder()
                .meetingId(meeting.getMeetingId())
                .meetingName(meeting.getMeetingName())
                .meetingDate(dateFormatter(meeting.getMeetingDate()))
                .ruang(meeting.getRuang())
                .meetingSummary(meeting.getMeetingSummary())
                .build();
    }

    public String dateFormatter(LocalDate time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(time);
    }


    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public boolean checkMeetingExists(int id) {
        Optional<Meeting> existingMeeting = meetingRepository.findByMeetingId(id);
        return existingMeeting.isPresent();
    }

    public Optional<Meeting> findByMeetingId(int id) {
        return meetingRepository.findByMeetingId(id);
    }

    public boolean checkMeetingAttendeeExists(Meeting meeting, User user) {
//        System.out.println(meeting.getMeetingId());
//        System.out.println(user.getId());

        MeetingAttendee existingMeetingAttendee = meetingAttendeeRepository.findByMeetingAndUser(meeting, user);
        return existingMeetingAttendee != null;

    }

    public MeetingAttendee getMeetingAttendee(Meeting meeting, User user) {
        MeetingAttendee meetingAttendee = meetingAttendeeRepository.findByMeetingAndUser(meeting, user);
        return meetingAttendee;
    }

    public Meeting getMeeting(int id) {
        return meetingRepository.findByMeetingId(id)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + id));
    }

    public MeetingResponse getMeetingResponse(int id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + id));

        return buildMeetingResponse(meeting);
    }

    public void deleteMeeting(int id) {
        meetingAttendeeRepository.deleteAllByMeeting(findByMeetingId(id).get());
        meetingRepository.deleteById(id);
    }

    public List<MemberResponse> getMeetingAttendees(int meetingId) {
        List<MeetingAttendee> meetingAttendees = meetingAttendeeRepository.findAllByMeeting(meetingRepository.findById(meetingId).get());
        List<MemberResponse> attendeesInfo = new ArrayList<>();

        for (MeetingAttendee meetingAttendee : meetingAttendees) {
            User user = meetingAttendee.getUser();
            MemberResponse memberResponse = new MemberResponse();
            memberResponse.setName(user.getNama());
            memberResponse.setDivisi(user.getDivisi());

            LocalDateTime timeOfAttendance = meetingAttendee.getTimeOfAttendance();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            memberResponse.setTime(timeOfAttendance.format(formatter));

            attendeesInfo.add(memberResponse);
        }
        return attendeesInfo;
    }

    public List<MeetingResponseWithId> findAllByOrderByMeetingDateAsc() {
        List<Meeting> meetings = meetingRepository.findTop10ByOrderByMeetingDateAsc();
        return meetings.stream()
                .map(this::buildMeetingResponseWithId)
                .collect(Collectors.toList());
    }

    public List<MeetingResponseWithId> findAllByOrderByMeetingDateDesc() {
        List<Meeting> meetings = meetingRepository.findTop10ByOrderByMeetingDateAsc();
        return meetings.stream()
                .map(this::buildMeetingResponseWithId)
                .collect(Collectors.toList());

    }

    public List<MeetingResponseWithId> getMeetingsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Meeting> meetings = meetingRepository.findByMeetingDateBetween(startDate, endDate);

        return meetings.stream()
                .map(this::buildMeetingResponseWithId)
                .collect(Collectors.toList());
    }

    public String attendMeeting(int id, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (user.getStatusKeanggotaan() != StatusKeanggotaan.ANGGOTA) {
            return "User bukan member tidak dapat menghadiri meeting";
        }

        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException("Meeting not found with ID: " + id));

        if (checkMeetingAttendeeExists(meeting, user)) {
            return "Member telah menghadiri meeting";
        }

        MeetingAttendee meetingAttendee = new MeetingAttendee();
        meetingAttendee.setUser(user);
        meetingAttendee.setMeeting(meeting);
        meetingAttendee.setTimeOfAttendance(LocalDateTime.now());

        meetingAttendeeRepository.save(meetingAttendee);
        return "Absensi telah dicatat";
    }

    public void deleteMeetingAttendee(MeetingAttendee meetingAttendee) {
        meetingAttendeeRepository.delete(meetingAttendee);
    }

    public MeetingResponse editMeeting(Meeting meeting, EditMeetingRequest request) {
        meeting.setRuang(request.getRuang());
        meeting.setMeetingName(request.getMeetingName());
        meeting.setMeetingSummary(request.getMeetingSummary());
        meeting.setMeetingDate(LocalDate.parse(request.getMeetingDate()));

        Meeting updatedMeeting = meetingRepository.save(meeting);

        return buildMeetingResponse(updatedMeeting);
    }

}
