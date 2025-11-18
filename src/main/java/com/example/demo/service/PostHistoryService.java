package com.example.demo.service;

import com.example.demo.config.AppConfig;
import com.example.demo.model.request.PostHistoryRequest;
import com.example.demo.model.response.PostHistoryResponse;
import com.example.demo.repository.InMemoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostHistoryService {

    private final InMemoryTransactionRepository repository;
    private final AppConfig appConfig;

    public List<PostHistoryResponse> execute(PostHistoryRequest request) {

        var start = request.getStartDatetime();
        var end = request.getEndDatetime();

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endDatetime must not be before startDatetime");
        }

        var startInstant = start.toInstant();
        var endInstant = end.toInstant();

        var startUtcHour = ZonedDateTime.ofInstant(startInstant, ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);
        var endUtcHour = ZonedDateTime.ofInstant(endInstant, ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);

        var result = new ArrayList<PostHistoryResponse>();

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
        var currentHour = startUtcHour;
        while (!currentHour.isAfter(endUtcHour)) {

            var currentHourInstant = currentHour.toInstant();

            var sum = repository.getTransaction(currentHourInstant).values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            var balance = appConfig.getInitialBalance() + sum;

            var odt = currentHour.toOffsetDateTime();

            result.add(
                    PostHistoryResponse.builder()
                            .datetime(odt.format(formatter))
                            .amount(balance)
                            .build()
            );

            currentHour = currentHour.plusHours(1);
        }

        return result;

    }

}
