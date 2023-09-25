package com.simpleregisterlogin.services;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendVerificationRequest(String username, String email, String token);

    String getToken();
}
