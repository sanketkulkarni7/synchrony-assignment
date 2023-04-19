package com.synchrony.assignment.userprofile.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author sanketku
 */
public interface DefaultRoleAuthority {

    String ROLE_USER = "ROLE_USER";
    GrantedAuthority AUTHORITY_ROLE_USER = new SimpleGrantedAuthority(ROLE_USER);

}

