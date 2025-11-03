package com.practice.project.dto;

import lombok.Data;

@Data
// DTO này được sử dụng khi người dùng muốn đăng ký 1 thiết lập cảnh báo mới hoặc cập nhật các thông số chính
public class AlertRequestDTO {
    private String cityName;
    private String warningType;
    private Double thresholdValue;
    private String notificationChannel;
    private Boolean isActive;
}
