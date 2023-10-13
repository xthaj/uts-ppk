package com.ses.ppk.repository;

import com.ses.ppk.entity.MeetingAttendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingAttendeeRepository extends JpaRepository<MeetingAttendee, Integer> {
}
