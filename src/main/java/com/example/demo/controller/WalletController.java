package com.example.demo.controller;

import com.example.demo.model.request.PostHistoryRequest;
import com.example.demo.model.request.PostSaveRequest;
import com.example.demo.service.PostHistoryService;
import com.example.demo.service.PostSaveRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final PostHistoryService postHistoryService;
    private final PostSaveRecordService postSaveRecordService;

    @PostMapping("/save")
    public ResponseEntity<?> saveRecord(@RequestBody PostSaveRequest request) {

        if (request.getDatetime() == null || request.getAmount() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "datetime and amount are required"));
        }

        try {

            postSaveRecordService.execute(request);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("status", "ok"));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }

    }

    @PostMapping("/history")
    public ResponseEntity<?> history(@RequestBody PostHistoryRequest request) {

        if (request.getStartDatetime() == null || request.getEndDatetime() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "startDatetime and endDatetime are required"));
        }

        try {

            var result = postHistoryService.execute(request);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
