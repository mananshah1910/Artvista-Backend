package com.artgallery.backend.controller;

import com.artgallery.backend.model.Artwork;
import com.artgallery.backend.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    @Autowired
    private ArtworkRepository artworkRepository;

    @GetMapping
    public List<Artwork> getAllArtworks(@RequestParam(required = false) String status) {
        if (status != null) {
            return artworkRepository.findByStatus(status);
        }
        return artworkRepository.findAll();
    }

    @PostMapping
    public Artwork addArtwork(@RequestBody Artwork artwork) {
        if (artwork.getStatus() == null) artwork.setStatus("pending");
        return artworkRepository.save(artwork);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveArtwork(@PathVariable Long id) {
        return artworkRepository.findById(id)
                .map(artwork -> {
                    artwork.setStatus("approved");
                    artworkRepository.save(artwork);
                    return ResponseEntity.ok(artwork);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArtwork(@PathVariable Long id) {
        artworkRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
