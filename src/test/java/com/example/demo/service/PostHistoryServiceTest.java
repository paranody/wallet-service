package com.example.demo.service;

import com.example.demo.config.AppConfig;
import com.example.demo.model.request.PostHistoryRequest;
import com.example.demo.repository.InMemoryTransactionRepository;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PostHistoryServiceTest {

    @InjectMocks
    PostHistoryService postHistoryService;

    @Mock
    InMemoryTransactionRepository repository;

    @Mock
    AppConfig config;

    @Test
    @DisplayName("Success")
    void success() throws Exception {
        var request = PostHistoryRequest.builder()
                .startDatetime(OffsetDateTime.parse("2019-10-05T00:45:05+00:00"))
                .endDatetime(OffsetDateTime.parse("2019-10-05T02:45:05+00:00"))
                .build();

        Mockito.doReturn(getTrx())
                .when(repository)
                .getTransaction(Mockito.any());

        var result = postHistoryService.execute(request);

        assertEquals(3, result.size());
        assertEquals(getTrx().firstEntry().getValue(), result.get(0).getAmount());

        Mockito.verify(repository, Mockito.times(3)).getTransaction(Mockito.any());

    }

    @Test
    @DisplayName("Failed - Exception endDatetime is before startDatetime")
    void failed_endDatetimeIsBeforeStartDateTime() throws Exception {
        var request = PostHistoryRequest.builder()
                .startDatetime(OffsetDateTime.parse("2019-10-05T00:45:05+00:00"))
                .endDatetime(OffsetDateTime.parse("2019-10-04T02:45:05+00:00"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> postHistoryService.execute(request));
    }

    private NavigableMap<Instant, Double> getTrx() {
        var instant = OffsetDateTime.parse("2019-10-05T01:45:05+00:00").toInstant();

        NavigableMap<Instant, Double> map = new TreeMap<>();
        map.put(instant, 1000.0);

        return map;
    }
}
