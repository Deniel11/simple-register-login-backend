package com.schedulemaker.configurations;

import com.schedulemaker.dtos.UserDTO;
import com.schedulemaker.entities.User;
import com.schedulemaker.services.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.text.ParseException;

@Configuration
public class TestConfiguration {

    private final String fakeUserName = "fakeUser";

    private final String fakeUserEmail = "fake_user@email.com";

    private final String fakeUserPassword = "password";

    private final String fakeUserBirthDate = "01-01-2001";

    private final boolean fakeUserAdmin = false;

    private final boolean fakeUserValid = true;

    MapperService mapperService;

    @Autowired
    public TestConfiguration(MapperService mapperService) throws ParseException {
        this.mapperService = mapperService;
    }

    @Bean(name = "fakeUser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFakeUser() {
        User fakeUser = new User(fakeUserName, fakeUserEmail, fakeUserPassword, fakeUserBirthDate);
        fakeUser.setAdmin(fakeUserAdmin);
        fakeUser.setValid(fakeUserValid);
        return fakeUser;
    }

    @Bean(name = "fakeUserDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    UserDTO getFakeUserDTO() {
        return mapperService.convertUserToUserDTO(getFakeUser());
    }
}
