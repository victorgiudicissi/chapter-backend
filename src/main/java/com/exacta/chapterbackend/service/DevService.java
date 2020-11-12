package com.exacta.chapterbackend.service;

import com.exacta.chapterbackend.model.Dev;

import java.util.List;
import java.util.Optional;

public interface DevService {

    public Optional<Dev> findById(Long id);
    public List<Dev> findAll();
    public Dev save(Dev dev);

    public Dev update(Long id);
}
