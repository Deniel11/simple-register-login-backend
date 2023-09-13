package com.simpleregisterlogin.configurations;

import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.UpdateUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.services.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.ParseException;

@Configuration
public class TestConfiguration {

    private final String fakeUserName = "fakeUser";

    private final String fakeAdminUserName = "fakeAdminUser";

    private final String fakeUserEmail = "fake_user@email.com";

    private final String fakeAdminUserEmail = "fake_admin_user@email.com";

    private final String fakeUserPassword = "password";

    private final String fakeUserDateOfBirth = "01-01-2001";

    private final boolean fakeUserAdmin = false;

    private final boolean fakeAdminUserAdmin = true;

    private final boolean fakeUserValid = true;

    MapperService mapperService;

    BCryptPasswordEncoder encoder;

    @Autowired
    public TestConfiguration(MapperService mapperService, BCryptPasswordEncoder encoder) throws ParseException {
        this.mapperService = mapperService;
        this.encoder = encoder;
    }

    @Bean(name = "fakeUser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFakeUser() {
        User fakeUser = new User(fakeUserName, fakeUserEmail, fakeUserPassword, fakeUserDateOfBirth);
        fakeUser.setAdmin(fakeUserAdmin);
        fakeUser.setValid(fakeUserValid);
        return fakeUser;
    }

    @Bean(name = "fakeAdminUser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFakeAdminUser() {
        User fakeUser = getFakeUser();
        fakeUser.setUsername(fakeAdminUserName);
        fakeUser.setEmail(fakeAdminUserEmail);
        fakeUser.setAdmin(fakeAdminUserAdmin);
        return fakeUser;
    }

    @Bean(name = "fakeUserDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    UserDTO getFakeUserDTO() {
        return mapperService.convertUserToUserDTO(getFakeUser());
    }

    @Bean(name = "fakeRegisteredUserDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    RegisteredUserDTO getFakeRegisteredUserDTO() {
        return mapperService.convertUserToRegisteredUserDTO(getFakeUser());
    }

    @Bean("fakeEncodedPassword")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    String getEncodedPassword() {
        return encoder.encode(fakeUserPassword);
    }

    @Bean(name = "fakeUpdateUserDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    UpdateUserDTO getFakeUpdateUserDTO() {
        return mapperService.convertUserToUpdateUserDTO(getFakeUser());
    }
}
