package com.example.demo.repository;

import com.example.demo.model.entity.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    TransactionRepository repository;

    @Test
    @DisplayName("Success save and get transactions")
    void saveAndGetTrx() {
        var start = Instant.now().minusSeconds(100);
        var end = Instant.now().plusSeconds(100);

        var trx = Transaction.builder()
                .datetime(Instant.now())
                .amount(10.0)
                .build();

        repository.save(trx);

        var result = repository.findByDatetimeBetween(start, end);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(10.0, result.get(0).getAmount());
    }
}
