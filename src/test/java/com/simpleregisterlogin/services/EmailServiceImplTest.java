package com.simpleregisterlogin.services;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.exceptions.BuildEmailMessageException;
import com.simpleregisterlogin.exceptions.SendEmailMessageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceImplTest {

    EmailServiceImpl emailService;

    JavaMailSender javaMailSender;

    Environment environment;

    ResultTextsConfiguration texts;

    BeanFactory beanFactory;

    MimeMessage message;

    @Autowired
    public EmailServiceImplTest(ResultTextsConfiguration texts, BeanFactory beanFactory) {
        this.javaMailSender = Mockito.mock(JavaMailSender.class);
        this.environment = Mockito.mock(Environment.class);
        this.texts = texts;
        this.emailService = new EmailServiceImpl(javaMailSender, environment, texts);
        this.beanFactory = beanFactory;
        this.message = Mockito.mock(MimeMessage.class);
    }

    @Test
    void sendVerificationRequest_WithInvalidFromEmail_ThrowsBuildEmailMessageException() throws MessagingException {
        String verificationToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(environment.getProperty("MAIL_TEST")).thenReturn("false");
        Mockito.when(environment.getProperty("DOMAIN")).thenReturn("domain");
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(message);
        Mockito.doThrow(MessagingException.class).when(message).setSubject(texts.getVerifySubjectText());
        Assertions.assertThrows(BuildEmailMessageException.class, () -> emailService.sendVerificationRequest(fakeUser.getUsername(), fakeUser.getEmail(), verificationToken));
    }

    @Test
    void sendVerificationRequest_WithInvalidSenderData_ThrowsSendEmailMessageException() {
        String verificationToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(environment.getProperty("MAIL_TEST")).thenReturn("false");
        Mockito.when(environment.getProperty("DOMAIN")).thenReturn("domain");
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(message);
        Mockito.doThrow(MailSendException.class).when(javaMailSender).send(message);
        Assertions.assertThrows(SendEmailMessageException.class, () -> emailService.sendVerificationRequest(fakeUser.getUsername(), fakeUser.getEmail(), verificationToken));
    }

    @Test
    void sendVerificationRequest_WithValidData_NothingDoesThrow() {
        String verificationToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(environment.getProperty("MAIL_TEST")).thenReturn("false");
        Mockito.when(environment.getProperty("DOMAIN")).thenReturn("domain");
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(message);
        Assertions.assertDoesNotThrow(() -> emailService.sendVerificationRequest(fakeUser.getUsername(), fakeUser.getEmail(), verificationToken));
    }

    @Test
    void sendVerificationRequest_WithTestCase_NothingDoesThrow() {
        String verificationToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(environment.getProperty("MAIL_TEST")).thenReturn("true");
        Assertions.assertDoesNotThrow(() -> emailService.sendVerificationRequest(fakeUser.getUsername(), fakeUser.getEmail(), verificationToken));
    }

    @Test
    void sendChangePasswordRequest_WithInvalidFromEmail_ThrowsBuildEmailMessageException() throws MessagingException {
        String forgotPasswordToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(environment.getProperty("MAIL_TEST")).thenReturn("false");
        Mockito.when(environment.getProperty("DOMAIN")).thenReturn("domain");
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(message);
        Mockito.doThrow(MessagingException.class).when(message).setSubject(texts.getChangePasswordSubjectText());
        Assertions.assertThrows(BuildEmailMessageException.class, () -> emailService.sendChangePasswordRequest(fakeUser.getUsername(), fakeUser.getEmail(), forgotPasswordToken));
    }

    @Test
    void sendChangePasswordRequest_WithInvalidSenderData_ThrowsSendEmailMessageException() {
        String forgotPasswordToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(environment.getProperty("MAIL_TEST")).thenReturn("false");
        Mockito.when(environment.getProperty("DOMAIN")).thenReturn("domain");
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(message);
        Mockito.doThrow(MailSendException.class).when(javaMailSender).send(message);
        Assertions.assertThrows(SendEmailMessageException.class, () -> emailService.sendChangePasswordRequest(fakeUser.getUsername(), fakeUser.getEmail(), forgotPasswordToken));
    }

    @Test
    void sendChangePasswordRequest_WithValidData_ReturnsExceptedMessage() {
        String forgotPasswordToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(environment.getProperty("MAIL_TEST")).thenReturn("false");
        Mockito.when(environment.getProperty("DOMAIN")).thenReturn("domain");
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(message);
        Assertions.assertEquals(texts.getChangePasswordSentText(), emailService.sendChangePasswordRequest(fakeUser.getUsername(), fakeUser.getEmail(), forgotPasswordToken));
        Assertions.assertDoesNotThrow(() -> emailService.sendChangePasswordRequest(fakeUser.getUsername(), fakeUser.getEmail(), forgotPasswordToken));
    }

    @Test
    void sendChangePasswordRequest_WithTestCase_ReturnsExceptedMessage() {
        String forgotPasswordToken = "token";
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        Mockito.when(environment.getProperty("MAIL_TEST")).thenReturn("true");
        Assertions.assertEquals(texts.getChangePasswordSentText(), emailService.sendChangePasswordRequest(fakeUser.getUsername(), fakeUser.getEmail(), forgotPasswordToken));
        Assertions.assertDoesNotThrow(() -> emailService.sendChangePasswordRequest(fakeUser.getUsername(), fakeUser.getEmail(), forgotPasswordToken));
    }
}
