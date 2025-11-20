package com.practice.project.service;

import com.practice.project.dto.WeatherResponseDTO;
import com.practice.project.model.AlertPreference;
import com.practice.project.model.WarningEvent;
import com.practice.project.repository.WarningRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WarningEventService {
    private WarningRepository eventRepository;
    // Thời gian tối thiểu giữa 2 lần cảnh báo giống nhau (60 phút)
    private static final long COOLDOWN_MINUTES = 60;

    public WarningEventService(WarningRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public boolean shouldSendNotification(AlertPreference alert){
        Optional<WarningEvent> lastEventOpt = eventRepository.findTopByAlertPreferenceOrderByTriggeredTimeDesc(alert);
        if (lastEventOpt.isPresent()){
            return true;
        }
        WarningEvent lastEvent = lastEventOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if (lastEvent.getTriggeredTime().isAfter(now.minusMinutes(COOLDOWN_MINUTES))){
            return true;
        }
        System.out.println("LOG: Bỏ qua cảnh báo cho " + alert.getLocation().getCityName() + " vì đang trong thời gian chờ.");
        return false;
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
