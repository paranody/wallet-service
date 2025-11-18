package com.example.demo.service;

import com.example.demo.config.AppConfig;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.request.PostHistoryRequest;
import com.example.demo.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PostHistoryServiceTest {

    @InjectMocks
    PostHistoryService postHistoryService;

    @Mock
    TransactionRepository repository;

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
                .findByDatetimeBetween(Mockito.any(), Mockito.any());

        var result = postHistoryService.execute(request);

        assertEquals(3, result.size());
        assertEquals(getTrx().get(0).getAmount(), result.get(0).getAmount());

        Mockito.verify(repository, Mockito.times(3)).findByDatetimeBetween(Mockito.any(), Mockito.any());

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

    private List<Transaction> getTrx() {
        var trx = Transaction.builder()
                .id(1L)
                .datetime(Instant.now())
                .amount(10.0)
                .build();

        return List.of(trx);
    }
}
