package com.synchrony.assignment.userprofile.dao;

import com.synchrony.assignment.userprofile.model.Image;
import com.synchrony.assignment.userprofile.model.User;
import com.synchrony.assignment.userprofile.repository.ImageRespository;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author sanketku
 */
@Component
public class ImageDao {

    private ImageRespository imageRespository;

    public ImageDao(ImageRespository imageRespository) {
        this.imageRespository = imageRespository;
    }

    public void saveImage(Image image) {
        imageRespository.save(image);
    }

    public Set<Image> findImageByUser(User user) {
        return imageRespository.findByUser(user);
    }

    public void deleteImage(Image image) {
        imageRespository.delete(image);
    }

    public Set<Image> findByUser(User user) {
        return imageRespository.findByUser(user);
    }
}
