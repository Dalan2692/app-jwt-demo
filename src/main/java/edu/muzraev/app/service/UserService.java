package edu.muzraev.app.service;

import edu.muzraev.app.exceptions.domain.EmailExistsException;
import edu.muzraev.app.exceptions.domain.EmailNotFoundException;
import edu.muzraev.app.exceptions.domain.UsernameExistsException;
import edu.muzraev.app.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User register(String firstName, String lastName, String username, String email) throws EmailExistsException, UsernameExistsException;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User addNewUser(String firstName , String lastName, String username, String email, String role, boolean active , MultipartFile profile) throws EmailExistsException, UsernameExistsException, IOException;
    User updateUser(String firstName, String lastName, String currentUsername, String email, String username, String role, boolean active ,
                    MultipartFile profileImage) throws EmailExistsException, UsernameExistsException, IOException;
    void deleteUser(long id);
    void resetPassword(String email) throws EmailNotFoundException;
    User updateProfileImage(String username, MultipartFile file) throws EmailExistsException, UsernameExistsException, IOException;
}
