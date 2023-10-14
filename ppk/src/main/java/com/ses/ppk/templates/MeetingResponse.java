package com.ses.ppk.templates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingResponse {
    private String meetingName;
    private String meetingDate;
    private String ruang;
    private String meetingSummary;
}
