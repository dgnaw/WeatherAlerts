package com.practice.project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarningEventDTO {
    private Long eventId;
    private String warningType;
    private Double actualValue;
    private LocalDateTime triggeredTime;
    private Boolean isSentSuccessfully;

    private String locationName;
}
