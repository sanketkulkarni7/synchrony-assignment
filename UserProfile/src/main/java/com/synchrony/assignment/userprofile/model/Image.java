package com.synchrony.assignment.userprofile.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Image object entity contains main details about a Image
 *
 * @author sanketku
 */
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor

public class Image {

    /**
     * Unique identifier of the image
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;

    @Column
    private String imageid;
    @Column
    private String imageurl;
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    public Image(String imageId, String imageurl, User user) {
        this.imageid = imageId;
        this.imageurl = imageurl;
        this.user = user;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


}

