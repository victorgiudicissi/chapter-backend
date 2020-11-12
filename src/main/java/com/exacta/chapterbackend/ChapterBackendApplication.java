package com.exacta.chapterbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChapterBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChapterBackendApplication.class, args);
	}

}
