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
    private Divisi divisi;
    private Role role;
    private StatusKeanggotaan statusKeanggotaan;
}