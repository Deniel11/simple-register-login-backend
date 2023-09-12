package com.simpleregisterlogin.dtos;

import java.util.ArrayList;
import java.util.List;

public class RegisteredUserDTOList {

    List<RegisteredUserDTO> users;

    public RegisteredUserDTOList() {
        users = new ArrayList<>();
    }

    public List<RegisteredUserDTO> getUsers() {
        return users;
    }
}
