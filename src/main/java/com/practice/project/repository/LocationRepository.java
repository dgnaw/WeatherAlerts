package com.practice.project.repository;

import com.practice.project.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCityNameIgnoreCase(String cityName);
}
