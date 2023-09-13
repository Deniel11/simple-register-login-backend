package com.simpleregisterlogin.services;

import com.simpleregisterlogin.dtos.*;
import com.simpleregisterlogin.entities.User;
import com.simpleregisterlogin.exceptions.*;
import com.simpleregisterlogin.repositories.UserRepository;
import com.simpleregisterlogin.security.UserDetailsImpl;
import com.simpleregisterlogin.security.UserDetailsServiceImpl;
import com.simpleregisterlogin.utils.GeneralUtility;
import com.simpleregisterlogin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    private final MapperService mapperService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager, MapperService mapperService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.mapperService = mapperService;
    }

    @Override
    public RegisteredUserDTO addNewUser(UserDTO userDTO) {
        validateParameters(getInvalidRegistrationParameterNames(userDTO));
        validatePassword(userDTO.getPassword());
        validateUsername(userDTO.getUsername());
        validateEmail(userDTO.getEmail());
        validateDateOfBirth(userDTO.getDateOfBirth());

        User user = mapperService.convertUserDTOtoUser(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        return mapperService.convertUserToRegisteredUserDTO(user);
    }

    private boolean isUsernameTaken(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }


    private boolean isEmailTaken(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    @Override
    public String createAuthenticationToken(AuthenticationRequestDTO authenticationRequest) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        return jwtUtil.generateToken(userDetails);
    }

    @Override
    public void authenticate(AuthenticationRequestDTO authenticationRequest) {
        validateParameters(getInvalidUsernameAndPasswordParameterNames(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            throw new UserNotFoundException();
        }
    }

    private void validateParameters(String parameters) {
        if (parameters.length() > 0) {
            throw new InvalidParameterException(parameters);
        }
    }

    private void validatePassword(String password) {
        if (!GeneralUtility.hasLessCharactersThan(password, 8)) {
            throw new LowPasswordLengthException(8);
        }
    }

    private void validateUsername(String username) {
        if (isUsernameTaken(username)) {
            throw new ParameterTakenException("Username");
        }
    }

    private void validateEmail(String email) {
        if (isEmailTaken(email)) {
            throw new ParameterTakenException("Email");
        }

        if (!GeneralUtility.isValidEmail(email)) {
            throw new WrongEmailFormatException();
        }
    }

    private void validateDateOfBirth(String dateOfBirth) {
        if (!GeneralUtility.isValidDate(dateOfBirth)) {
            throw new WrongDateFormatException();
        }
    }

    private String getInvalidRegistrationParameterNames(UserDTO userDTO) {
        String parameter = getInvalidUsernameAndPasswordParameterNames(userDTO.getUsername(), userDTO.getPassword());

        if (GeneralUtility.isEmptyOrNull(userDTO.getEmail())) {
            String parameterName = "Email";
            if (parameter.length() > 0) {
                parameter += ", " + parameterName;
            } else {
                parameter = parameterName;
            }
        }

        if (GeneralUtility.isEmptyOrNull(userDTO.getDateOfBirth())) {
            String parameterName = "Date of birth";
            if (parameter.length() > 0) {
                parameter += ", " + parameterName;
            } else {
                parameter = parameterName;
            }
        }
        return parameter;
    }

    private String getInvalidUsernameAndPasswordParameterNames(String username, String password) {
        String parameter = "";
        if (GeneralUtility.isEmptyOrNull(username)) {
            String parameterName = "Username";
            if (parameter.length() > 0) {
                parameter += ", " + parameterName;
            } else {
                parameter = parameterName;
            }
        }
        if (GeneralUtility.isEmptyOrNull(password)) {
            String parameterName = "Password";
            if (parameter.length() > 0) {
                parameter += ", " + parameterName;
            } else {
                parameter = parameterName;
            }
        }
        return parameter;
    }

    @Override
    public RegisteredUserDTO getUser(Long id) {

        if (id == null) {
            throw new InvalidParameterException("id");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return mapperService.convertUserToRegisteredUserDTO(user);
    }

    @Override
    public RegisteredUserDTO getOwnUser(HttpServletRequest request) {
        Long id = userDetailsService.extractIdFromRequest(request);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return mapperService.convertUserToRegisteredUserDTO(user);
    }

    @Override
    public RegisteredUserDTOList getUsers() {
        RegisteredUserDTOList registeredUserDTOList = new RegisteredUserDTOList();
        for (User user : userRepository.findAll()) {
            registeredUserDTOList.getUsers().add(mapperService.convertUserToRegisteredUserDTO(user));
        }
        return registeredUserDTOList;
    }

    @Override
    public RegisteredUserDTO updateUser(Long id, UpdateUserDTO updateUserDTO, HttpServletRequest request) {
        Long actualUserId = userDetailsService.extractIdFromRequest(request);
        boolean ownUser = true;
        User editedUser = null;
        if (userDetailsService.extractAdminFromRequest(request)) {
            if (id.equals(actualUserId)) {
                editedUser = userRepository.findById(actualUserId).orElseThrow(() -> new UserNotFoundException(actualUserId));
            } else {
                editedUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
                ownUser = false;
            }
        } else {
            if (updateUserDTO.getAdmin() != null || updateUserDTO.getValid() != null) {
                throw new CustomAccessDeniedException("Edit: admin, valid");
            }

            if (!id.equals(actualUserId)) {
                throw new CustomAccessDeniedException("Edit: other user");
            }
            editedUser = userRepository.findById(actualUserId).orElseThrow(() -> new UserNotFoundException(actualUserId));
        }
        updateUsername(editedUser, updateUserDTO, ownUser);
        updateEmail(editedUser, updateUserDTO, ownUser);
        updatePassword(editedUser, updateUserDTO, ownUser);
        updateDateOfBirth(editedUser, updateUserDTO, ownUser);
        updateAdmin(editedUser, updateUserDTO, ownUser);
        updateValid(editedUser, updateUserDTO, ownUser);
        userRepository.save(editedUser);
        return mapperService.convertUserToRegisteredUserDTO(editedUser);
    }

    private void updateUsername(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (!GeneralUtility.isEmptyOrNull(updateUserDTO.getUsername())) {
            if (underUpdateUser.getUsername().equals(updateUserDTO.getUsername()) && ownUser) {
                throw new ParameterMatchException("Username");
            }
            validateUsername(updateUserDTO.getUsername());

            underUpdateUser.setUsername(updateUserDTO.getUsername());
        }
    }

    private void updateEmail(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (!GeneralUtility.isEmptyOrNull(updateUserDTO.getEmail())) {
            if (underUpdateUser.getEmail().equals(updateUserDTO.getEmail()) && ownUser) {
                throw new ParameterMatchException("Email");
            }
            validateEmail(updateUserDTO.getEmail());

            underUpdateUser.setEmail(updateUserDTO.getEmail());
        }
    }

    private void updatePassword(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (!GeneralUtility.isEmptyOrNull(updateUserDTO.getPassword())) {
            if (encoder.matches(updateUserDTO.getPassword(), underUpdateUser.getPassword()) && ownUser) {
                throw new ParameterMatchException("Password");
            }
            validatePassword(updateUserDTO.getPassword());

            underUpdateUser.setPassword(encoder.encode(updateUserDTO.getPassword()));
        }
    }

    private void updateDateOfBirth(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (!GeneralUtility.isEmptyOrNull(updateUserDTO.getDateOfBirth())) {
            if (underUpdateUser.getDateOfBirth().equals(updateUserDTO.getDateOfBirth()) && ownUser) {
                throw  new ParameterMatchException("Date of Birth");
            }
            validateDateOfBirth(updateUserDTO.getDateOfBirth());

            underUpdateUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
    }

    private void updateAdmin(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (updateUserDTO.getAdmin() != null) {
            if (underUpdateUser.getAdmin() == updateUserDTO.getAdmin() && ownUser) {
                throw new ParameterMatchException("isAdmin");
            }
            underUpdateUser.setAdmin(updateUserDTO.getAdmin());
        }
    }

    private void updateValid(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (updateUserDTO.getValid() != null) {
            if (underUpdateUser.getValid() == updateUserDTO.getValid() && ownUser) {
                throw new ParameterMatchException("valid");
            }
            underUpdateUser.setValid(updateUserDTO.getValid());
        }
    }
}
