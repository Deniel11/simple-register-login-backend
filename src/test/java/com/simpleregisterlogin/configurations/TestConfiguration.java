package com.simpleregisterlogin.configurations;

import com.simpleregisterlogin.dtos.PasswordDTO;
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

@Configuration
public class TestConfiguration {

    MapperService mapperService;

    BCryptPasswordEncoder encoder;

    @Autowired
    public TestConfiguration(MapperService mapperService, BCryptPasswordEncoder encoder) {
        this.mapperService = mapperService;
        this.encoder = encoder;
    }

    @Bean(name = "fakeUser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFakeUser() {
        String fakeUserName = "fakeUser";
        String fakeUserEmail = "fake_user@email.com";
        String fakeUserPassword = "password";
        String fakeUserDateOfBirth = "01-01-2001";
        String fakeVerificationToken = "fakeVerificationToken";

        User fakeUser = new User(fakeUserName, fakeUserEmail, fakeUserPassword, fakeUserDateOfBirth);
        fakeUser.setAdmin(false);
        fakeUser.setVerified(true);
        fakeUser.setEnabled(true);
        fakeUser.setVerificationToken(fakeVerificationToken);
        return fakeUser;
    }

    @Bean(name = "fakeAdminUser")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    User getFakeAdminUser() {
        String fakeAdminUserName = "fakeAdminUser";
        String fakeAdminUserEmail = "fake_admin_user@email.com";
        String fakeAdminVerificationToken = "fakeAdminVerificationToken";

        User fakeUser = getFakeUser();
        fakeUser.setUsername(fakeAdminUserName);
        fakeUser.setEmail(fakeAdminUserEmail);
        fakeUser.setAdmin(true);
        fakeUser.setVerificationToken(fakeAdminVerificationToken);
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
        return encoder.encode(getFakeUser().getPassword());
    }

    @Bean(name = "fakeUpdateUserDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    UpdateUserDTO getFakeUpdateUserDTO() {
        return mapperService.convertUserToUpdateUserDTO(getFakeUser());
    }

    @Bean(name = "fakePasswordDTO")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    PasswordDTO getFakePasswordDTO() {
        String newPassword = "new password";
        return new PasswordDTO(newPassword);
    }
}
