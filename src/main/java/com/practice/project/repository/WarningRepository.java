package com.practice.project.repository;

import com.practice.project.model.WarningEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarningRepository extends JpaRepository<WarningEvent, Long> {
}
