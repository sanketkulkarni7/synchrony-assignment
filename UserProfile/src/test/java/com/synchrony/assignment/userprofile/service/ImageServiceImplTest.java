package com.synchrony.assignment.userprofile.service;


import com.synchrony.assignment.userprofile.dao.ImageDao;
import com.synchrony.assignment.userprofile.dao.UserDao;
import com.synchrony.assignment.userprofile.dto.Data;
import com.synchrony.assignment.userprofile.dto.ImageDetail;
import com.synchrony.assignment.userprofile.dto.ImageResponse;
import com.synchrony.assignment.userprofile.exception.ImgurGatewayException;
import com.synchrony.assignment.userprofile.gateway.ImgurGatewayResponseErrorHandler;
import com.synchrony.assignment.userprofile.model.Image;
import com.synchrony.assignment.userprofile.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sanketku
 */
@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    private ImageServiceImpl imageService;

    private UserDao userDao;

    private ImageDao imageDao;


    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;

    private ResponseErrorHandler responseErrorHandler;

    @BeforeEach
    void setUp() {
        responseErrorHandler = mock(ResponseErrorHandler.class);
        restTemplateBuilder = mock(RestTemplateBuilder.class);
        restTemplate = mock(RestTemplate.class);
        imageDao = mock(ImageDao.class);
        userDao = mock(UserDao.class);
        when(restTemplateBuilder.errorHandler(any(ImgurGatewayResponseErrorHandler.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        imageService = new ImageServiceImpl(userDao, imageDao, restTemplateBuilder);
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testUploadImageSuccess() throws IOException {
        String imageUrl = "https://cdn.maikoapp.com/3d4b/4quqa/150.jpg";
        User user = new User();
        user.setUsername("testuser");
        ImageResponse expectedResponse = new ImageResponse();
        expectedResponse.setImageId("1234");
        expectedResponse.setUploadedImage("https://i.imgur.com/abcd.png");
        expectedResponse.setSuccess("true");

        ResponseEntity<ImageDetail> responseEntity = new ResponseEntity<>(new ImageDetail(new Data("1234", "https://i.imgur.com/abcd.png"), true, 1), HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(ImageDetail.class))).thenReturn(responseEntity);

        ImageResponse actualResponse = imageService.uploadImage(imageUrl, user);
        assertEquals(expectedResponse.getImageId(), actualResponse.getImageId());
        assertEquals(expectedResponse.getUploadedImage(), actualResponse.getUploadedImage());
        assertEquals(expectedResponse.getSuccess(), actualResponse.getSuccess());
    }

    @Test
    void testUploadImageFailure() throws IOException {
        String imageUrl = "https://cdn.maikoapp.com/3d4b/4quqa/150.jpg";
        User user = new User();
        user.setUsername("testuser");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(ImageDetail.class))).thenThrow(new ImgurGatewayException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Image Upload Failed"));

        assertThrows(ImgurGatewayException.class, () -> imageService.uploadImage(imageUrl, user));
    }

    @Test
    void testGetImageSuccess() {
        String imageId = "1234";
        User user = new User();
        user.setUsername("testuser");
        ImageResponse expectedResponse = new ImageResponse();
        expectedResponse.setImageId("1234");
        expectedResponse.setUploadedImage("https://i.imgur.com/abcd.png");
        expectedResponse.setSuccess("true");
        Image image = new Image(imageId, "https://i.imgur.com/abcd.png", user);
        Set<Image> images = new HashSet<>();
        images.add(image);
        when(imageDao.findImageByUser(user)).thenReturn(images);
        ResponseEntity<ImageDetail> responseEntity = new ResponseEntity<>(new ImageDetail(new Data("1234", "https://i.imgur.com/abcd.png"), true, 1), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(ImageDetail.class), anyMap())).thenReturn(responseEntity);
        ImageResponse actualResponse = imageService.getImage(imageId, user);
        assertEquals(expectedResponse.getImageId(), actualResponse.getImageId());
        assertEquals(expectedResponse.getUploadedImage(), actualResponse.getUploadedImage());
        assertEquals(expectedResponse.getSuccess(), actualResponse.getSuccess());

    }
}