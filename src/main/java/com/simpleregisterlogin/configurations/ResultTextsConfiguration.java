package com.simpleregisterlogin.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/texts.sample")
public class ResultTextsConfiguration {

    @Value("${text.ok}")
    private String ok;

    @Value("${text.username}")
    private String usernameText;

    @Value("${text.email}")
    private String emailText;

    @Value("${text.password}")
    private String passwordText;

    @Value("${text.dateOfBirth}")
    private String dateOfBirthText;

    @Value("${text.isAdmin}")
    private String isAdminText;

    @Value("${text.valid}")
    private String validText;

    @Value("${text.id}")
    private String idText;

    @Value("${text.accessDeniedOne}")
    private String accessDeniedOneText;

    @Value("${text.accessDeniedTwo}")
    private String accessDeniedTwoText;

    @Value("${text.email.verifySubject}")
    private String verifySubjectText;

    @Value("${text.email.verifyButton}")
    private String verifyButtonText;

    @Value("${text.email.verifyContent.address}")
    private String verifyContentAddressText;

    @Value("${text.email.verifyContent}")
    private String verifyContentText;

    @Value("${text.email.beenVerify}")
    private String beenVerifyText;

    @Value("${text.error}")
    private String error;

    @Value("${text.error.userNotFound.default}")
    private String userNotFoundText;

    @Value("${text.error.userNotFound.message.partOne}")
    private String userNotFoundTextWithMessagePartOne;

    @Value("${text.error.userNotFound.message.partTwo}")
    private String userNotFoundTextWithMessagePartTwo;

    @Value("${text.error.userNotFound.long.partOne}")
    private String userNotFoundTextWithLongPartOne;

    @Value("${text.error.userNotFound.long.partTwo}")
    private String userNotFoundTextWithLongPartTwo;

    @Value("${text.error.invalidToken}")
    private String invalidTokenText;

    @Value("${text.error.invalidParameter}")
    private String invalidParameterText;

    @Value("${text.error.lowPasswordLength.partOne}")
    private String lowPasswordLengthTextPartOne;

    @Value("${text.error.lowPasswordLength.partTwo}")
    private String lowPasswordLengthTextPartTwo;

    @Value("${text.error.parameterTaken}")
    private String parameterTakenText;

    @Value("${text.error.wrongEmailFormat}")
    private String wrongEmailFormatText;

    @Value("${text.error.wrongDateFormat}")
    private String wrongDateFormatText;

    @Value("${text.error.accessDenied}")
    private String accessDeniedText;

    @Value("${text.error.parameterMatch}")
    private String parameterMatchText;

    @Value("${text.error.userNotActivated}")
    private String userNotActivatedText;

    @Value("${text.error.userNotEnabled}")
    private String userNotEnabledText;

    @Value("${text.error.buildEmailMessageProblem}")
    private String buildEmailMessageProblemText;

    @Value("${text.error.sendEmailMessageProblem}")
    private String sendEmailMessageProblemText;

    @Value("${text.error.userAlreadyVerified}")
    private String userAlreadyVerifiedText;

    public String getOk() {
        return ok;
    }

    public String getError() {
        return error;
    }

    public String getUserNotFoundText() {
        return userNotFoundText;
    }

    public String getUserNotFoundTextWithMessagePartOne() {
        return userNotFoundTextWithMessagePartOne;
    }

    public String getUserNotFoundTextWithMessagePartTwo() {
        return userNotFoundTextWithMessagePartTwo;
    }

    public String getUserNotFoundTextWithLongPartOne() {
        return userNotFoundTextWithLongPartOne;
    }

    public String getUserNotFoundTextWithLongPartTwo() {
        return userNotFoundTextWithLongPartTwo;
    }

    public String getInvalidTokenText() {
        return invalidTokenText;
    }

    public String getInvalidParameterText() {
        return invalidParameterText;
    }

    public String getLowPasswordLengthTextPartOne() {
        return lowPasswordLengthTextPartOne;
    }

    public String getLowPasswordLengthTextPartTwo() {
        return lowPasswordLengthTextPartTwo;
    }

    public String getParameterTakenText() {
        return parameterTakenText;
    }

    public String getWrongEmailFormatText() {
        return wrongEmailFormatText;
    }

    public String getWrongDateFormatText() {
        return wrongDateFormatText;
    }

    public String getAccessDeniedText() {
        return accessDeniedText;
    }

    public String getParameterMatchText() {
        return parameterMatchText;
    }

    public String getUsernameText() {
        return usernameText;
    }

    public String getEmailText() {
        return emailText;
    }

    public String getPasswordText() {
        return passwordText;
    }

    public String getDateOfBirthText() {
        return dateOfBirthText;
    }

    public String getIsAdminText() {
        return isAdminText;
    }

    public String getValidText() {
        return validText;
    }

    public String getIdText() {
        return idText;
    }

    public String getAccessDeniedOneText() {
        return accessDeniedOneText;
    }

    public String getAccessDeniedTwoText() {
        return accessDeniedTwoText;
    }

    public String getUserNotActivatedText() {
        return userNotActivatedText;
    }

    public String getBuildEmailMessageProblemText() {
        return buildEmailMessageProblemText;
    }

    public String getSendEmailMessageProblemText() {
        return sendEmailMessageProblemText;
    }

    public String getUserNotEnabledText() {
        return userNotEnabledText;
    }

    public String getVerifyButtonText() {
        return verifyButtonText;
    }

    public String getVerifySubjectText() {
        return verifySubjectText;
    }

    public String getVerifyContentAddressText() {
        return verifyContentAddressText;
    }

    public String getVerifyContentText() {
        return verifyContentText;
    }

    public String getUserAlreadyVerifiedText() {
        return userAlreadyVerifiedText;
    }

    public String getBeenVerifyText() {
        return beenVerifyText;
    }
}
