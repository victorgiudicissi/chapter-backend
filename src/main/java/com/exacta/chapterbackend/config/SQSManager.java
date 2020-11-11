package com.exacta.chapterbackend.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class SQSManager {

    private final ApplicationConfig applicationConfig;
    private final AmazonSQS sqs;

    public SQSManager(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        sqs = AmazonSQSClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(generatorBasicAWSCredentials(config.getAccessKey(), config.getSecretKey())))
                .withRegion(config.getRegion()).build();

        this.queueUrl = config.getQueueUrl();
        this.deadLetterQueueUrl = config.getQueueDLQUrl();

        // Get dead-letter queue ARN
        GetQueueAttributesResult deadLetterQueueAttributes = sqs.getQueueAttributes(new GetQueueAttributesRequest(deadLetterQueueUrl).withAttributeNames("QueueArn"));

        String deadLetterQueueARN = deadLetterQueueAttributes.getAttributes()
                .get("QueueArn");

        //Set DLQ QueueArn
        SetQueueAttributesRequest queueAttributesRequest = new SetQueueAttributesRequest().withQueueUrl(queueUrl)
                .addAttributesEntry("RedrivePolicy", "{\"maxReceiveCount\":\"" + config.getMaxReceiveCount() + "\", " + "\"deadLetterTargetArn\":\"" + deadLetterQueueARN + "\"}");

        sqs.setQueueAttributes(queueAttributesRequest);

        execute.put(SQSOperationEnum.SHOP, b2WSqsExecute);
        execute.put(SQSOperationEnum.SUBA, b2WSqsExecute);
        execute.put(SQSOperationEnum.SOUB, b2WSqsExecute);
        execute.put(SQSOperationEnum.ACOM, b2WSqsExecute);
    }

    public void enqueue(String messageBody, Map<String, MessageAttributeValue> messageAttributes) {

        SendMessageRequest sendMessageStandardQueue = new SendMessageRequest().withQueueUrl(config.getQueueUrl())
                .withMessageBody(messageBody)
                .withMessageAttributes(messageAttributes);


        final SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageStandardQueue);

        if (sendMessageResult == null) {
            throw new BPayException(500, "api_gift", "Erro ao enviar a mensagem para a fila, " + queueUrl);
        }

    }

    @Scheduled(fixedRate = 1000)
    public Disposable getMessage() {

        final ReceiveMessageRequest receiveMessageRequest =
                new ReceiveMessageRequest(queueUrl)
                        .withMaxNumberOfMessages(10)
                        .withWaitTimeSeconds(1);

        final List<Message> messages = sqs.receiveMessage(receiveMessageRequest.withMessageAttributeNames(Constants.SQS_EXECUTER_GIFT))
                .getMessages();

        for (final Message message : messages) {
            String body = message.getBody();

            if (!"".equals(body)) {
                Gift gift = JsonUtil.fromJson(message.getBody(), Gift.class);

                MessageAttributeValue attributeValueExecutor = message.getMessageAttributes().get(Constants.SQS_EXECUTER_GIFT);

                if (attributeValueExecutor != null && attributeValueExecutor.getStringValue() != null && gift != null) {

                    final SQSOperationEnum sqsOperationEnum = SQSOperationEnum.valueOf(attributeValueExecutor.getStringValue());

                    return execute.get(sqsOperationEnum).execute(gift).flatMap(giftResponse -> {

                        if (Boolean.TRUE.equals(giftResponse.isExecuted())) {
                            final String messageReceiptHandle = message.getReceiptHandle();
                            sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
                        }
                        return Mono.empty();
                    }).subscribe();
                }
            }
        }
        return null;
    }

    private BasicAWSCredentials generatorBasicAWSCredentials(String accessKey, String secretKeyId) {
        return new BasicAWSCredentials(accessKey, secretKeyId);
    }
}
