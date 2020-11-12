package com.exacta.chapterbackend.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.exacta.chapterbackend.model.Dev;
import com.exacta.chapterbackend.service.DevService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SQSManager {

    private final static String SQS_INCENTIVE_ATTRIBUTE = "SQS-DEV-ATTRIBUTE";

    private final ApplicationConfig applicationConfig;
    private final AmazonSQS sqs;
    private final DevService devService;
    private final String queueUrl;

    public SQSManager(ApplicationConfig applicationConfig, @Lazy DevService devService) {
        this.applicationConfig = applicationConfig;
        this.devService = devService;
        this.queueUrl = applicationConfig.getQueueUrl();

        sqs = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        generatorBasicAWSCredentials(applicationConfig.getAccessKey(), applicationConfig.getSecretKey()))
                )
                .withRegion(applicationConfig.getRegion())
                .build();
    }

    public void enqueue(Long devId) {

        String messageBody = "{ \"id\": \""+ devId +"\"}";

        SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
                .withQueueUrl(applicationConfig.getQueueUrl())
                .withMessageBody(messageBody)
                .withMessageAttributes(messageAttributes());

       sqs.sendMessage(sendMessageStandardQueue);
    }

    private Map<String, MessageAttributeValue> messageAttributes() {
        Map<String, MessageAttributeValue> messageAttribute = new HashMap<>();

        messageAttribute.put(
                SQS_INCENTIVE_ATTRIBUTE,
                new MessageAttributeValue().withDataType("String").withStringValue("DEV-TO-CONSUME")
        );

        return messageAttribute;
    }

    @Scheduled(fixedRate = 15000)
    public void processMessage() {
        final ReceiveMessageRequest receiveMessageRequest =
                new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(10).withWaitTimeSeconds(1);

        final List<Message> messages = sqs.receiveMessage(
                receiveMessageRequest.withMessageAttributeNames(SQS_INCENTIVE_ATTRIBUTE)
        ).getMessages();

        messages.stream().forEach(message -> {

            if (!StringUtils.isNullOrEmpty(message.getBody())) {
                Dev dev = Jackson.fromJsonString(message.getBody(), Dev.class);

                if (dev != null && dev.getId() != null) {
                    dev = devService.update(dev.getId());

                    if(dev.isHired()) {
                        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, message.getReceiptHandle()));
                    }
                }
            }
        });
    }

    private BasicAWSCredentials generatorBasicAWSCredentials(String accessKey, String secretKeyId) {
        return new BasicAWSCredentials(accessKey, secretKeyId);
    }
}
