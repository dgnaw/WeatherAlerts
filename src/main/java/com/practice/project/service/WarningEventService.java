package com.practice.project.service;

import com.practice.project.dto.WeatherResponseDTO;
import com.practice.project.model.AlertPreference;
import com.practice.project.model.WarningEvent;
import com.practice.project.repository.WarningRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WarningEventService {
    private WarningRepository eventRepository;

    public WarningEventService(WarningRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    /*
    * Lưu sự kiện cảnh báo đã kích hoạt
    * được gọi bởi AlertScheduler
    * */
    @Transactional
    public void saveNewEvent(AlertPreference alert, WeatherResponseDTO weatherData, boolean isNotificationSuccess) {
        WarningEvent event = new WarningEvent();
        // gán khóa ngoại
        event.setPreference(alert);
        event.setWarningType(alert.getWarningType());

        // lấy giá trị thực tế đã kích hoạt
        Double actualValue = getActualValueForEvent(alert.getWarningType(), weatherData);
        event.setActualValue(actualValue);

        // lấy timestamp và trạng thái gửi thông báo
        event.setTriggeredTime(weatherData.getTimestamp());
        event.setIsSentSuccessfully(isNotificationSuccess);
        eventRepository.save(event);
    }

    private Double getActualValueForEvent(String warningType, WeatherResponseDTO weatherData) {
        return switch (warningType){
            case "HIGH_TEMP" -> weatherData.getTemperature();
            case "HIGH_WIND" -> weatherData.getWindSpeed();
            default -> null;
        };
    }
}
