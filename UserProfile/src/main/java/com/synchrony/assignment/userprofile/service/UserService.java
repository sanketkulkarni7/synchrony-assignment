package com.synchrony.assignment.userprofile.service;

import com.synchrony.assignment.userprofile.dto.UserDto;
import com.synchrony.assignment.userprofile.dto.UserProfileResponse;
import com.synchrony.assignment.userprofile.model.User;

/**
 * @author sanketku
 */
public interface UserService {

    UserDto create(UserDto userDto);

    User getUserDetails(String userName);

    UserProfileResponse getUserProfileDetails(User user);

}
