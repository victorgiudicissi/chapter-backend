package com.exacta.chapterbackend.service;

import com.exacta.chapterbackend.model.Dev;

import java.util.List;
import java.util.Optional;

public interface DevService {
     Optional<Dev> findById(Long id);
     List<Dev> findAll();
     Dev save(Dev dev);
     Dev update(Long id);
}
