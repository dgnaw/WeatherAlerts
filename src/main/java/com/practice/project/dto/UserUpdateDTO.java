package com.practice.project.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String oldPassword;
    private String newPassword;
}
