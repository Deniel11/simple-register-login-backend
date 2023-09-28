package com.simpleregisterlogin.services;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.exceptions.BuildEmailMessageException;
import com.simpleregisterlogin.exceptions.SendEmailMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final Environment env;

    private final ResultTextsConfiguration texts;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, Environment env, ResultTextsConfiguration texts) {
        this.mailSender = mailSender;
        this.env = env;
        this.texts = texts;
    }

    public void sendVerificationRequest(String username, String email, String token) {
        if (Objects.requireNonNull(env.getProperty("MAIL_TEST")).equals("false")) {
            MimeMessage message = mailSender.createMimeMessage();
            String url = "http://" + env.getProperty("DOMAIN") + "/verify-email";
            String key = "token";
            String button = "<a style='" + texts.getButtonStyle() + "' href='" + url + "?" + key + "=" + token + "'>" + texts.getVerifyButtonText() + "</a>";
            String h1 = "<h1>" + texts.getVerifyContentAddressText() + " " + username + ",</h1>";
            String paragraph = "<p style='" + texts.getParagraphStyle() + "'>" + texts.getVerifyContentText() + "</p>";
            try {
                message.setFrom(env.getProperty("MAIL_SENDER_EMAIL"));
                message.setRecipients(MimeMessage.RecipientType.TO, email);
                message.setSubject(texts.getVerifySubjectText());
                message.setContent(h1 + paragraph + button, "text/html; charset=utf-8");
            } catch (MessagingException exception) {
                throw new BuildEmailMessageException();
            }

            try {
                mailSender.send(message);
            } catch (MailSendException exception) {
                throw new SendEmailMessageException();
            }
        }
    }

    public String getToken() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 30;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public String sendChangePasswordRequest(String username, String email, String token) {
        if (Objects.requireNonNull(env.getProperty("MAIL_TEST")).equals("false")) {
            MimeMessage message = mailSender.createMimeMessage();
            String url = "http://" + env.getProperty("DOMAIN") + "/change-password";
            String key = "token";
            String button = "<a style='" + texts.getButtonStyle() + "' href='" + url + "?" + key + "=" + token + "'>" + texts.getChangePasswordButtonText() + "</a>";
            String h1 = "<h1>" + texts.getVerifyContentAddressText() + " " + username + ",</h1>";
            String paragraph = "<p style='" + texts.getParagraphStyle() + "'>" + texts.getChangePasswordContentText() + "</p>";
            try {
                message.setFrom(env.getProperty("MAIL_SENDER_EMAIL"));
                message.setRecipients(MimeMessage.RecipientType.TO, email);
                message.setSubject(texts.getChangePasswordSubjectText());
                message.setContent(h1 + paragraph + button, "text/html; charset=utf-8");
            } catch (MessagingException exception) {
                throw new BuildEmailMessageException();
            }

            try {
                mailSender.send(message);
            } catch (MailSendException exception) {
                throw new SendEmailMessageException();
            }
        }
        return texts.getChangePasswordSentText();
    }
}
