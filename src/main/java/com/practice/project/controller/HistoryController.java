package com.practice.project.controller;

import com.practice.project.dto.WarningEventDTO;
import com.practice.project.model.WarningEvent;
import com.practice.project.service.WarningEventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/history")
public class HistoryController {
    private final WarningEventService warningEventService;

    public HistoryController(WarningEventService warningEventService) {
        this.warningEventService = warningEventService;
    }

    private Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    @GetMapping("/alerts")
    public ResponseEntity<Page<WarningEventDTO>> getUserAlertHistory(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Long userId = getLoggedInUserId();

        // Tạo đối tượng Pageable: Trang 'page', kích thước 'size' và sắp xếp giảm dần theo thời gian
        Pageable pageable = PageRequest.of(page, size, Sort.by("triggeredTime").descending());

        Page<WarningEventDTO> history = warningEventService.getUserHistory(userId, pageable);

        return ResponseEntity.ok(history);
    }
}
