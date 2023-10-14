package com.ses.ppk.repository;

import com.ses.ppk.entity.Meeting;
import com.ses.ppk.entity.MeetingAttendee;
import com.ses.ppk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingAttendeeRepository extends JpaRepository<MeetingAttendee, Integer> {
    MeetingAttendee findByMeetingAndUser(Meeting meeting, User user);
}
