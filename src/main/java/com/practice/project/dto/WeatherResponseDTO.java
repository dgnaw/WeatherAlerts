package com.practice.project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeatherResponseDTO {
    private LocalDateTime timestamp;
    private Double temperature;
    private Double windSpeed;
    private Integer humidity;

    private String weatherDescription;
}
