package com.exacta.chapterbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${aws.region.static}")
    private String region;

    @Value("${aws.sqs.secretAccessKey}")
    private String secretKey;

    @Value("${aws.sqs.accessKey}")
    private String accessKey;

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;

    @Value(value = "${aws.sqs.delaySeconds}")
    private int delaySeconds;

    public String getRegion() {
        return region;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

}
