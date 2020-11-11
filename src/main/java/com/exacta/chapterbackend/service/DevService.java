package com.exacta.chapterbackend.service;

import com.exacta.chapterbackend.model.Dev;
import com.exacta.chapterbackend.repository.DevRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DevService {
    private DevRepository devRepository;

    public DevService(DevRepository devRepository) {
        this.devRepository = devRepository;
    }

    public List<Dev> findAll() {
        return devRepository.findAll();
    }

    public Optional<Dev> findById(Long id) {
        return devRepository.findById(id);
    }

    public Dev save(Dev dev) {
        return devRepository.save(dev);
    }
}
