package com.practice.project.repository;

import com.practice.project.model.AlertPreference;
import com.practice.project.model.WarningEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarningRepository extends JpaRepository<WarningEvent, Long> {
    Optional<WarningEvent> findTopByAlertPreferenceOrderByTriggeredTimeDesc(AlertPreference alert);
}
