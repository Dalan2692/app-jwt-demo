package edu.muzraev.app.exceptions.domain;

import com.auth0.jwt.exceptions.TokenExpiredException;
import edu.muzraev.app.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this point. Please send a %s request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";




    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return createHttpResponse(HttpStatus.BAD_REQUEST,ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(HttpStatus.BAD_REQUEST,INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(HttpStatus.FORBIDDEN,NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(HttpStatus.UNAUTHORIZED,ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException tokenExpiredException) {
        return createHttpResponse(HttpStatus.UNAUTHORIZED,tokenExpiredException.getMessage());
    }
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotException(EmailNotFoundException emailNotFoundException) {
        return createHttpResponse(HttpStatus.BAD_REQUEST,emailNotFoundException.getMessage());
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<HttpResponse> emailExistsException(EmailExistsException emailExistsException) {
        return createHttpResponse(HttpStatus.BAD_REQUEST,emailExistsException.getMessage());
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<HttpResponse> usernameExistsException(UsernameExistsException usernameExistsException) {
        return createHttpResponse(HttpStatus.BAD_REQUEST,usernameExistsException.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException userNotFoundException) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, userNotFoundException.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, "This page was not found");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException e){
        HttpMethod supportedMethod = Objects.requireNonNull(e.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED,String.format(METHOD_IS_NOT_ALLOWED,supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception e){
        LOGGER.error(e.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException e){
        LOGGER.error(e.getMessage());
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException e){
        LOGGER.error(e.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR,ERROR_PROCESSING_FILE);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>
                (new HttpResponse
                        (httpStatus.value(),
                                httpStatus,
                                httpStatus.getReasonPhrase().toUpperCase(),
                                message.toUpperCase()),httpStatus);
    }

}
