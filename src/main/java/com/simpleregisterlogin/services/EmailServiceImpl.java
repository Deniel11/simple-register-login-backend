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
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;

    private final Environment env;

    private final ResultTextsConfiguration texts;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, Environment env, ResultTextsConfiguration texts) {
        this.mailSender = mailSender;
        this.env = env;
        this.texts = texts;
    }

    public void sendVerificationRequest(String username, String email, String token) {
        if (!env.getProperty("MAIL_TEST").equals("true")) {
            MimeMessage message = mailSender.createMimeMessage();

            try {
                message.setFrom("admin@simple-register-login.com");
                message.setRecipients(MimeMessage.RecipientType.TO, email);
                message.setSubject(texts.getVerifySubjectText());

                String button = "<form action=\"http://"
                        + env.getProperty("DOMAIN")
                        + "/api/user/verify-email\" method=\"get\" target=\"_blank\"> <input type=\"hidden\" name=\"token\" value=\""
                        + token
                        + "\" />  <button type=\"submit\">"
                        + texts.getVerifyButtonText()
                        + "</button> </form>";

                String htmlContent = "<h1>" + texts.getVerifyContentAddressText() + " " + username + ",</h1>"
                        + "<p>" + texts.getVerifyContentText() + "</p>"
                        + button;
                message.setContent(htmlContent, "text/html; charset=utf-8");
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

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
