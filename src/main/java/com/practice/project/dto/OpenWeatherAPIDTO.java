package com.practice.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherAPIDTO {
    private OpenWeatherMainDTO main;
    private OpenWeatherWindDTO wind;
    private List<OpenWeatherDetailDTO> weather;
    private Long dt;
}
