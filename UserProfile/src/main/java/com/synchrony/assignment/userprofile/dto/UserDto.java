package com.synchrony.assignment.userprofile.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author sanketku
 */

@Data
@Builder


public class UserDto {

    private long userId;
    private String username;

    private String password;

    private Set<ImageDto> images;

    public UserDto(long userId, String username, String password, Set<ImageDto> images) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.images = images;
    }

    public UserDto() {

    }

}
