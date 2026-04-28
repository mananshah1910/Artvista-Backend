package com.artgallery.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "artworks")
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    private Double price;
    
    @Column(length = 1000)
    private String history;
    
    private String medium;
    private String year;
    private String image;
    
    @Column(nullable = false)
    private String status; // pending, approved

    public Artwork() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }
    public String getMedium() { return medium; }
    public void setMedium(String medium) { this.medium = medium; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
