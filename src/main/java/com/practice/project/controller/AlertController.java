package com.practice.project.controller;

import com.practice.project.dto.AlertRequestDTO;
import com.practice.project.dto.AlertResponseDTO;
import com.practice.project.exception.AlertNotFoundException;
import com.practice.project.exception.UserNotFoundException;
import com.practice.project.service.AlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    private Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()){
            throw new IllegalStateException("User is not authenticated");
        }
        try{
            return Long.parseLong(authentication.getName());
        }catch (NumberFormatException e){
            throw new IllegalStateException("User ID format error in context");
        }
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> getAllAlerts() {
        try{
            // Lay Id nguoi dung dang nhap
            Long userId = getLoggedInUserId();

            List<AlertResponseDTO> alert = alertService.getAllUserAlerts(userId);
            return ResponseEntity.ok(alert);
        }catch(IllegalStateException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> getAlertById(@PathVariable Long id){
        try{
            Long userId = getLoggedInUserId();
            AlertResponseDTO alert = alertService.getAlertByIdAndUserId(id, userId);
            return ResponseEntity.ok(alert);
        }catch (SecurityException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }catch(AlertNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch(AccessDeniedException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping
    public ResponseEntity<AlertResponseDTO> createAlert(@RequestBody AlertRequestDTO dto){
        try{
            Long userId = getLoggedInUserId();
            AlertResponseDTO savedAlerts = alertService.createAlert(dto,userId);
            return new ResponseEntity<>(savedAlerts, HttpStatus.CREATED);
        }catch(SecurityException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }catch(IllegalArgumentException e){
            // Loi nghiep vu (vd: Ten thanh pho ko hop le)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch(UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> updateAlert(@PathVariable Long id, @RequestBody AlertRequestDTO dto){
        try{
            Long userId = getLoggedInUserId();
            AlertResponseDTO updatedAlert = alertService.updateAlert(id, dto, userId);
            return ResponseEntity.ok(updatedAlert);
        }catch(SecurityException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }catch(AlertNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(AccessDeniedException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> deleteAlert(@PathVariable Long id){
        try{
            Long userId = getLoggedInUserId();
            alertService.deleteAlert(id, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch(AlertNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (SecurityException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }catch(AccessDeniedException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
