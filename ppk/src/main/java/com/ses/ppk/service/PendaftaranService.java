package com.ses.ppk.service;

import com.ses.ppk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PendaftaranService {
    private final UserRepository userRepository;


}
