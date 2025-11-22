package com.practice.project.repository;

import com.practice.project.model.AlertPreference;
import com.practice.project.model.WarningEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarningRepository extends JpaRepository<WarningEvent, Long> {
    Optional<WarningEvent> findTopByPreferenceOrderByTriggeredTimeDesc(AlertPreference alert);

    Page<WarningEvent> findByPreference_User_UserId(Long userId, Pageable pageable);
}
