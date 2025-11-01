package com.practice.project.dto;

import lombok.Data;


@Data
public class UserRegistrationDTO {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
}
