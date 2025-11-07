package com.practice.project.service;

import com.practice.project.dto.WeatherResponseDTO;
import com.practice.project.model.AlertPreference;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    /* Hàm chính để gửi cảnh báo dựa trên kênh đã thiết lập
    * alert : Thiết lập cảnh báo (chứa kênh, người nhận)
    * weatherData : Dữ liệu thời tiết kích hoạt cảnh báo
    * */
    public void sendAlert(AlertPreference alert, WeatherResponseDTO weatherData){
        String channel = alert.getNotificationChannel();
        switch (channel){
            case "EMAIL":
                sendEmailAlert(alert, weatherData);
                break;
            case "SMS":
                System.out.println("LOG : Cảnh báo SMS chưa được cấu hình.");
                break;
            case "PUSH":
                System.out.println("LOG : Cảnh báo PUSH chưa được cấu hình");
                break;
            default:
                System.err.println("Kênh thông báo không hợp lệ: " + channel);
        }
    }
    /*
    * Xử lý việc gửi Email
    * */
    public void sendEmailAlert(AlertPreference alert, WeatherResponseDTO weatherData){
        /* Sử dụng lớp tiện ich (helper class) : SimpleMailMessage
        * để tạo 1 email văn bản đơn giản bao gồm các thông tin cơ bản như :
        * Người gửi (from)
        * Người nhận (to, cc, bcc)
        * Tiêu đề (subject)
        * Nội dung (text)
        * */
        SimpleMailMessage message = new SimpleMailMessage();

        // 1. Nguoi nhan (Lay tu User Entity)
        String recipientEmail = alert.getUser().getEmail();
        if (recipientEmail == null || recipientEmail.isEmpty()){
            System.err.println("Không thể gửi email: Email người nhận bị thiếu.");
            return;
        }

        // 2. Noi dun email
        String subject = "[CẢNH BÁO THỜI TIẾT] " + alert.getWarningType() + " tại " + alert.getLocation().getCityName();
        String body = buildEmailBody(alert, weatherData);

        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body);

        try{
            mailSender.send(message);
            System.out.println("Thông báo email thành công đến: " + recipientEmail);
        } catch (Exception e){
            System.err.println("Lỗi khi gửi email đến " + recipientEmail + ": " + e.getMessage());
        }

    }

    /*
    * Xây dựng nôi dung chi tiết của Email
    * */
    private String buildEmailBody(AlertPreference alert, WeatherResponseDTO weatherData) {
        return String.format(
                "Cảnh báo của bạn đã được kích hoạt!\n" +
                "Địa điểm: %s\n" +
                "Loại cảnh báo: %s\n" +
                "Ngưỡng thiết lập: %s\n" +
                "Giá trị hiện tại: Nhiệt độ %.1f°C, Tốc độ gió %.1f km/h\n" +
                "Tình trạng: %s\n" +
                "Thời điểm: %s (UTC)",
                alert.getLocation().getCityName(),
                alert.getWarningType(),
                alert.getThresholdValue(),
                weatherData.getTemperature(),
                weatherData.getWindSpeed(),
                weatherData.getWeatherDescription(),
                weatherData.getTimestamp());

    }
}
