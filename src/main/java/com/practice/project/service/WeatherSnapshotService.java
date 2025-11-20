package com.practice.project.service;

import com.practice.project.dto.WeatherResponseDTO;
import com.practice.project.model.Location;
import com.practice.project.model.WeatherSnapshot;
import com.practice.project.repository.WeatherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WeatherSnapshotService {
    private final WeatherRepository weatherRepository;
    private static final long FRESHNESS_THRESHOLD_MINUTES = 15;
    public WeatherSnapshotService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }
    /*
     * Lưu dữ liệu thời tiết mới nhất vào lịch sử
     * Được gọi bởi AlertScheduler
     * */
    @Transactional
    public void saveSnapshot(WeatherResponseDTO weatherData, Location location) {
        WeatherSnapshot snapshot = new WeatherSnapshot();

        // ánh xạ từ dto -> entity
        snapshot.setTemperature(weatherData.getTemperature());
        snapshot.setHumidity(weatherData.getHumidity());
        snapshot.setWindSpeed(weatherData.getWindSpeed());
        snapshot.setWeatherDescription(weatherData.getWeatherDescription());

        // gán khóa ngoại cho Location và Timestamp
        snapshot.setLocation(location);
        if (weatherData.getTimestamp() != null) {
            snapshot.setTimestamp(weatherData.getTimestamp());
        }else{
            snapshot.setTimestamp(LocalDateTime.now());
        }
        weatherRepository.save(snapshot);
    }
    public WeatherResponseDTO getWeatherDataForLocation(Location location, WeatherClientService weatherClient) {
        Optional<WeatherSnapshot> latestSnapshot = weatherRepository.findTopByLocationOrderByTimestampDesc(location);
        if (latestSnapshot.isPresent()) {
            WeatherSnapshot snapshot = latestSnapshot.get();
            LocalDateTime now = LocalDateTime.now();

            if (snapshot.getTimestamp().isAfter(now.minusMinutes(FRESHNESS_THRESHOLD_MINUTES))){
                System.out.println("LOG : Sử dụng dữ liệu Cache cho: " + location.getCityName());
                return mapSnapshotToDTO(snapshot);
            }
        }
        System.out.println("LOG: Gọi API mới cho " + location.getCityName());
        WeatherResponseDTO newData = weatherClient.getCurrentWeather(location.getLatitude(), location.getLongitude());

        saveSnapshot(newData, location);

        return newData;
    }

    private WeatherResponseDTO mapSnapshotToDTO(WeatherSnapshot snapshot) {
        WeatherResponseDTO dto = new WeatherResponseDTO();
        dto.setTemperature(snapshot.getTemperature());
        dto.setWindSpeed(snapshot.getWindSpeed());
        dto.setHumidity(snapshot.getHumidity());
        dto.setWeatherDescription(snapshot.getWeatherDescription());
        dto.setTimestamp(snapshot.getTimestamp());
        return dto;
    }
}
