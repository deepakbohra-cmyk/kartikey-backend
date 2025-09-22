package com.kartikey.kartikey.service;

public interface EmailService {
    void sendFeedbackNotification(String to, String[] cc, String subject, String body);
}
