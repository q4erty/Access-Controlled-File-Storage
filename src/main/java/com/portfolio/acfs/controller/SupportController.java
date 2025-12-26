package com.portfolio.acfs.controller;

import com.portfolio.acfs.dto.AnswerSupportRequestRequest;
import com.portfolio.acfs.dto.CreateSupportRequestRequest;
import com.portfolio.acfs.dto.SupportRequestResponse;
import com.portfolio.acfs.dto.UserSupportRequestResponse;
import com.portfolio.acfs.entity.SupportRequest;
import com.portfolio.acfs.mapper.SupportRequestMapper;
import com.portfolio.acfs.service.SupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final SupportRequestMapper supportRequestMapper;

    @PostMapping
    public ResponseEntity<UserSupportRequestResponse> createRequest(
            @Valid @RequestBody CreateSupportRequestRequest request) {
        SupportRequest supportRequest = supportService.createRequest(request.getSubject(), request.getQuestion());
        return ResponseEntity.ok(supportRequestMapper.toUserSupportRequestResponse(supportRequest));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UserSupportRequestResponse>> getUserRequests() {
        List<SupportRequest> requests = supportService.getUserRequests();
        return ResponseEntity.ok(supportRequestMapper.toUserSupportRequestResponseList(requests));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    public ResponseEntity<List<SupportRequestResponse>> getAllRequests() {
        List<SupportRequest> requests = supportService.getAllRequests();
        return ResponseEntity.ok(supportRequestMapper.toSupportRequestResponseList(requests));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<SupportRequestResponse> getRequestById(@PathVariable Long requestId) {
        SupportRequest request = supportService.getRequestById(requestId);
        return ResponseEntity.ok(supportRequestMapper.toSupportRequestResponse(request));
    }

    @PostMapping("/{requestId}/answer")
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    public ResponseEntity<SupportRequestResponse> answerRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody AnswerSupportRequestRequest request) {
        SupportRequest supportRequest = supportService.answerRequest(requestId, request.getAnswer());
        return ResponseEntity.ok(supportRequestMapper.toSupportRequestResponse(supportRequest));
    }
}