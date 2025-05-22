package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

    public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCreatedEvent for order id {}", orderId);

        try{
            PaymentRequestAvroModel paymentRequestAvroModel
                    = orderMessagingDataMapper.orderCreateEventToPaymentRequestAvroModel(domainEvent);

            kafkaProducer.send(
                    orderServiceConfigData.getPaymentRequestTopicName()
                    , orderId
                    , paymentRequestAvroModel
                    , getKafkaCallBack(orderServiceConfigData.getPaymentRequestTopicName(), paymentRequestAvroModel)
            );

            log.info("Sent OrderCreatedEvent for order id {}", orderId);
        } catch (Exception e) {
            log.error("Error sending OrderCreatedEvent for order id {}", orderId, e);
        }
    }

    private BiConsumer<SendResult<String, PaymentRequestAvroModel>, Throwable> getKafkaCallBack(
            String paymentRequestTopicName, PaymentRequestAvroModel paymentRequestAvroModel
    ) {
        return (result, throwable) -> {
            if (throwable != null) {
                log.error(
                        "Error occurred when sending payment request {} to topic {} ",
                        paymentRequestAvroModel.toString(),
                        paymentRequestTopicName,
                        throwable
                );
            }else{
                RecordMetadata metadata = result.getRecordMetadata();
                log.info(
                    "Received Successful response from Kafka for order id: {} Topic: {} Partition: {} OffSet: {} Timestamp:{}",
                        paymentRequestAvroModel.getOrderId(),
                        paymentRequestTopicName,
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp()
                );
            }
        };
    }
}
