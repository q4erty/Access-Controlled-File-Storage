package com.portfolio.acfs.service;

import com.portfolio.acfs.entity.SupportRequest;
import java.util.List;

public interface SupportService {

    SupportRequest createRequest(String subject, String question);

    List<SupportRequest> getUserRequests();

    List<SupportRequest> getAllRequests();

    SupportRequest getRequestById(Long requestId);

    SupportRequest answerRequest(Long requestId, String answer);
}
