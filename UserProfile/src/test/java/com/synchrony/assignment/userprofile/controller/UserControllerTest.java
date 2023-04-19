package com.synchrony.assignment.userprofile.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchrony.assignment.userprofile.dto.ImageResponse;
import com.synchrony.assignment.userprofile.dto.UserDto;
import com.synchrony.assignment.userprofile.dto.UserProfileResponse;
import com.synchrony.assignment.userprofile.model.Image;
import com.synchrony.assignment.userprofile.model.User;
import com.synchrony.assignment.userprofile.service.ImageService;
import com.synchrony.assignment.userprofile.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sanketku
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ImageService imageService;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDto user = new UserDto();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        UserDto savedUser = new UserDto();
        savedUser.setUsername("testUser");
        savedUser.setPassword("testPassword");
        given(userService.create(user)).willReturn(savedUser);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.password").value("testPassword"));
    }

    @Test
    @WithMockUser
    public void testGetUser() throws Exception {
        User user = new User();
        user.setUsername("user");
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUsername("user");
        given(userService.getUserDetails("user")).willReturn(user);
        given(userService.getUserProfileDetails(user)).willReturn(userProfileResponse);

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    public void testGetUserWithUnauthorizedUser() throws Exception {
        User user = new User();
        user.setUsername("user");
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setUsername("user");
        given(userService.getUserDetails("user")).willReturn(user);
        given(userService.getUserProfileDetails(user)).willReturn(userProfileResponse);

        mockMvc.perform(get("/api/user"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @WithMockUser
    public void testUploadImage() throws Exception {
        String imageUrl = "https://example.com/image.jpg";
        String imageId = "abcd1234";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setUploadedImage("https://i.imgur.com/" + imageId + ".jpg");
        imageResponse.setImageId(imageId);
        given(imageService.uploadImage(anyString(), any(User.class))).willReturn(imageResponse);
        given(userService.getUserDetails("user")).willReturn(new User());

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("url", imageUrl);

        mockMvc.perform(post("/api/uploadImage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestMap)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadedImage").value("https://i.imgur.com/abcd1234.jpg"))
                .andExpect(jsonPath("$.imageId").value("abcd1234"));
    }

    @Test
    public void testUploadImageWithUnauthorizedUser() throws Exception {
        String imageUrl = "https://example.com/image.jpg";
        String imageId = "abcd1234";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setUploadedImage("https://i.imgur.com/" + imageId + ".jpg");
        imageResponse.setImageId(imageId);
        given(imageService.uploadImage(anyString(), any(User.class))).willReturn(imageResponse);

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("url", imageUrl);

        mockMvc.perform(post("/api/uploadImage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestMap)))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @WithMockUser
    public void testGetImage() throws Exception {
        String imageId = "abcd1234";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setUploadedImage("https://i.imgur.com/" + imageId + ".jpg");
        imageResponse.setImageId(imageId);
        given(imageService.getImage(any(), any(User.class))).willReturn(imageResponse);
        given(userService.getUserDetails("user")).willReturn(new User());
        mockMvc.perform(get("/api/image?imageid={imageId}", imageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadedImage").value("https://i.imgur.com/abcd1234.jpg"))
                .andExpect(jsonPath("$.imageId").value("abcd1234"));
    }

    @Test
    @WithMockUser
    public void testGetImages() throws Exception {
        User user = new User();
        user.setUsername("user");
        Set<Image> images = new HashSet<>();
        images.add(new Image());
        given(userService.getUserDetails(user.getUsername())).willReturn(user);
        given(imageService.getImages(user)).willReturn(images);

        mockMvc.perform(get("/api/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser
    public void testDeleteImage() throws Exception {
        String imageId = "abcd1234";
        mockMvc.perform(delete("/api/deleteImage?imageid={imageId}", imageId))
                .andExpect(status().isAccepted());
    }
}
