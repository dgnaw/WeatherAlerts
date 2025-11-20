package com.practice.project.service;

import com.practice.project.dto.WeatherResponseDTO;
import com.practice.project.model.AlertPreference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/* Lập lịch cảnh báo  */
@Service
public class AlertScheduler {

    private final AlertService alertService;
    private final WeatherClientService weatherClientService;
    private final NotificationService notificationService;
    private final WarningEventService warningEventService;
    private final WeatherSnapshotService weatherSnapshotService;

    public AlertScheduler(AlertService alertService, WeatherClientService weatherClientService, NotificationService notificationService, WeatherSnapshotService weatherSnapshotService, WarningEventService warningEventService) {
        this.alertService = alertService;
        this.weatherClientService = weatherClientService;
        this.notificationService = notificationService;
        this.weatherSnapshotService = weatherSnapshotService;
        this.warningEventService = warningEventService;
    }

    /*
    *  Lập lịch chạy mỗi 15 phút
    * */
    @Scheduled(fixedRate = 900000)
    public void checkAndTriggerAlerts() {
        System.out.println("--- Bắt đầu kiểm tra cảnh báo ---");

        List<AlertPreference> activeAlerts = alertService.findAllActiveAlerts();

        for (AlertPreference alert : activeAlerts) {
            try{
                WeatherResponseDTO weatherData = weatherSnapshotService.getWeatherDataForLocation(
                        alert.getLocation(),
                        weatherClientService
                );

                boolean isAlertTriggered = checkThreshhold(alert, weatherData);
                if (isAlertTriggered){
                    triggerNotificationAndSaveEvent(alert, weatherData);
                }
            }catch (RuntimeException e) {
                System.err.println("Lỗi khi kiểm tra cảnh báo cho preferenceId " + alert.getPreferenceId() + ": " + e.getMessage());
            }
        }
        System.out.println("--- Kết thúc kiểm tra cảnh báo ---");
    }

    private boolean checkThreshhold(AlertPreference alert, WeatherResponseDTO weatherData) {
        Double currentValue = null;

        switch(alert.getWarningType()){
            case "HIGH_TEMP" :
                currentValue = weatherData.getTemperature();
                break;
            case "HIGH_HUMIDITY":
                currentValue = Double.valueOf(weatherData.getHumidity());
                break;
            case "HIGH_WIND":
                currentValue = weatherData.getWindSpeed();
                break;
            default:
                return false;
        }

        if (currentValue != null && currentValue >= alert.getThresholdValue()) {
            return true;
        }
        return false;
    }

    private void triggerNotificationAndSaveEvent(AlertPreference alert, WeatherResponseDTO weatherData) {
       if(!warningEventService.shouldSendNotification(alert)){
           return;
       }
        System.out.println("!!! CẢNH BÁO KÍCH HOẠT: " + alert.getWarningType());
       try {
           notificationService.sendAlert(alert, weatherData);
           warningEventService.saveNewEvent(alert, weatherData, true);
       } catch (Exception e) {
           warningEventService.saveNewEvent(alert, weatherData, false);
       }
    }
}
