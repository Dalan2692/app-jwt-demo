package edu.muzraev.app.listener;

import edu.muzraev.app.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationFailedListener {
    private final LoginAttemptService loginAttemptService;

    public AuthenticationFailedListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void authenticationFailure(AuthenticationFailureBadCredentialsEvent event) throws ExecutionException {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
