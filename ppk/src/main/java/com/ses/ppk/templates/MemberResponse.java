package com.ses.ppk.templates;

import com.ses.ppk.entity.Divisi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private String username;
    private String name;
    private Divisi divisi;
    private String time;
}
