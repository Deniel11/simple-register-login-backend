package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.RegisteredUserDTO;
import com.simpleregisterlogin.dtos.UpdateUserDTO;
import com.simpleregisterlogin.dtos.UserDTO;
import com.simpleregisterlogin.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

public class MapperServiceImplTest {

    String username = "fakeUser";

    String email = "fakeUser@email.com";

    String password = "password";

    String dateOfBirth = "10.10.2000";

    MapperServiceImpl mapperService;

    ModelMapper modelMapper;

    public MapperServiceImplTest() {
        this.modelMapper = Mockito.mock(ModelMapper.class);
        this.mapperService = new MapperServiceImpl();
    }

    @Test
    void convertUserToUserDTO_FromValidUser_ReturnsCorrectDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setDateOfBirth(dateOfBirth);
        User user = new User(username, email, password, dateOfBirth);

        Mockito.when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        Assertions.assertEquals(user.getUsername(), mapperService.convertUserToUserDTO(user).getUsername());
        Assertions.assertEquals(user.getEmail(), mapperService.convertUserToUserDTO(user).getEmail());
        Assertions.assertEquals(user.getPassword(), mapperService.convertUserToUserDTO(user).getPassword());
        Assertions.assertEquals(user.getDateOfBirth(), mapperService.convertUserToUserDTO(user).getDateOfBirth());
    }

    @Test
    void convertUserToRegisteredUserDTO_FromValidUser_ReturnsCorrectDTO() {
        User user = new User(username, email, password, dateOfBirth);
        RegisteredUserDTO registeredUserDTO = new RegisteredUserDTO();
        registeredUserDTO.setUsername(username);
        registeredUserDTO.setEmail(email);
        registeredUserDTO.setDateOfBirth(dateOfBirth);
        registeredUserDTO.setAdmin(user.getAdmin());
        registeredUserDTO.setVerified(user.getVerified());
        registeredUserDTO.setEnabled(user.getEnabled());
        Mockito.when(modelMapper.map(user, RegisteredUserDTO.class)).thenReturn(registeredUserDTO);

        Assertions.assertEquals(registeredUserDTO.getId(), mapperService.convertUserToRegisteredUserDTO(user).getId());
        Assertions.assertEquals(registeredUserDTO.getUsername(), mapperService.convertUserToRegisteredUserDTO(user).getUsername());
        Assertions.assertEquals(registeredUserDTO.getEmail(), mapperService.convertUserToRegisteredUserDTO(user).getEmail());
        Assertions.assertEquals(registeredUserDTO.getDateOfBirth(), mapperService.convertUserToRegisteredUserDTO(user).getDateOfBirth());
        Assertions.assertEquals(registeredUserDTO.getAdmin(), mapperService.convertUserToRegisteredUserDTO(user).getAdmin());
        Assertions.assertEquals(registeredUserDTO.getVerified(), mapperService.convertUserToRegisteredUserDTO(user).getVerified());
        Assertions.assertEquals(registeredUserDTO.getEnabled(), mapperService.convertUserToRegisteredUserDTO(user).getEnabled());
    }

    @Test
    void convertUserDTOtoUser_FromValidUserDTO_ReturnsCorrectUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setDateOfBirth(dateOfBirth);
        User user = new User(username, email, password, dateOfBirth);
        Mockito.when(modelMapper.map(userDTO, User.class)).thenReturn(user);

        Assertions.assertEquals(user.getId(), mapperService.convertUserDTOtoUser(userDTO).getId());
        Assertions.assertEquals(user.getUsername(), mapperService.convertUserDTOtoUser(userDTO).getUsername());
        Assertions.assertEquals(user.getEmail(), mapperService.convertUserDTOtoUser(userDTO).getEmail());
        Assertions.assertEquals(user.getPassword(), mapperService.convertUserDTOtoUser(userDTO).getPassword());
        Assertions.assertEquals(user.getDateOfBirth(), mapperService.convertUserDTOtoUser(userDTO).getDateOfBirth());
        Assertions.assertEquals(user.getAdmin(), mapperService.convertUserDTOtoUser(userDTO).getAdmin());
        Assertions.assertEquals(user.getVerified(), mapperService.convertUserDTOtoUser(userDTO).getVerified());
        Assertions.assertEquals(user.getEnabled(), mapperService.convertUserDTOtoUser(userDTO).getEnabled());
        Assertions.assertEquals(user.getVerificationToken(), mapperService.convertUserDTOtoUser(userDTO).getVerificationToken());
        Assertions.assertEquals(user.getForgotPasswordToken(), mapperService.convertUserDTOtoUser(userDTO).getForgotPasswordToken());
    }

    @Test
    void convertUserToUpdateUserDTO_FromValidUser_ReturnsCorrectDTO() {
        User user = new User(username, email, password, dateOfBirth);
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername(username);
        updateUserDTO.setEmail(email);
        updateUserDTO.setPassword(password);
        updateUserDTO.setDateOfBirth(dateOfBirth);
        updateUserDTO.setAdmin(user.getAdmin());
        updateUserDTO.setVerified(user.getVerified());
        updateUserDTO.setEnabled(user.getEnabled());
        Mockito.when(modelMapper.map(user, UpdateUserDTO.class)).thenReturn(updateUserDTO);

        Assertions.assertEquals(updateUserDTO.getUsername(), mapperService.convertUserToUpdateUserDTO(user).getUsername());
        Assertions.assertEquals(updateUserDTO.getEmail(), mapperService.convertUserToUpdateUserDTO(user).getEmail());
        Assertions.assertEquals(updateUserDTO.getPassword(), mapperService.convertUserToUpdateUserDTO(user).getPassword());
        Assertions.assertEquals(updateUserDTO.getDateOfBirth(), mapperService.convertUserToUpdateUserDTO(user).getDateOfBirth());
        Assertions.assertEquals(updateUserDTO.getAdmin(), mapperService.convertUserToUpdateUserDTO(user).getAdmin());
        Assertions.assertEquals(updateUserDTO.getVerified(), mapperService.convertUserToUpdateUserDTO(user).getVerified());
        Assertions.assertEquals(updateUserDTO.getEnabled(), mapperService.convertUserToUpdateUserDTO(user).getEnabled());
    }
}
