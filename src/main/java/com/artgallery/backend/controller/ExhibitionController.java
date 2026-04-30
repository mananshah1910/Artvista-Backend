package com.artgallery.backend.controller;

import com.artgallery.backend.model.Exhibition;
import com.artgallery.backend.repository.ExhibitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    @GetMapping
    public List<Exhibition> getAllExhibitions() {
        return exhibitionRepository.findAll();
    }

    @PostMapping
    public Exhibition addExhibition(@RequestBody @org.springframework.lang.NonNull Exhibition exhibition) {
        return exhibitionRepository.save(exhibition);
    }

    @DeleteMapping("/{id}")
    public void deleteExhibition(@PathVariable @org.springframework.lang.NonNull Long id) {
        exhibitionRepository.deleteById(id);
    }
}
