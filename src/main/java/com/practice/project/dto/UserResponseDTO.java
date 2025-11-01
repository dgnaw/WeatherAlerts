package com.practice.project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDateTime registeredDate;
}
