package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password reset verification code");
        message.setText("""
                You requested a password reset.

                Verification code: %s

                This code is valid for 10 minutes.
                If you did not request this, ignore this email.
                """.formatted(code));

        mailSender.send(message);
    }
}
