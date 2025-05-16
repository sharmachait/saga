package com.food.ordering.system.kafka.producer.service.impl;

import com.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {
    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, K key, V message, BiConsumer<SendResult<K, V>, Throwable> callback) {
        log.info("Sending data to topic {} with key {} and message {}", topicName, key, message);
        try {
            CompletableFuture<SendResult<K, V>>  resultFuture = kafkaTemplate.send(topicName, key, message);
            // the key is used only to determine the partition of the topic the data needs to go to
            resultFuture.whenComplete(callback);
        } catch (KafkaException e) {
            log.error("Error sending to Kafka - Topic: {}, Key: {}", topicName, key, e); // Full exception logged
            callback.accept(null, e); // Notify callback of synchronous failure
            throw new KafkaProducerException(e.getMessage()); // Optional: throw if sync error handling needed
        }
//
//        Some errors occur immediately when calling kafkaTemplate.send(...), such as:
//
//        Invalid topic name
//
//        Serialization failure (e.g., non-serializable key/message)
//
//        Configuration errors (e.g., missing Kafka brokers in config)
//
//        These errors prevent the CompletableFuture from being created and must be caught synchronously
    }


    @PreDestroy
    public void close(){
        if(kafkaTemplate != null){
            log.info("Closing kafka producer");
            kafkaTemplate.destroy();
        }
    }
}
