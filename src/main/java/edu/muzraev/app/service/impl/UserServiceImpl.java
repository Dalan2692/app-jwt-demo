package edu.muzraev.app.service.impl;


import edu.muzraev.app.enumeration.Role;
import edu.muzraev.app.exceptions.domain.EmailExistsException;
import edu.muzraev.app.exceptions.domain.EmailNotFoundException;
import edu.muzraev.app.exceptions.domain.UsernameExistsException;
import edu.muzraev.app.mail.MailSender;
import edu.muzraev.app.model.User;
import edu.muzraev.app.model.UserPrincipal;
import edu.muzraev.app.repository.UserRepository;

import edu.muzraev.app.service.LoginAttemptService;
import edu.muzraev.app.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static edu.muzraev.app.constants.EmailConstants.EMAIL_SUBJECT;
import static edu.muzraev.app.constants.FileConstants.*;
import static edu.muzraev.app.constants.UserImplConstants.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static edu.muzraev.app.enumeration.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encodePassword;
    private final LoginAttemptService loginAttemptService;
    private final MailSender mailSender;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encodePassword, LoginAttemptService loginAttemptService, MailSender mailSender) {
        this.userRepository = userRepository;
        this.encodePassword = encodePassword;
        this.loginAttemptService = loginAttemptService;
        this.mailSender = mailSender;
    }




    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME+" : "+username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + " : " +username);

            return userPrincipal;
        }
    }

    private void validateLoginAttempt(User user) {
        if (user.isActive()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setActive(false);
            } else {
                user.setActive(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws EmailExistsException, UsernameExistsException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodePassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword);
        user.setActive(true);
//        user.setNonLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        LOGGER.info("New user password: " + password);
//        emailService.sendNewPasswordEmail(user.getFirstName(),password,user.getEmail());
        mailSender.send(user.getEmail(),EMAIL_SUBJECT, "Hello " + user.getFirstName() + ", \n Your new account password is " + password + " \n The Support Team");

        return user;
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return encodePassword.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }



    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameExistsException, EmailExistsException {

        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        if (StringUtils.isNoneBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + ":" +currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;

        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isActive, MultipartFile profileImage) throws EmailExistsException, UsernameExistsException, IOException {
        validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
        User user = new User();
        String password = generatePassword();
        String encodePassword = encodePassword(password);
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword);
        user.setActive(isActive);
//        user.setNonLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }


    @Override
    public User updateUser(String firstName,
                           String lastName,
                           String currentUsername,
                           String email,
                           String username,
                           String role,
                           boolean active ,
//                           boolean notLocked,
                           MultipartFile profileImage) throws EmailExistsException, UsernameExistsException, IOException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, username, email);
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setJoinDate(new Date());
        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setActive(active);
//        currentUser.setNonLocked(notLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);

        return currentUser;
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL+ email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
//        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        mailSender.send(user.getEmail(),
                EMAIL_SUBJECT, "Hello " + user.getFirstName() + ", \n Your new password is " + password + " \n The Support Team");
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistsException, UsernameExistsException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION) , REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM  + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH +
                username + DOT + JPG_EXTENSION).toUriString();
    }

//    private String getTemporaryProfileImageUrl(String  username) {
//        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString() + username;
//    }
}
