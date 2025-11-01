package com.practice.project.repository;

import com.practice.project.model.AlertPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertPreferenceRepository extends JpaRepository<AlertPreference, Long> {
}
