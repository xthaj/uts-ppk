package com.ses.ppk.service;

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

    public List<UserResponse> findAllApplicants() {
        List<User> applicants = userRepository.findByStatusKeanggotaan(StatusKeanggotaan.PENDAFTAR);

        return applicants.stream()
                .map(this::buildUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .nama(user.getNama())
                .kelas(user.getKelas())
                .divisi(user.getDivisi())
                .statusKeanggotaan(user.getStatusKeanggotaan())
                .role(user.getRole())
                .build();
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }

    public Optional<UserResponse> editUser(User user, UserFullRequest userRequest) {
        //edit user
        user.setUsername(userRequest.getUsername());
        user.setNama(userRequest.getNama());
        user.setKelas(userRequest.getKelas());
        user.setDivisi(userRequest.getDivisi());

        user.setRole(userRequest.getRole());
        user.setStatusKeanggotaan(userRequest.getStatusKeanggotaan());

        UserResponse updatedUserResponse = buildUserResponse(user);

        return Optional.of(updatedUserResponse);

    }

    public void deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.delete(user);
        }
    }

    public String apply (ApplyRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (user.getStatusKeanggotaan() != StatusKeanggotaan.BUKAN_ANGGOTA) {
            return "Application refused: User is not a member.";
        }

        user.setDivisi(request.getDivisi());
        user.setKelas(request.getKelas());
        user.setStatusKeanggotaan(StatusKeanggotaan.PENDAFTAR);
        userRepository.save(user);
        return "Application accepted.";

    }

    public void toAdmin(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        user.setRole(Role.ADMIN);
        return;
    }

    //    application section
    public Optional<UserResponse> acceptApplicant(String username) {
        Optional<User> applicantOptional = userRepository.findByUsername(username);

        if (applicantOptional.isPresent()) {
            User applicant = applicantOptional.get();
            applicant.setStatusKeanggotaan(StatusKeanggotaan.ANGGOTA);
            return Optional.of(buildUserResponse(applicant));
        }
        return Optional.empty();
    }

    public Optional<UserResponse> declineApplicant(String username) {
        Optional<User> applicantOptional = userRepository.findByUsername(username);

        if (applicantOptional.isPresent()) {
            User applicant = applicantOptional.get();
            applicant.setStatusKeanggotaan(StatusKeanggotaan.BUKAN_ANGGOTA);
            applicant.setDivisi(null);
            return Optional.of(buildUserResponse(applicant));
        }
        return Optional.empty();
    }
}