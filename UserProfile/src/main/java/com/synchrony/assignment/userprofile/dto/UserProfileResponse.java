package com.synchrony.assignment.userprofile.dto;

import com.synchrony.assignment.userprofile.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response class which provides JSON response with user and image details
 *
 * @author sanketku
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private String username;

    private long userId;

    private Set<Image> images;
}
