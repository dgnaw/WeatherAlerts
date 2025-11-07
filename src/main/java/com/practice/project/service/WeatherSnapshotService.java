package com.practice.project.service;

import com.practice.project.dto.WeatherResponseDTO;
import com.practice.project.model.Location;
import com.practice.project.model.WeatherSnapshot;
import com.practice.project.repository.WeatherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WeatherSnapshotService {
    private final WeatherRepository weatherRepository;

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
        snapshot.setTimestamp(weatherData.getTimestamp());

        weatherRepository.save(snapshot);
    }
}
