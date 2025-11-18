package com.example.demo.controller;

import com.example.demo.model.request.PostHistoryRequest;
import com.example.demo.model.request.PostSaveRequest;
import com.example.demo.model.response.PostHistoryResponse;
import com.example.demo.service.PostHistoryService;
import com.example.demo.service.PostSaveRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    @InjectMocks
    WalletController walletController;

    @Mock
    PostSaveRecordService postSaveRecordService;

    @Mock
    PostHistoryService postHistoryService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(walletController)
                .build();
    }

    @Test
    @DisplayName("Success - POST /api/v1/save")
    void postSaveSuccess() throws Exception {

        var request = PostSaveRequest.builder()
                .datetime(OffsetDateTime.parse("2019-10-05T16:45:05+07:00"))
                .amount(1.1)
                .build();

        Mockito.doNothing()
                .when(postSaveRecordService)
                .execute(Mockito.any(PostSaveRequest.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/wallet/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(postSaveRecordService, Mockito.times(1))
                .execute(Mockito.any(PostSaveRequest.class));
    }

    @Test
    @DisplayName("Failed Empty Request - POST /api/v1/save")
    void postSaveFailed_requestEmpty() throws Exception {

        var request = PostSaveRequest.builder()
                .datetime(OffsetDateTime.parse("2019-10-05T16:45:05+07:00"))
                .build();


        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/wallet/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Failed amount less than 0 - POST /api/v1/save")
    void postSaveFailed_failedParseDatetime() throws Exception {

        var request = PostSaveRequest.builder()
                .datetime(OffsetDateTime.parse("2019-10-05T16:45:05+07:00"))
                .amount(-1.0)
                .build();

        Mockito.doThrow(new IllegalArgumentException("amount can't be less than 0"))
                .when(postSaveRecordService)
                .execute(Mockito.any(PostSaveRequest.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/wallet/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Success - POST /api/v1/history")
    void postHistorySuccess() throws Exception {

        var request = PostHistoryRequest.builder()
                .startDatetime(OffsetDateTime.parse("2019-10-05T00:45:05+00:00"))
                .endDatetime(OffsetDateTime.parse("2019-10-05T12:45:05+00:00"))
                .build();

        var response = List.of(
                PostHistoryResponse.builder()
                        .datetime("2019-10-05T00:45:05+00:00")
                        .amount(1000.0)
                        .build()
        );

        Mockito.doReturn(response)
                .when(postHistoryService)
                .execute(Mockito.any(PostHistoryRequest.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/wallet/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(postHistoryService, Mockito.times(1))
                .execute(Mockito.any(PostHistoryRequest.class));
    }

    @Test
    @DisplayName("Failed Request Empty - POST /api/v1/history")
    void postHistoryFailed_requestEmpty() throws Exception {

        var request = PostHistoryRequest.builder()
                .startDatetime(OffsetDateTime.parse("2019-10-05T16:45:05+07:00"))
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/wallet/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Failed Parse Datetime - POST /api/v1/history")
    void postHistoryFailed_parseDatetime() throws Exception {

        var json = """
        {
            "startDatetime": "2019-10-05T16:45:05+07:00ID",
            "endDatetime": "2019-10-05T16:45:05+07:00ID"
        }
        """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/wallet/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(json))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Failed Illegal Argument - POST /api/v1/history")
    void postHistoryFailed_illegalArgument() throws Exception {

        var request = PostHistoryRequest.builder()
                .startDatetime(OffsetDateTime.parse("2019-10-05T16:45:05+07:00"))
                .endDatetime(OffsetDateTime.parse("2019-10-04T16:45:05+07:00"))
                .build();

        Mockito.doThrow(new IllegalArgumentException("endDatetime must not be before startDatetime"))
                .when(postHistoryService)
                .execute(Mockito.any(PostHistoryRequest.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/wallet/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
