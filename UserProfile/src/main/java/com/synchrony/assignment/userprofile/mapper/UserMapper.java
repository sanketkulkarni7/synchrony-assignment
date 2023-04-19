package com.synchrony.assignment.userprofile.mapper;


import com.synchrony.assignment.userprofile.dto.ImageDto;
import com.synchrony.assignment.userprofile.dto.UserDto;
import com.synchrony.assignment.userprofile.model.Image;
import com.synchrony.assignment.userprofile.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;

public class UserMapper {

    private PasswordEncoder encoder;

    // Convert User JPA Entity into UserDto
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getUserId(),
                user.getPassword(),
                user.getUsername(),
                user.getImages().stream().map(UserMapper::mapToImage).collect(Collectors.toSet())
        );

    }

    // Convert UserDto into User JPA Entity
    public static User mapToUser(UserDto userDto) {
        User user = new User(
                userDto.getUserId(),
                userDto.getPassword(),
                userDto.getUsername(),
                userDto.getImages().stream().map(UserMapper::mapToImage).collect(Collectors.toSet())
        );
        return user;
    }

    public static ImageDto mapToImage(Image image) {
        return new ImageDto(image.getImageid());
    }

    public static Image mapToImage(ImageDto image) {
        return new Image(image.getImageId(), null, null);
    }
}
