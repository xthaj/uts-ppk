package com.ses.ppk.templates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingResponseWithId {
    private Integer meetingId;
    private String meetingName;
    private String meetingDate;
    private String ruang;
    private String meetingSummary;
}
