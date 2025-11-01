package com.practice.project.repository;

import com.practice.project.model.WeatherSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<WeatherSnapshot, Long> {
}
