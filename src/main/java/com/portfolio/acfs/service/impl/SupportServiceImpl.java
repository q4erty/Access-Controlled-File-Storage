package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.SupportRequest;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.SupportRequestRepository;
import com.portfolio.acfs.service.SupportService;
import com.portfolio.acfs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserService userService;

    @Transactional
    public SupportRequest createRequest(String subject, String question) {
        User currentUser = userService.getCurrentUser();

        SupportRequest request = SupportRequest.builder()
                .user(currentUser)
                .subject(subject)
                .question(question)
                .status(SupportRequest.Status.PENDING)
                .build();

        return supportRequestRepository.save(request);
    }

    public List<SupportRequest> getUserRequests() {
        User currentUser = userService.getCurrentUser();
        return supportRequestRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
    }

    public List<SupportRequest> getAllRequests() {
        User currentUser = userService.getCurrentUser();

        if (!hasRole(currentUser, Role.RoleType.SUPPORT) && !hasRole(currentUser, Role.RoleType.ADMIN)) {
            throw new RuntimeException("Access denied. Only SUPPORT or ADMIN can view all requests.");
        }

        return supportRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public SupportRequest getRequestById(Long requestId) {
        User currentUser = userService.getCurrentUser();
        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Support request not found"));

        boolean isOwner = request.getUser().getId().equals(currentUser.getId());
        boolean isSupport = hasRole(currentUser, Role.RoleType.SUPPORT);
        boolean isAdmin = hasRole(currentUser, Role.RoleType.ADMIN);

        if (!isOwner && !isSupport && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        return request;
    }

    @Transactional
    public SupportRequest answerRequest(Long requestId, String answer) {
        User currentUser = userService.getCurrentUser();

        if (!hasRole(currentUser, Role.RoleType.SUPPORT) && !hasRole(currentUser, Role.RoleType.ADMIN)) {
            throw new RuntimeException("Access denied. Only SUPPORT or ADMIN can answer requests.");
        }

        SupportRequest request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Support request not found"));

        if (request.getStatus() == SupportRequest.Status.ANSWERED) {
            throw new RuntimeException("Request already answered");
        }

        request.setAnswer(answer);
        request.setAnsweredBy(currentUser);
        request.setAnsweredAt(LocalDateTime.now());
        request.setStatus(SupportRequest.Status.ANSWERED);

        return supportRequestRepository.save(request);
    }

    private boolean hasRole(User user, Role.RoleType roleType) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == roleType);
    }
}