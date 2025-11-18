package com.example.demo.service;

import com.example.demo.model.request.PostSaveRequest;
import com.example.demo.repository.InMemoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostSaveRecordService {

    private final InMemoryTransactionRepository repository;

    public void execute(PostSaveRequest request) {
        if (request.getAmount() < 0) {
            throw new IllegalArgumentException("amount can't be less than 0");
        }

        var odt = request.getDatetime().toInstant();

        repository.save(odt, request.getAmount());

    }
}
