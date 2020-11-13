package com.exacta.chapterbackend.service.impl;

import com.exacta.chapterbackend.config.SQSManager;
import com.exacta.chapterbackend.model.Dev;
import com.exacta.chapterbackend.repository.DevRepository;
import com.exacta.chapterbackend.service.DevService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DevServiceImpl implements DevService {

    private final DevRepository devRepository;
    private final SQSManager sqsManager;

    public DevServiceImpl(DevRepository devRepository, SQSManager sqsManager) {
        this.devRepository = devRepository;
        this.sqsManager = sqsManager;
    }

    @Override
    public List<Dev> findAll() {
        return devRepository.findAll();
    }

    @Override
    public Optional<Dev> findById(Long id) {
        return devRepository.findById(id);
    }

    @Override
    public Dev save(Dev dev) {
        devRepository.save(dev);
        sqsManager.enqueue(dev.getId());
        return dev;
    }

    @Override
    public Dev update(Long id) {

        Optional<Dev> dev = findById(id);
        dev.get().setHired(true);
        devRepository.save(dev.get());

        return dev.get();
    }
}
