package com.ses.ppk.repository;

import com.ses.ppk.entity.StatusKeanggotaan;
import com.ses.ppk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    List<User> findByStatusKeanggotaan(StatusKeanggotaan statusKeanggotaan);
}
