package com.exacta.chapterbackend.controller;

import com.exacta.chapterbackend.model.Dev;
import com.exacta.chapterbackend.service.DevService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("chapter")
public class ChapterController {
    private final DevService devService;

    public ChapterController(DevService devService) {
        this.devService = devService;
    }

    @PostMapping
    public Dev create(@RequestBody Dev dev) {
        return this.devService.save(dev);
    }

    @GetMapping
    public List<Dev> findAll() {
        return this.devService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Dev> findById(@PathVariable Long id) {
        return this.devService.findById(id);
    }
}
