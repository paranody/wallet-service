package com.example.demo.repository;

import com.example.demo.model.entity.OutboxEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

@DataJpaTest
public class OutboxRepositoryTest {

    @Autowired
    OutboxRepository repository;

    @Test
    @DisplayName("Success save and get Outbox")
    void saveAndGetTrx() {
        var start = Instant.now().minusSeconds(100);
        var end = Instant.now().plusSeconds(100);

        var outbox = OutboxEvent.builder()
                .eventType("insert")
                .eventData("json-dummy")
                .build();

        repository.save(outbox);

        var result = repository.findAll();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("insert", result.get(0).getEventType());
    }
}
