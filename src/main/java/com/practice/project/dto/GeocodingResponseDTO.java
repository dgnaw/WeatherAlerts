package com.practice.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponseDTO {
    private String name;
    private Double lat;
    private Double lon;
    private String country;
}
