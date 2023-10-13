package com.ses.ppk.templates;

import com.ses.ppk.entity.Divisi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String nama;
    private String kelas;
    private Divisi divisi;
}