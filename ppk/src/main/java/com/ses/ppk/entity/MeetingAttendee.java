package com.ses.ppk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_meeting_attendee")
public class MeetingAttendee extends User {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Meeting meeting;

    @ManyToOne
    private User user;

    private LocalDateTime timeOfAttendance;
}

