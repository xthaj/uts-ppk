package com.ses.ppk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_kegiatan")
public class Kegiatan {
    @Id
    @GeneratedValue
    private int kegiatan_id;
    private String jenis_kegiatan;
    private String judul_kegiatan;
    private String kegiatan_summary;
    private Date kegiatan_date;
    private String lokasi;

    @ManyToMany
    private Set<User> kegiatan_participants = new HashSet<>();
}
