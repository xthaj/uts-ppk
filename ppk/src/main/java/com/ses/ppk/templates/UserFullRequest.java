package com.ses.ppk.templates;

import com.ses.ppk.entity.Divisi;
import com.ses.ppk.entity.Role;
import com.ses.ppk.entity.StatusKeanggotaan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFullRequest {
    private String username;
    private String nama;
    private String kelas;
    private String divisi;
    private String role;
    private String statusKeanggotaan;
}