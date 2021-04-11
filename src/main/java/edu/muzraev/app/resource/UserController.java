package edu.muzraev.app.resource;

import edu.muzraev.app.exceptions.domain.*;
import edu.muzraev.app.model.HttpResponse;
import edu.muzraev.app.model.User;
import edu.muzraev.app.model.UserPrincipal;
import edu.muzraev.app.service.UserService;
import edu.muzraev.app.utility.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static edu.muzraev.app.constants.FileConstants.*;
import static edu.muzraev.app.constants.SecurityConstants.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/","/user" })
@CrossOrigin("http://localhost:4200")
public class UserController extends ExceptionHandling {

    public static final String EMAIL_SENT = "Email sent ";
    public static final String USER_DELETE_SUCCESSFULLY = "User delete successfully";
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody  User user) {
        authenticate(user.getUsername(),user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody  User user) throws EmailExistsException, UsernameExistsException, MessagingException {
        User newUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }



    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("active") String active,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UsernameExistsException, IOException, EmailExistsException {

        User newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(active),  profileImage);
        return new ResponseEntity<>(newUser,OK);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("active") String active,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UsernameExistsException, IOException, EmailExistsException {

        User updatedUser = userService.updateUser(firstName, lastName, currentUsername, email, username, role, Boolean.parseBoolean(active),
                profileImage);
        return new ResponseEntity<>(updatedUser,OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable("username") String username) {
        User foundUser = userService.findUserByUsername(username);
        return new ResponseEntity<>(foundUser,OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users,OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT +email);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return response(OK, USER_DELETE_SUCCESSFULLY);
    }


    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
                                                   @RequestParam("profileImage")MultipartFile profileImage) throws UsernameExistsException, IOException, EmailExistsException {
        User user = userService.updateProfileImage(username,profileImage);
        return new ResponseEntity<>(user,OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}" , produces = IMAGE_JPEG_VALUE)
    public byte[]getTempProfileImage(@PathVariable("username")String username) throws IOException{
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[]chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0){
                byteArrayOutputStream.write(chunk,0,bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus , httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase()), httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER,jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }
}
