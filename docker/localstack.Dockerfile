FROM localstack/localstack

EXPOSE 4566

ENV SERVICES=sqs
ENV AWS_REGION=sa-east-1
ENV AWS_ACCESS_KEY_ID=teste-chapter
ENV AWS_SECRET_ACCESS_KEY=teste-chapter