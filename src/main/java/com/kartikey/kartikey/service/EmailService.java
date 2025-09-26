package com.kartikey.kartikey.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendFeedbackNotification(String to, String[] cc, String subject, String body);
}
