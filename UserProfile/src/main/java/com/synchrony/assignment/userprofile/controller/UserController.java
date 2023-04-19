package com.synchrony.assignment.userprofile.controller;

import com.synchrony.assignment.userprofile.dto.ImageResponse;
import com.synchrony.assignment.userprofile.dto.UserDto;
import com.synchrony.assignment.userprofile.dto.UserProfileResponse;
import com.synchrony.assignment.userprofile.model.Image;
import com.synchrony.assignment.userprofile.model.User;
import com.synchrony.assignment.userprofile.service.ImageService;
import com.synchrony.assignment.userprofile.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author sanketku
 */
@RestController
@RequestMapping("/api")
public class UserController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private UserService userService;
    @Resource
    private ImageService imageService;

    /**
     * Creates a user with username and password
     *
     * @param user
     * @return
     */
    @PostMapping("/user")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {

        UserDto savedUser = userService.create(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * Gets the user profile details
     *
     * @param principal
     * @return
     */
    @GetMapping("/user")
    public UserProfileResponse getUser(Principal principal) {
        if (Objects.isNull(principal)) {
            logger.error("Unauthorized User");
            throw new UnauthorizedUserException("Unauthorized");
        }
        String username = principal.getName();
        User user = userService.getUserDetails(username);
        return userService.getUserProfileDetails(user);
    }

    /**
     * Uploads an image with imageUrl to imgur and returns image link and image id
     *
     * @param imageUrl
     * @param principal
     * @return
     */
    @PostMapping("/uploadImage")
    public @ResponseBody ImageResponse upload(@RequestBody Map<String, String> imageUrl, Principal principal) throws IOException {
        if (Objects.isNull(principal)) {
            logger.error("Unauthorized User");
            throw new UnauthorizedUserException("Unauthorized");
        }
        String username = principal.getName();
        User user = userService.getUserDetails(username);
        return imageService.uploadImage(imageUrl.get("url"), user);

    }

    /**
     * Fetches the image for the current logged in user with valid access token based on provided imageID
     *
     * @param id
     * @param principal
     * @return
     */
    @GetMapping("/image")
    public ImageResponse getImage(@RequestParam("imageid") String id, Principal principal) {
        logger.info("getImage called for id {}", id);
        if (Objects.isNull(principal)) {
            logger.error("Unauthorized User");
            throw new UnauthorizedUserException("Unauthorized");
        }
        String username = principal.getName();
        User user = userService.getUserDetails(username);

        return imageService.getImage(id, user);
    }

    /**
     * Fetches all the images uploaded on imgur of the logged in user with valid access token
     *
     * @param principal
     * @return
     */
    @GetMapping("/images")
    public Set<Image> getImages(Principal principal) {

        if (Objects.isNull(principal)) {
            logger.error("Unauthorized User");
            throw new UnauthorizedUserException("Unauthorized");
        }
        String username = principal.getName();
        User user = userService.getUserDetails(username);
        return imageService.getImages(user);
    }

    /**
     * Deletes the image based on given imageId associated with logged in user
     *
     * @param id
     * @param principal
     * @return
     */
    @DeleteMapping("/deleteImage")
    public ResponseEntity<Void> deleteImage(@RequestParam("imageid") String id, Principal principal) {
        logger.info("Deleting image with ID {}", id);
        if (Objects.isNull(principal)) {
            logger.error("Unauthorized User");
            throw new UnauthorizedUserException("Unauthorized");
        }
        String username = principal.getName();
        User user = userService.getUserDetails(username);
        imageService.deleteImage(id, user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}