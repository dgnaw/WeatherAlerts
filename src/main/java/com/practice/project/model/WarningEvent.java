package com.practice.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class WarningEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name= "preference_id")
    private AlertPreference preference;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private LocalDateTime triggeredTime;
    private String message;
    private Double actualValue;
    private Boolean isSentSuccessfully;
}
