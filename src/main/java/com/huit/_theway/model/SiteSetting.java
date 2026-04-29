package com.huit._theway.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "site_settings")
@Data
public class SiteSetting {
    
    @Id
    private Long id = 1L;

    // Slide 1
    private String slide1Image;
    private String slide1Eyebrow;
    private String slide1Title;
    private String slide1Subtitle;
    private String slide1LinkUrl;
    private String slide1LinkText;

    // Slide 2
    private String slide2Image;
    private String slide2Eyebrow;
    private String slide2Title;
    private String slide2Subtitle;
    private String slide2LinkUrl;
    private String slide2LinkText;

    // Categories
    private String category1Slug;
    private String category2Slug;

    // Optional: Section Titles
    private String category1Title;
    private String category1Subtitle;
    private String category2Title;
    private String category2Subtitle;
}
