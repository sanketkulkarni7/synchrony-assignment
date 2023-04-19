package com.synchrony.assignment.userprofile.authentication;

import com.synchrony.assignment.userprofile.model.User;
import com.synchrony.assignment.userprofile.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

/**
 * Authentication Implementation for the User
 *
 * @author sanketku
 */
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    public UserAuthenticationProvider(UserRepository repository, PasswordEncoder encoder) {
        this.encoder = encoder;
        this.repository = repository;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<User> user = repository.findByUsername(username);
        if (!user.isPresent()) {
            throw new BadCredentialsException("User Details not found");
        }

        if (encoder.matches(password, user.get().getPassword())) {

            return new UsernamePasswordAuthenticationToken(username, password, Collections.singleton(DefaultRoleAuthority.AUTHORITY_ROLE_USER));
        } else {
            throw new BadCredentialsException("Password mismatch");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
