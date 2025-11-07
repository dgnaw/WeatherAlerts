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

        // 1. Lấy tất cả cá thết lập đã cảnh báo đang hoạt động
        List<AlertPreference> activeAlerts = alertService.findAllActiveAlerts();

        for (AlertPreference alert : activeAlerts) {
            // 2. lấy tọa độ
            Double lat = alert.getLocation().getLatitude();
            Double lon = alert.getLocation().getLongitude();

            try{
                // 3. Gọi WeatherClientService để lấy dữ liệu thời tiết hiện tại
                WeatherResponseDTO weatherData = weatherClientService.getCurrentWeather(lat, lon);

                // 4. Áp dụng logic để kiểm tra ngưỡng
                boolean isAlertTriggered = checkThreshhold(alert, weatherData);

                if (isAlertTriggered) {
                    // 5. Nếu vượt ngưỡng, kích hoạt thông báo và lưu Event
                    triggerNotificationAndSaveEvent(alert, weatherData);
                }

                // 6. Lưu WeatherSnapshot (Lưu dữ liệu thời tiết vừa lấy)
                weatherSnapshotService.saveSnapshot(weatherData, alert.getLocation());

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
        System.out.println("!!! CẢNH BÁO KÍCH HOẠT: " + alert.getWarningType() + " tại " + alert.getLocation().getCityName());

        boolean notificationSuccess = true;
        try{
            // 1. Gửi thông báo đến người dùng
            notificationService.sendAlert(alert, weatherData);
        }catch (Exception e) {
            System.err.println("Lỗi gửi thông báo cho preferenceId " + alert.getPreferenceId() + ": " + e.getMessage());
            notificationSuccess = false;
        }
        // 2. Lưu WarningEvent (sự kiện đã xảy ra)
        warningEventService.saveNewEvent(alert, weatherData, notificationSuccess);
    }
}
