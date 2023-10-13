package com.ses.ppk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_meeting")
public class Meeting {
    @Id
    @GeneratedValue
    private int meetingId;
    private LocalDate meetingDate;
    private String ruang;
    private String meetingName;
    private String meetingSummary;

    @OneToMany(mappedBy = "_meeting")
    private Set<MeetingAttendee> attendees = new HashSet<>();

}
