package com.practice.project.service;

import com.practice.project.dto.OpenWeatherAPIDTO;
import com.practice.project.dto.WeatherResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
/* Lớp trung gian làm việc giữa dịch vụ API bên ngoài (OpenWeather) và Spring Boot
* Chức năng : Lấy tọa độ từ Location và gọi API bên ngoài để lấy dữ liệu thời tiết hiện tại
* */
public class WeatherClientService {
    private RestTemplate restTemplate;

    @Value("${openweathermap.api.url}")
    private String baseUrl;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherResponseDTO mapRawResponseToDTO(OpenWeatherAPIDTO rawResponse) {
        WeatherResponseDTO dto = new WeatherResponseDTO();
        if (rawResponse == null) {
            throw new IllegalStateException("Received empty response from weather API");
        }

        if (rawResponse.getDt() != null) {
            LocalDateTime timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(rawResponse.getDt()),
                    ZoneId.of("UTC")
            );
            dto.setTimestamp(timestamp);
        }

        if (rawResponse.getMain() != null) {
            dto.setTemperature(rawResponse.getMain().getTemp());
            dto.setHumidity(rawResponse.getMain().getHumidity());
        }

        if (rawResponse.getWind() != null) {
            dto.setWindSpeed(rawResponse.getWind().getSpeed());
        }

        if (rawResponse.getWeather() != null && !rawResponse.getWeather().isEmpty()) {
            dto.setWeatherDescription(rawResponse.getWeather().get(0).getDescription());
        }
        return dto;
    }

    public WeatherResponseDTO getCurrentWeather(Double lat, Double lon) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appId", apiKey)
                .queryParam("units", "metric")
                .toUriString();
        try{
            OpenWeatherAPIDTO rawResponse = restTemplate.getForObject(url, OpenWeatherAPIDTO.class);
            return mapRawResponseToDTO(rawResponse);
        }catch (Exception e){
            System.err.println("Error fetching weather data: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve weather data", e);
        }
    }
}
