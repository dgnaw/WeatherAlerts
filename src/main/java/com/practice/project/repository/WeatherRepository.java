package com.practice.project.repository;

import com.practice.project.model.Location;
import com.practice.project.model.WeatherSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherSnapshot, Long> {
    Optional<WeatherSnapshot> findTopByLocationOrderByTimestampDesc(Location location);
}
