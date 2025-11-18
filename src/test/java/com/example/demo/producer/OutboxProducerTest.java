package com.example.demo.producer;

import com.example.demo.model.entity.OutboxEvent;
import com.example.demo.repository.OutboxRepository;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class OutboxProducerTest {

    @InjectMocks
    OutboxProducer outboxProducer;

    @Mock
    OutboxRepository outboxRepository;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @DisplayName("Success Produce Event")
    void success() throws Exception {
        var event = OutboxEvent.builder()
                .eventType("insert")
                .eventData("{\"test\":1}")
                .createdAt(Instant.now())
                .build();

        Mockito.doReturn(List.of(event))
                .when(outboxRepository)
                .findByPublishedFalse(Mockito.any());

        var future = new CompletableFuture<RecordMetadata>();
        future.complete(null);

        Mockito.doReturn(future)
                .when(kafkaTemplate)
                .send(Mockito.anyString(), Mockito.any());

        outboxProducer.publishPendingEvents();

        Mockito.verify(outboxRepository, Mockito.times(1))
                .save(Mockito.any());

        Mockito.verify(outboxRepository, Mockito.times(1))
                .findByPublishedFalse(Mockito.any());
    }

    @Test
    @DisplayName("Failed Produce Event")
    void failed_publishEvent() throws Exception {
        var event = OutboxEvent.builder()
                .eventType("insert")
                .eventData("{\"test\":1}")
                .createdAt(Instant.now())
                .build();

        Mockito.doReturn(List.of(event))
                .when(outboxRepository)
                .findByPublishedFalse(Mockito.any());

        var future = new CompletableFuture<RecordMetadata>();
        future.completeExceptionally(new RuntimeException("Kafka down"));

        Mockito.doReturn(future)
                .when(kafkaTemplate)
                .send(Mockito.anyString(), Mockito.any());

        outboxProducer.publishPendingEvents();

        Mockito.verify(outboxRepository, Mockito.times(1))
                .findByPublishedFalse(Mockito.any());
    }

    @Test
    @DisplayName("Error Produce Event")
    void error_publishEvent() throws Exception {
        var event = OutboxEvent.builder()
                .eventType("insert")
                .eventData("{\"test\":1}")
                .createdAt(Instant.now())
                .build();

        Mockito.doReturn(List.of(event))
                .when(outboxRepository)
                .findByPublishedFalse(Mockito.any());

        Mockito.doThrow(new RuntimeException("Error kafka"))
                .when(kafkaTemplate)
                .send(Mockito.anyString(), Mockito.any());

        outboxProducer.publishPendingEvents();

        Mockito.verify(outboxRepository, Mockito.times(1))
                .findByPublishedFalse(Mockito.any());
    }

}
