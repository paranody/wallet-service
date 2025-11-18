package com.example.demo.service;

import com.example.demo.model.entity.OutboxEvent;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.request.PostSaveRequest;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PostSaveRecordService {

    private final TransactionRepository repository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void execute(PostSaveRequest request) {
        if (request.getAmount() < 0) {
            throw new IllegalArgumentException("amount can't be less than 0");
        }

        var odt = request.getDatetime().toInstant();

        var transaction = Transaction.builder()
                .datetime(odt)
                .amount(request.getAmount())
                .build();

        repository.save(transaction);

        publishEvent(request, odt);

    }

    private void publishEvent(PostSaveRequest request, Instant odt) {
        var eventData = new HashMap<>();
        eventData.put("amount", request.getAmount());
        eventData.put("datetime", odt);
        eventData.put("timestamp", Instant.now().toString());

        String eventJson;
        try {
            eventJson = objectMapper.writeValueAsString(eventData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);

        }

        var outboxEvent = OutboxEvent.builder()
                .eventType("insert")
                .eventData(eventJson)
                .createdAt(Instant.now())
                .build();

        outboxRepository.save(outboxEvent);
    }
}
