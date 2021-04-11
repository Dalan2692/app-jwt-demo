package edu.muzraev.app.exceptions.domain;

public class EmailExistsException extends Exception {
    public EmailExistsException(String message) {
        super(message);
    }
}
