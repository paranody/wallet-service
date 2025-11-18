package com.example.demo.service;

import com.example.demo.config.AppConfig;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.request.PostHistoryRequest;
import com.example.demo.model.response.PostHistoryResponse;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostHistoryService {

    private final TransactionRepository repository;
    private final AppConfig appConfig;

    public List<PostHistoryResponse> execute(PostHistoryRequest request) {

        var start = request.getStartDatetime();
        var end = request.getEndDatetime();

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endDatetime must not be before startDatetime");
        }

        var startInstant = start.toInstant();
        var endInstant = end.toInstant();

        var currentHour = ZonedDateTime.ofInstant(startInstant, ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);
        var endHour = ZonedDateTime.ofInstant(endInstant, ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);

        var result = new ArrayList<PostHistoryResponse>();

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
        var sum = 0.0;
        while (!currentHour.isAfter(endHour)) {

            var hourStart = currentHour.toInstant();
            var hourEnd = currentHour.plusHours(1).toInstant();

            var transactions = repository.findByDatetimeBetween(hourStart, hourEnd);

            sum = sum + transactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            var balance = appConfig.getInitialBalance() + sum;

            result.add(
                    PostHistoryResponse.builder()
                            .datetime(hourStart.atOffset(ZoneOffset.UTC).format(formatter))
                            .amount(balance)
                            .build()
            );

            currentHour = currentHour.plusHours(1);
        }

        return result;

    }

}
