package com.schedulemaker.configurations;

import com.schedulemaker.entities.User;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class TestConfiguration {

    private final String fakeUserName = "fakeUser";

    private final String fakeUserEmail = "fake_user@email.com";

    private final String fakeUserPassword = "password";

    private final Date fakeUserBirthDate = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");

    private final boolean fakeUserAdmin = false;

    private final boolean fakeUserValid = true;

    public TestConfiguration() throws ParseException {
    }

    @Bean(name = "fakeUser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFakeUser() {
        User fakeUser = new User(fakeUserName, fakeUserEmail, fakeUserPassword, fakeUserBirthDate);
        fakeUser.setAdmin(fakeUserAdmin);
        fakeUser.setValid(fakeUserValid);
        return fakeUser;
    }
}
