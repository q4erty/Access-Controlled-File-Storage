package com.portfolio.acfs.service;

public interface MailService {
    void sendPasswordResetCode(String to, String code);
}
