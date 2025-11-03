package com.practice.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
// Model thiết lập cảnh báo
public class AlertPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferenceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private String warningType;

    private Double thresholdValue; // ngưỡng cảnh báo ( VD : 35.0 độ C, 50.0 km/h)

    private String notificationChannel; // kênh gửi thông báo (EMAIL, SMS hay PUSH)

    private Boolean isActive;

    private LocalDateTime createdAt;

}
