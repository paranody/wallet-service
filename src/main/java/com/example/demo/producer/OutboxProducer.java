package com.example.demo.producer;

import com.example.demo.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutboxProducer {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        var events = outboxRepository.findByPublishedFalse(Limit.of(100));

        if (events.isEmpty()) return;

        System.out.println("Publishing " + events.size() + " events from outbox.");

        events.forEach(event -> {
            try {
                var topic = "wallet-events";

                kafkaTemplate.send(topic, event.getEventData())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                event.setPublished(true);
                                event.setPublishedAt(Instant.now());
                                outboxRepository.save(event);
                                System.out.println("Published event: " + event.getId());
                            } else {
                                System.out.println("Failed to publish event: " + event.getId() + " Error: " + ex);
                            }
                        });

            } catch (Exception ex) {
                System.out.println("Error to publish event: " + event.getId() + " Error: " + ex);
            }
        });
    }
}
