package com.synchrony.assignment.userprofile.service;

import com.synchrony.assignment.userprofile.dto.ImageResponse;
import com.synchrony.assignment.userprofile.model.Image;
import com.synchrony.assignment.userprofile.model.User;

import java.io.IOException;
import java.util.Set;


/**
 * @author sanketku
 */
public interface ImageService {

    ImageResponse uploadImage(String imageUrl, User user) throws IOException;

    ImageResponse getImage(String id, User user);

    Set<Image> getImages(User user);

    void deleteImage(String id, User user);


}
