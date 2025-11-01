package com.practice.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
// model dữ liệu thời tiết
public class WeatherSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapshotId;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private LocalDateTime timestamp; // Thời điểm dữ liệu được lấy
    private Double temperature;
    private Integer humidity; // Độ ẩm
    private Double windSpeed;
    private String weatherDescription;
}
