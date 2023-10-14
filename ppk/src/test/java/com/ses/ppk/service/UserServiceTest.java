package com.ses.ppk.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void checkKelas() {
        System.out.println("1ST1: " + UserService.checkKelas("1ST1")); // Should be true
        System.out.println("2KS3: " + UserService.checkKelas("2KS3")); // Should be true
        System.out.println("3SE6: " + UserService.checkKelas("3SE6")); // Should be true
        System.out.println("4SI4: " + UserService.checkKelas("4SI4")); // Should be true

        // Invalid kelas values
        System.out.println("1ST7: " + UserService.checkKelas("1ST7")); // Should be false
        System.out.println("2D31: " + UserService.checkKelas("2D31")); // Should be false
        System.out.println("5SK2: " + UserService.checkKelas("5SK2")); // Should be false
        System.out.println("2KS: " + UserService.checkKelas("2KS")); // Should be false

        // Assertions
//        assertThat(checkKelas("1ST1")).isTrue();
//        assertThat(checkKelas("2KS3")).isTrue();
//        assertThat(checkKelas("3SE6")).isTrue();
//        assertThat(checkKelas("4SI4")).isTrue();
//        assertThat(checkKelas("1ST7")).isFalse();
//        assertThat(checkKelas("2D31")).isFalse();
//        assertThat(checkKelas("5SK2")).isFalse();
//        assertThat(checkKelas("2KS")).isFalse();
    }
}