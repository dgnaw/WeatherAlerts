package com.practice.project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
// DTO này được dùng khi Server trả về thông tin về 1 hoặc nhiều thiết lập cảnh báo
public class AlertResponseDTO {
    // Du lieu thiet lap
    private Long preferenceId;
    private Long userId;
    private String warningType;
    private Double thresholdValue;
    private String notificationChannel;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Du lieu Location (de hien thi cho nguoi dung)
    private String locationName;
    private Double latitude;
    private Double longitude;
}
