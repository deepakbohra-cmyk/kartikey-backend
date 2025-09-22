package com.kartikey.kartikey.service.impl;

import com.kartikey.kartikey.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendFeedbackNotification(String to, String[] cc, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        if (cc != null && cc.length > 0) {
            message.setCc(cc);
        }
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
