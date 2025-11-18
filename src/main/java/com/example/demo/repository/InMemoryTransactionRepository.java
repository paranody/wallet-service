package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
public class InMemoryTransactionRepository {

    private final ConcurrentSkipListMap<Instant, Double> transactions = new ConcurrentSkipListMap<>();

    public void save(Instant instant, Double amount) {
        transactions.merge(instant, amount, Double::sum);
    }

    public NavigableMap<Instant, Double> getTransaction(Instant end) {
        return transactions.headMap(end, true);
    }
}
