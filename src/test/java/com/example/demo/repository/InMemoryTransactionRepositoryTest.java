package com.example.demo.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class InMemoryTransactionRepositoryTest {

    @InjectMocks
    InMemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
    }

    @Test
    @DisplayName("Success save and get transactions")
    void saveAndGetTrx() {
        var now = Instant.now();

        repository.save(now, 100.0);

        var result = repository.getTransaction(now);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(100.0, result.firstEntry().getValue());
    }
}
