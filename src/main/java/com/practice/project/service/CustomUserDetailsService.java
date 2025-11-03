package com.practice.project.service;

import com.practice.project.model.User;
import com.practice.project.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email) // Giả định có findByEmail
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // RẤT QUAN TRỌNG: Đặt User ID (dạng String) vào trường username
        // để nó được lưu trong Security Context
        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getUserId())) // <--- Đây là ID DẠNG SỐ
                .password(user.getPassword())
                .roles("USER") // Cần định nghĩa vai trò
                .build();
    }
}