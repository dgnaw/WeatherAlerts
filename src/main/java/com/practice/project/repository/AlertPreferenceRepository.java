package com.practice.project.repository;

import com.practice.project.model.AlertPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertPreferenceRepository extends JpaRepository<AlertPreference, Long> {
   List<AlertPreference> findByUserUserId(Long userId);
}
