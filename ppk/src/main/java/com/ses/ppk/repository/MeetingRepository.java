package com.ses.ppk.repository;

import com.ses.ppk.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
    Optional<Meeting> findByMeetingId(Integer integer);
    List<Meeting> findTop10ByOrderByMeetingDateAsc();
    List<Meeting> findTop10ByOrderByMeetingDateDesc();
    List<Meeting> findAllByOrderByMeetingDateAsc();
    List<Meeting> findAllByOrderByMeetingDateDesc();
    List<Meeting> findByMeetingDateBetween(LocalDate startDate, LocalDate endDate);


}
