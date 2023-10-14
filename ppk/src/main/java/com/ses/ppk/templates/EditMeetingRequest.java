package com.ses.ppk.templates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditMeetingRequest {
    private String meetingName;
    private String meetingSummary;
    private String ruang;
    private String meetingDate;
}
