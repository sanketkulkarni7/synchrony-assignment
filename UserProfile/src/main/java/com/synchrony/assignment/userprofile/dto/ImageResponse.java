package com.synchrony.assignment.userprofile.dto;

import lombok.Data;

/**
 * Generic Image response which maps the imgur API response to the required fields
 *
 * @author sanketku
 */
@Data
public class ImageResponse {

    String uploadedImage;
    String imageId;

    String success;
    String error;

}
