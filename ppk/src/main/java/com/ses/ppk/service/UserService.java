package com.ses.ppk.service;

import com.ses.ppk.entity.Divisi;
import com.ses.ppk.entity.Role;
import com.ses.ppk.repository.UserRepository;
import com.ses.ppk.templates.ApplyRequest;
import com.ses.ppk.templates.ChangePasswordRequest;
import com.ses.ppk.templates.UserFullRequest;
import com.ses.ppk.templates.UserResponse;
import com.ses.ppk.entity.User;
import com.ses.ppk.entity.StatusKeanggotaan;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findUser(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserResponse> findUserResponse(String username) {
        return userRepository.findByUsername(username)
                .map(this::buildUserResponse);
    }

    public List<UserResponse> findAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::buildUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .nama(user.getNama())
                .kelas(user.getKelas())
                .divisi(user.getDivisi())
                .statusKeanggotaan(user.getStatusKeanggotaan())
                .role(user.getRole())
                .build();
    }

    public String changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return "Wrong password";
        }

        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            return "Passwords do not match";
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);

        return "Password has been changed";
    }


    public UserResponse editUser(User user, UserFullRequest userRequest) {
        user.setUsername(userRequest.getUsername());
        user.setNama(userRequest.getNama());
        user.setKelas(userRequest.getKelas());
        user.setDivisi(Divisi.valueOf(userRequest.getDivisi()));

        user.setRole(Role.valueOf(userRequest.getRole()));
        user.setStatusKeanggotaan(StatusKeanggotaan.valueOf(userRequest.getStatusKeanggotaan()));
        userRepository.save(user);

        UserResponse updatedUserResponse = buildUserResponse(user);

        return updatedUserResponse;
    }


    public void deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.delete(user);
        }
    }

    public void toAdmin(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    //    application section
    public String apply (ApplyRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (user.getStatusKeanggotaan() == StatusKeanggotaan.ANGGOTA) {
            return "Application refused: User is already a member.";
        }

        if (user.getStatusKeanggotaan() == StatusKeanggotaan.PENDAFTAR) {
            return "Application refused: User has applied to be a member.";
        }

        user.setStatusKeanggotaan(StatusKeanggotaan.PENDAFTAR);
        user.setKelas(request.getKelas());
        user.setDivisi(Divisi.valueOf(request.getDivisi()));
        userRepository.save(user);
        return "Application accepted.";

    }

    public List<UserResponse> findAllApplicants() {
        List<User> applicants = userRepository.findByStatusKeanggotaan(StatusKeanggotaan.PENDAFTAR);

        return applicants.stream()
                .map(this::buildUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse acceptApplicant(User applicant) {
        applicant.setStatusKeanggotaan(StatusKeanggotaan.ANGGOTA);
        userRepository.save(applicant);
        return buildUserResponse(applicant);
    }

    public UserResponse declineApplicant(User applicant) {
        applicant.setStatusKeanggotaan(StatusKeanggotaan.BUKAN_ANGGOTA);
        applicant.setDivisi(null);
        userRepository.save(applicant);
        return buildUserResponse(applicant);
    }


    public boolean isUserApplicatioValid(ApplyRequest userRequest) {
        try {
            Divisi.valueOf(userRequest.getDivisi());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public boolean userExists(String username) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        return existingUser.isEmpty();
    }

    public boolean checkDivisi(String divisi) {
        if (divisi == null) {
            return true; // Accept null values
        }
        try {
            Divisi.valueOf(divisi);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public boolean checkRole(String role) {
        try {
            Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public boolean checkStatus(String status) {
        try {
            StatusKeanggotaan.valueOf(status);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public static boolean checkKelas(String kelas) {
        if (kelas == null) {
            return true; // Accept null values
        }
        String regexPattern = "^[1-2](ST|KS|D3)[1-6]$|^[3-4](SE|SK|SI|SD)[1-6]$";
        return kelas.matches(regexPattern);
    }


}