package com.simpleregisterlogin.services;

import com.simpleregisterlogin.configurations.ResultTextsConfiguration;
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

    private final ResultTextsConfiguration texts;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager, MapperService mapperService, ResultTextsConfiguration texts) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.mapperService = mapperService;
        this.texts = texts;
    }

    @Override
    public RegisteredUserDTO addNewUser(UserDTO userDTO, String token) {
        validateParameters(getInvalidRegistrationParameterNames(userDTO));
        validatePassword(userDTO.getPassword());
        validateUsername(userDTO.getUsername());
        validateEmail(userDTO.getEmail());
        validateDateOfBirth(userDTO.getDateOfBirth());

        User user = mapperService.convertUserDTOtoUser(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setVerificationToken(token);
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
            throw new ParameterTakenException(texts.getUsernameText());
        }
    }

    private void validateEmail(String email) {
        if (isEmailTaken(email)) {
            throw new ParameterTakenException(texts.getEmailText());
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
            if (parameter.length() > 0) {
                parameter += ", " + texts.getEmailText();
            } else {
                parameter = texts.getEmailText();
            }
        }

        if (GeneralUtility.isEmptyOrNull(userDTO.getDateOfBirth())) {
            if (parameter.length() > 0) {
                parameter += ", " + texts.getDateOfBirthText();
            } else {
                parameter = texts.getDateOfBirthText();
            }
        }
        return parameter;
    }

    private String getInvalidUsernameAndPasswordParameterNames(String username, String password) {
        String parameter = "";
        if (GeneralUtility.isEmptyOrNull(username)) {
            if (parameter.length() > 0) {
                parameter += ", " + texts.getUsernameText();
            } else {
                parameter = texts.getUsernameText();
            }
        }
        if (GeneralUtility.isEmptyOrNull(password)) {
            if (parameter.length() > 0) {
                parameter += ", " + texts.getPasswordText();
            } else {
                parameter = texts.getPasswordText();
            }
        }
        return parameter;
    }

    private String getInvalidForgotPasswordTokenAndPasswordDTO(String forgotPasswordToken, PasswordDTO passwordDTO) {
        String parameter = "";
        if (GeneralUtility.isEmptyOrNull(forgotPasswordToken)) {
            if (parameter.length() > 0) {
                parameter += ", " + texts.getForgotPasswordTokenText();
            } else {
                parameter = texts.getForgotPasswordTokenText();
            }
        }
        if (GeneralUtility.isEmptyOrNull(passwordDTO.getNewPassword())) {
            if (parameter.length() > 0) {
                parameter += ", " + texts.getNewPasswordText();
            } else {
                parameter = texts.getNewPasswordText();
            }
        }
        return parameter;
    }

    @Override
    public RegisteredUserDTO getUser(Long id) {

        if (id == null) {
            throw new InvalidParameterException(texts.getIdText());
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
        User editedUser;
        if (userDetailsService.extractAdminFromRequest(request)) {
            if (id.equals(actualUserId)) {
                editedUser = userRepository.findById(actualUserId).orElseThrow(() -> new UserNotFoundException(actualUserId));
            } else {
                editedUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
                ownUser = false;
            }
        } else {
            if (updateUserDTO.getAdmin() != null || updateUserDTO.getVerified() != null) {
                throw new CustomAccessDeniedException(texts.getAccessDeniedOneText());
            }

            if (!id.equals(actualUserId)) {
                throw new CustomAccessDeniedException(texts.getAccessDeniedTwoText());
            }
            editedUser = userRepository.findById(actualUserId).orElseThrow(() -> new UserNotFoundException(actualUserId));
        }
        updateUsername(editedUser, updateUserDTO, ownUser);
        updateEmail(editedUser, updateUserDTO, ownUser);
        updatePassword(editedUser, updateUserDTO, ownUser);
        updateDateOfBirth(editedUser, updateUserDTO, ownUser);
        updateAdmin(editedUser, updateUserDTO, ownUser);
        updateValid(editedUser, updateUserDTO, ownUser);
        updateEnabled(editedUser, updateUserDTO, ownUser);
        userRepository.save(editedUser);
        return mapperService.convertUserToRegisteredUserDTO(editedUser);
    }

    private void updateUsername(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (!GeneralUtility.isEmptyOrNull(updateUserDTO.getUsername())) {
            if (underUpdateUser.getUsername().equals(updateUserDTO.getUsername()) && ownUser) {
                throw new ParameterMatchException(texts.getUsernameText());
            }
            validateUsername(updateUserDTO.getUsername());

            underUpdateUser.setUsername(updateUserDTO.getUsername());
        }
    }

    private void updateEmail(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (!GeneralUtility.isEmptyOrNull(updateUserDTO.getEmail())) {
            if (underUpdateUser.getEmail().equals(updateUserDTO.getEmail()) && ownUser) {
                throw new ParameterMatchException(texts.getEmailText());
            }
            validateEmail(updateUserDTO.getEmail());

            underUpdateUser.setEmail(updateUserDTO.getEmail());
        }
    }

    private void updatePassword(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (!GeneralUtility.isEmptyOrNull(updateUserDTO.getPassword())) {
            if (encoder.matches(updateUserDTO.getPassword(), underUpdateUser.getPassword()) && ownUser) {
                throw new ParameterMatchException(texts.getPasswordText());
            }
            validatePassword(updateUserDTO.getPassword());

            underUpdateUser.setPassword(encoder.encode(updateUserDTO.getPassword()));
        }
    }

    private void updateDateOfBirth(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (!GeneralUtility.isEmptyOrNull(updateUserDTO.getDateOfBirth())) {
            if (underUpdateUser.getDateOfBirth().equals(updateUserDTO.getDateOfBirth()) && ownUser) {
                throw new ParameterMatchException(texts.getDateOfBirthText());
            }
            validateDateOfBirth(updateUserDTO.getDateOfBirth());

            underUpdateUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
    }

    private void updateAdmin(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (updateUserDTO.getAdmin() != null) {
            if (underUpdateUser.getAdmin() == updateUserDTO.getAdmin() && ownUser) {
                throw new ParameterMatchException(texts.getIsAdminText());
            }
            underUpdateUser.setAdmin(updateUserDTO.getAdmin());
        }
    }

    private void updateValid(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (updateUserDTO.getVerified() != null) {
            if (underUpdateUser.getVerified() == updateUserDTO.getVerified() && ownUser) {
                throw new ParameterMatchException(texts.getValidText());
            }
            underUpdateUser.setVerified(updateUserDTO.getVerified());
        }
    }

    private void updateEnabled(User underUpdateUser, UpdateUserDTO updateUserDTO, boolean ownUser) {
        if (updateUserDTO.getEnabled() != null) {
            if (underUpdateUser.getEnabled() == updateUserDTO.getEnabled() && ownUser) {
                throw new ParameterMatchException(texts.getEnabledText());
            }
            underUpdateUser.setEnabled(updateUserDTO.getEnabled());
        }
    }

    @Override
    public String verifyUser(EmailTokenDTO emailTokenDTO) {
        User user = userRepository.findUserByVerificationToken(emailTokenDTO.getToken()).orElseThrow(() -> new InvalidTokenException());
        if (user.getVerified()) {
            throw new UserAlreadyVerifiedException();
        }
        user.setVerified(true);
        userRepository.save(user);
        return texts.getBeenVerifyText();
    }

    @Override
    public String changePassword(String forgotPasswordToken, PasswordDTO passwordDTO) {
        User user = userRepository.findUserByForgotPasswordToken(forgotPasswordToken).orElseThrow(() -> new InvalidTokenException());
        if (user.getForgotPasswordRequestTime() + 600000 < System.currentTimeMillis()) {
            throw new ExpiredTokenException();
        }
        validateParameters(getInvalidForgotPasswordTokenAndPasswordDTO(forgotPasswordToken, passwordDTO));
        validatePassword(passwordDTO.getNewPassword());

        user.setPassword(encoder.encode(passwordDTO.getNewPassword()));
        user.setForgotPasswordToken(null);
        userRepository.save(user);
        return texts.getPasswordChangedText();
    }

    @Override
    public String saveToken(String email, String forgotPasswordToken) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new EmailAddressNotFoundException(email));
        user.setForgotPasswordToken(forgotPasswordToken);
        user.setForgotPasswordRequestTime(System.currentTimeMillis());
        userRepository.save(user);
        return user.getUsername();
    }
}
