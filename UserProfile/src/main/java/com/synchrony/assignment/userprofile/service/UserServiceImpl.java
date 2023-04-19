package com.synchrony.assignment.userprofile.service;

import com.synchrony.assignment.userprofile.dao.ImageDao;
import com.synchrony.assignment.userprofile.dao.UserDao;
import com.synchrony.assignment.userprofile.dto.UserDto;
import com.synchrony.assignment.userprofile.dto.UserProfileResponse;
import com.synchrony.assignment.userprofile.exception.ResourceNotFoundException;
import com.synchrony.assignment.userprofile.exception.UserAlreadyExistsException;
import com.synchrony.assignment.userprofile.model.Image;
import com.synchrony.assignment.userprofile.model.User;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * @author sanketku
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserDao userDao;


    private ImageDao imageDao;

    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserDao userDao, ImageDao imageDao, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.imageDao = imageDao;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a user based on user details
     *
     * @param userDto
     * @return userDto
     */
    @Override
    public UserDto create(UserDto userDto) {

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = modelMapper.map(userDto, User.class);
        Optional<User> optionalUser = userDao.findByUsername(user.getUsername());

        if (optionalUser.isPresent()) {
            logger.error("User with username{} already exists", userDto.getUserId());
            throw new UserAlreadyExistsException("User Already Exists");
        }
        logger.info("Creating user with username {}", userDto.getUsername());
        User savedUser = userDao.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Fetches a user from DB based on username
     *
     * @param username
     * @return userDto
     */
    @Override
    public User getUserDetails(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> {
            logger.error("User with username {} not present ", username);
            return new ResourceNotFoundException("User", "username", username);
        });

    }

    /**
     * Get the user and  associated image details for the current user
     *
     * @param user
     * @return
     */
    @Override
    public UserProfileResponse getUserProfileDetails(User user) {

        Set<Image> images = imageDao.findByUser(user);
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUserId(user.getUserId());
        userProfileResponse.setUsername(user.getUsername());
        userProfileResponse.setImages(images);

        return userProfileResponse;

    }
}
