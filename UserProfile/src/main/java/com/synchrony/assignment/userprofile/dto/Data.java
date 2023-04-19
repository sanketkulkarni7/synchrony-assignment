package com.synchrony.assignment.userprofile.dto;

import java.util.ArrayList;

/**
 * @author sanketku
 */
@lombok.Data
public class Data {

    public String id;
    public Object title;
    public Object description;
    public int datetime;
    public String type;
    public boolean animated;
    public int width;
    public int height;
    public int size;
    public int views;
    public int bandwidth;
    public Object vote;
    public boolean favorite;
    public Object nsfw;
    public Object section;
    public Object account_url;
    public int account_id;
    public boolean is_ad;
    public boolean in_most_viral;
    public ArrayList<Object> tags;
    public int ad_type;
    public String ad_url;
    public boolean in_gallery;
    public String deletehash;
    public String name;
    public String link;

    public Data(String id, String link) {
        this.id = id;
        this.link = link;
    }
}
