package com.exacta.chapterbackend.repository;

import com.exacta.chapterbackend.model.Dev;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevRepository extends JpaRepository<Dev, Long> {
}
