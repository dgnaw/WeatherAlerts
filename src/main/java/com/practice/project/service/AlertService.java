package com.practice.project.service;

import com.practice.project.dto.AlertRequestDTO;
import com.practice.project.dto.AlertResponseDTO;
import com.practice.project.exception.AlertNotFoundException;
import com.practice.project.exception.UserNotFoundException;
import com.practice.project.model.AlertPreference;
import com.practice.project.model.Location;
import com.practice.project.model.User;
import com.practice.project.repository.AlertPreferenceRepository;
import com.practice.project.repository.LocationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertPreferenceRepository alertPreferenceRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;

    public AlertService(AlertPreferenceRepository alertPreferenceRepository,
                        LocationRepository locationRepository,UserService userService,
                        RestTemplate restTemplate) {
        this.alertPreferenceRepository = alertPreferenceRepository;
        this.userService = userService;
        this.locationRepository = locationRepository;
        this.restTemplate = restTemplate;
    }

    private Location findOrCreateLocation(String cityName){
        Optional<Location> existingLocation = locationRepository.findByCityNameIgnoreCase(cityName);

        if (existingLocation.isPresent()){
            return existingLocation.get();
        }

        // Giả định đối tượng để tiếp tục
        Location newLocation = new Location();
        newLocation.setCityName(cityName);
        newLocation.setCountryCode("VN");
        newLocation.setLatitude(21.0285);
        newLocation.setLongitude(105.8542);

        return locationRepository.save(newLocation);

    }

    public AlertResponseDTO mapToAlertResponseDTO(AlertPreference alertPreference){
        if (alertPreference == null){return null;}

        AlertResponseDTO dto = new AlertResponseDTO();
        dto.setPreferenceId(alertPreference.getPreferenceId());
        dto.setWarningType(alertPreference.getWarningType());
        dto.setThresholdValue(alertPreference.getThresholdValue());
        dto.setNotificationChannel(alertPreference.getNotificationChannel());
        dto.setIsActive(alertPreference.getIsActive());
        dto.setCreatedAt(alertPreference.getCreatedAt());

        dto.setUserId(alertPreference.getUser().getUserId());

        Location location = alertPreference.getLocation();
        if (location != null){
            dto.setLocationName(location.getCityName());
            dto.setLatitude(location.getLatitude());
            dto.setLongitude(location.getLongitude());
        }
        return dto;
    }

    private AlertPreference mapToEntity(AlertRequestDTO dto){
        AlertPreference entity = new AlertPreference();
        entity.setWarningType(dto.getWarningType());
        entity.setThresholdValue(dto.getThresholdValue());
        entity.setNotificationChannel(dto.getNotificationChannel());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return entity;

    }

    @Transactional
    public AlertResponseDTO createAlert(AlertRequestDTO dto, Long userId) throws UserNotFoundException{
        User user = userService.findUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        Location location = findOrCreateLocation(dto.getCityName());

        AlertPreference newAlert = mapToEntity(dto);
        newAlert.setUser(user);
        newAlert.setLocation(location);
        newAlert.setCreatedAt(LocalDateTime.now());

        AlertPreference savedAlert = alertPreferenceRepository.save(newAlert);
        return mapToAlertResponseDTO(savedAlert);
    }

    public List<AlertResponseDTO> getAllUserAlerts(Long userId){
        List<AlertPreference> alerts = alertPreferenceRepository.findByUserUserId(userId);

        List<AlertResponseDTO> responseDTOS = new ArrayList<>();
        for (AlertPreference alertPreference : alerts){
            responseDTOS.add(mapToAlertResponseDTO(alertPreference));
        }
        return responseDTOS;
    }

    public AlertResponseDTO getAlertByIdAndUserId(Long alertId, Long userId) throws AlertNotFoundException, AccessDeniedException {
        AlertPreference alert = alertPreferenceRepository.findById(alertId).orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        if (!alert.getUser().getUserId().equals(userId)){
            throw new AccessDeniedException("User is not authorized to view this alert");
        }

        return mapToAlertResponseDTO(alert);
    }

    public AlertResponseDTO updateAlert(Long alertId, AlertRequestDTO dto, Long userId) throws AlertNotFoundException, AccessDeniedException {
        AlertPreference alertToUpdate = alertPreferenceRepository.findById(alertId).orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        if (!alertToUpdate.getUser().getUserId().equals(userId)){
            throw new AccessDeniedException("User is not authorized to view this alert");
        }

        alertToUpdate.setWarningType(dto.getWarningType());
        alertToUpdate.setThresholdValue(dto.getThresholdValue());
        alertToUpdate.setNotificationChannel(dto.getNotificationChannel());
        alertToUpdate.setIsActive(dto.getIsActive());

        if (!alertToUpdate.getLocation().getCityName().equalsIgnoreCase(dto.getCityName())){
            Location newLocation = findOrCreateLocation(dto.getCityName());
            alertToUpdate.setLocation(newLocation);
        }

        AlertPreference updatedAlert = alertPreferenceRepository.save(alertToUpdate);
        return mapToAlertResponseDTO(updatedAlert);
    }

    public void deleteAlert(Long alertId, Long requestUserId) throws AccessDeniedException, AlertNotFoundException {
        AlertPreference alert = alertPreferenceRepository.findById(alertId).orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        if (!alert.getUser().getUserId().equals(requestUserId)){
            throw new AccessDeniedException("User is not authorized to delete this alert");
        }

        alertPreferenceRepository.deleteById(alertId);
    }
}
