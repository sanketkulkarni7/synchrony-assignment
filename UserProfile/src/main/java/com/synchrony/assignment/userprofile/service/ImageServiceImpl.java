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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Service class which interacts with IMGUR API's to upload, view and delete images
 *
 * @author sanketku
 */
@Service
public class ImageServiceImpl implements ImageService {

    public static final String IMGUR_URL = "https://api.imgur.com/3/image";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserDao userDao;
    private final ImageDao imageDao;
    private final RestTemplate restTemplate;
    @Value("${imgur.clientId}")
    private String clientId;


    public ImageServiceImpl(UserDao userDao, ImageDao imageDao, RestTemplateBuilder restTemplateBuilder) {
        this.userDao = userDao;
        this.imageDao = imageDao;
        this.restTemplate = restTemplateBuilder.errorHandler(new ImgurGatewayResponseErrorHandler()).build();
    }

    /**
     * This method calls img ur API to upload the image to imgur with provided image url
     *
     * @param imageUrl
     * @param user
     * @return
     */
    @Override
    public ImageResponse uploadImage(String imageUrl, User user) throws IOException {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Client-ID " + clientId);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        imageUrl = getBase64String(imageUrl);

        map.add("image", imageUrl);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ImageResponse imageResponse = new ImageResponse();

        ResponseEntity<ImageDetail> response = restTemplate.postForEntity(IMGUR_URL, request, ImageDetail.class);

        if ((response.getBody()).isSuccess()) {
            logger.info("Successfully received response {}", response.getBody().toString());
            Data data = response.getBody().getData();

            imageResponse.setUploadedImage(data.getLink());
            imageResponse.setImageId(data.getId());
            imageResponse.setSuccess("true");

            Image image = new Image(imageResponse.getImageId(), imageResponse.getUploadedImage(), user);
            HashSet<Image> images = new HashSet();
            imageDao.saveImage(image);
            images.add(image);
            user.setImages(images);
            userDao.save(user);

        } else {
            logger.error("Upload failed, Response {}", response.getBody());
            throw new ImgurGatewayException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Image Upload Failed");
        }
        return imageResponse;
    }

    /**
     * Fetches Image details from IMGUR based on image hash
     *
     * @param id
     * @return
     */
    @Override
    public ImageResponse getImage(String id, User user) {
        String url = IMGUR_URL + "/{imageid}";
        Set<Image> images = imageDao.findImageByUser(user);
        Optional<Image> image = images.stream().filter(img -> img.getImageid().equalsIgnoreCase(id)).findFirst();
        if (!image.isPresent()) {
            logger.error("Image with ID {} doesn't belong to logged in user with username {}", id, user.getUsername());
            throw new UnauthorizedUserException("User not authorized to get details of image");
        }

        HttpEntity<String> entity = getHttpEntity();
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("imageid", id);
        ResponseEntity<ImageDetail> response = restTemplate.exchange(url, HttpMethod.GET, entity, ImageDetail.class, uriVariables);
        ImageResponse imageResponse = new ImageResponse();
        if (Objects.nonNull(response.getBody())) {
            Data data = response.getBody().getData();
            imageResponse.setUploadedImage(data.getLink());
            imageResponse.setImageId(data.getId());
            imageResponse.setSuccess("true");
        } else {
            logger.error("Error occurred while fetching image details from imgur with image id {}", id);
            throw new ImgurGatewayException(response.getStatusCodeValue(), "Error occured while fetching image details from imgur");
        }
        return imageResponse;
    }

    /**
     * This method gets all the images uploaded by the given user
     *
     * @param user
     * @return
     */
    @Override
    public Set<Image> getImages(User user) {

        logger.info("Fetching the images associated with user with username {}", user.getUsername());
        Set<Image> images = imageDao.findImageByUser(user);
        return images;
    }

    /**
     * This method deletes the image based on imagehash by calling imgur delete API
     *
     * @param id
     * @param user
     */
    @Override
    public void deleteImage(String id, User user) {

        Set<Image> images = imageDao.findImageByUser(user);
        Optional<Image> image = images.stream().filter(img -> img.getImageid().equalsIgnoreCase(id)).findFirst();
        if (!image.isPresent()) {
            logger.error("Image with ID {} doesn't belong to logged in user with username {}", id, user.getUsername());
            throw new UnauthorizedUserException("User not authorized to delete image");
        }

        imageDao.deleteImage(image.get());
        String url = IMGUR_URL + "/{imageid}";

        HttpEntity<String> entity = getHttpEntity();
        Map<String, String> uriVariables = new HashMap<>();

        uriVariables.put("imageid", id);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, ImageDetail.class, uriVariables);
    }


    private String getBase64String(String link) throws IOException {

        byte[] byteData;
        String base64 = "";
        try (InputStream in = new URL(link).openStream()) {
            byteData = IOUtils.toByteArray(in);
            base64 = Base64.getEncoder().encodeToString(byteData);

        } catch (IOException e) {
            logger.error("Error occurred ", e);
            throw e;

        }

        return base64;
    }

    private HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Client-ID " + clientId);
        return new HttpEntity<>("parameters", headers);

    }
}

