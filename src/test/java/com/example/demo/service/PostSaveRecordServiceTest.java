package com.example.demo.service;

import com.example.demo.model.request.PostSaveRequest;
import com.example.demo.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PostSaveRecordServiceTest {

    @InjectMocks
    PostSaveRecordService postSaveRecordService;

    @Mock
    InMemoryTransactionRepository repository;

    @Test
    @DisplayName("Success")
    void success() throws Exception {
        var request = PostSaveRequest.builder()
                .datetime(OffsetDateTime.parse("2019-10-05T16:45:05+07:00"))
                .amount(1.1)
                .build();

        var odt = request.getDatetime().toInstant();

        postSaveRecordService.execute(request);

        Mockito.verify(repository, Mockito.times(1))
                .save(odt, request.getAmount());
    }

    @Test
    @DisplayName("Failed amount less than zero")
    void failed_amountLessThanZero() throws Exception {
        var request = PostSaveRequest.builder()
                .datetime(OffsetDateTime.parse("2019-10-05T16:45:05+07:00"))
                .amount(-1.0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> postSaveRecordService.execute(request));
    }
}
